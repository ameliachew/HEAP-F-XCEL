package com.heap.backend.service.auth;

import com.heap.backend.data.request.*;
import com.heap.backend.data.response.*;
import com.heap.backend.models.*;
import com.heap.backend.repository.MenuRepository;
import com.heap.backend.repository.RecipeRepository;
import com.heap.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;

    public Response create(CreateMenuRequest request, String oldEmail) {

        try {
            User origUser = userRepository.findByEmail(oldEmail).orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
            String id = origUser.getId();

            //Checks through Items in the Menu to see if these are legit recipe in entries
            MenuSection[] menuSections = request.getSections();
            StoredMenuSection[] storedMenuSections = new StoredMenuSection[menuSections.length];

            for (int i = 0 ; i < menuSections.length ; i++) {

                MenuSection ms = menuSections[i];
                storedMenuSections[i].setName(ms.getName());

                for (int j = 0 ; j < ms.getItems().length ; j++) {

                    Item item = ms.getItems()[j];

                    if (recipeRepository.findByNameAndUserId(item.getItem(), id).isEmpty()) {

                        throw new IllegalArgumentException("Missing Recipe");

                    } else {

                        storedMenuSections[i].getItems()[j] =
                                recipeRepository.findByNameAndUserId(item.getItem(), id)
                                .orElseThrow(() -> new IllegalArgumentException("Missing Recipe")).getId();

                    }

                }
            }

            //Search through the menu ArrayList of Specific user and checks if a similar menu is present
            if (menuRepository.findByUserIdAndName(id, request.getName()).isPresent()) {

                throw new IllegalArgumentException("Duplicate Menu");

            }

            //If everything goes well, create StoredMenu and add it into the MenuService
            StoredMenu storedMenu = StoredMenu.builder()
                    .userId(id)
                    .name(request.getName())
                    .type(request.getType())
                    .sections(storedMenuSections)
                    .build();


            menuRepository.save(storedMenu);

        } catch (IllegalArgumentException e) {

            //If user cannot be found in the repository based on token obtained info, return ErrorResponse
            if ("Invalid Token".equals(e.getMessage())) {

                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Token")
                        .message("User not found")
                        .build();

            } else if ("Repeat Menu".equals(e.getMessage())) {

                return ErrorResponse.builder()
                        .error("Bad Request: Duplicate Menu")
                        .message("Please try another name for the recipe")
                        .build();

            } else {

                return ErrorResponse.builder()
                        .error("Bad Request: Recipe does not exists")
                        .message("Please ensure that items in menu have a recipe")
                        .build();

            }

        } catch (Exception e) {

            //Catches any other form of exception as unknown error
            return ErrorResponse.builder()
                    .error("Internal Server Error")
                    .message("Unknown Error")
                    .build();
        }

        return SuccessResponse.builder()
                .response("Menu has been created successfully")
                .build();
    }

    public Response delete(DeleteMenuRequest request, String oldEmail) {

        try {

            User origUser = userRepository.findByEmail(oldEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
            String id = origUser.getId();

            if (menuRepository.findByUserIdAndName(id, request.getName()).isEmpty()) {

                throw new IllegalArgumentException("No Such Menu");

            }

            menuRepository.deleteByUserIdAndName(id, request.getName());

        } catch (IllegalArgumentException e) {

            if ("Invalid Token".equals(e.getMessage())) {

                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Token")
                        .message("User not found")
                        .build();

            } else {
                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Menu")
                        .message("Menu not found")
                        .build();

            }

        } catch (Exception e) {

            //Catches any other form of exception as unknown error
            return ErrorResponse.builder()
                    .error("Internal Server Error")
                    .message("Unknown Error")
                    .build();
        }

        return SuccessResponse.builder()
                .response("Menu has been deleted successfully")
                .build();
    }

    public Response update(String menuId, UpdateMenuRequest request, String oldEmail) {

        try {

            User origUser = userRepository.findByEmail(oldEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
            String id = origUser.getId();

            StoredMenu storedMenu = menuRepository.findByIdAndUserId(menuId, id)
                    .orElseThrow(() -> new IllegalArgumentException("No such Menu"));

            storedMenu.setName(request.getName());
            storedMenu.setType(request.getType());

            //Checks through Items in the Menu to see if these are legit recipe in entries
            MenuSection[] menuSections = request.getSections();
            StoredMenuSection[] storedMenuSections = new StoredMenuSection[menuSections.length];

            for (int i = 0 ; i < menuSections.length ; i++) {

                MenuSection ms = menuSections[i];
                storedMenuSections[i].setName(ms.getName());

                for (int j = 0 ; j < ms.getItems().length ; j++) {

                    Item item = ms.getItems()[j];

                    if (recipeRepository.findByNameAndUserId(item.getItem(), id).isEmpty()) {

                        throw new IllegalArgumentException("Missing Recipe");

                    } else {

                        storedMenuSections[i].getItems()[j] =
                                recipeRepository.findByNameAndUserId(item.getItem(), id)
                                        .orElseThrow(() -> new IllegalArgumentException("Missing Recipe")).getId();

                    }

                }
            }

            storedMenu.setSections(storedMenuSections);

            menuRepository.save(storedMenu);

        } catch (IllegalArgumentException e) {

            if (e.getMessage().equals("Invalid Token")) {

                //If user cannot be found in the repository based on token obtained info, return ErrorResponse
                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Token")
                        .message("User not found")
                        .build();

            }

            return  ErrorResponse.builder()
                    .error("Bad Request: Invalid Recipe")
                    .message("Recipe not found")
                    .build();


        } catch (Exception e) {

            //Catches any other form of exception as unknown error
            return ErrorResponse.builder()
                    .error("Internal Server Error: Unknown Error")
                    .message("An unknown error has occurred! Do try again!")
                    .build();

        }

        return SuccessResponse.builder()
                .response("Menu has been updated successfully")
                .build();
    }

    public Response findOne(FindMenuRequest request, String oldEmail) {

        ReturnedMenu returnedMenu = null;

        try {

            User origUser = userRepository.findByEmail(oldEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
            String id = origUser.getId();

            if (menuRepository.findByUserIdAndName(id, request.getName()).isEmpty()) {

                throw new IllegalArgumentException("User has no such Menu");

            }

            StoredMenu storedMenu = menuRepository.findByUserIdAndName(id, request.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User has no such Menu"));

            returnedMenu = ReturnedMenu.builder()
                    .id(storedMenu.getId())
                    .name(storedMenu.getName())
                    .userId(storedMenu.getUserId())
                    .type(storedMenu.getType())
                    .build();

            ReturnedMenuSection[] returnedMenuSections = new ReturnedMenuSection[storedMenu.getSections().length];

            StoredMenuSection[] storedMenuSections = storedMenu.getSections();
            for (int i = 0 ; i < storedMenuSections.length ; i++) {

                String[] items = storedMenuSections[i].getItems();

                for (int j = 0 ; j < items.length ; j++) {

                    returnedMenuSections[i].getItems()[j] = recipeRepository
                            .findByUserIdAndId(id, items[j])
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Recipe"));

                }
            }

            returnedMenu.setSections(returnedMenuSections);

        } catch (IllegalArgumentException e) {

            if (e.getMessage().equals("Invalid Token")) {

                //If user cannot be found in the repository based on token obtained info, return ErrorResponse
                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Token")
                        .message("User not found")
                        .build();

            } else {

                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Menu Name")
                        .message("Menu Name not found")
                        .build();

            }

        } catch (Exception e) {

            //Catches any other form of exception as unknown error
            return ErrorResponse.builder()
                    .error("Internal Server Error: Unknown Error")
                    .message("An unknown error has occurred! Do try again!")
                    .build();

        }

        return SingleMenuResponse.builder()
                .returnedMenu(returnedMenu)
                .build();

    }

    public Response findAll(String oldEmail) {

        List<ReturnedMenu> menus = new ArrayList<>();

        try {

            User origUser = userRepository.findByEmail(oldEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
            String id = origUser.getId();

            List<StoredMenu> storedMenuList = menuRepository.findAllByUserId(id);

            for (StoredMenu sm : storedMenuList) {

                StoredMenuSection[] storedMenuSections = sm.getSections();
                ReturnedMenu rm = ReturnedMenu.builder()
                        .id(sm.getId())
                        .type(sm.getType())
                        .userId(sm.getUserId())
                        .name(sm.getName())
                        .build();

                ReturnedMenuSection[] rms = new ReturnedMenuSection[sm.getSections().length];

                for (int i = 0 ; i < storedMenuSections.length ; i++) {

                    StoredMenuSection sms = storedMenuSections[i];
                    String[] items = sms.getItems();
                    Recipe[] recipes = new Recipe[items.length];

                    for (int j = 0 ; j < items.length ; j++) {

                        recipes[j] = recipeRepository
                                .findByUserIdAndName(id, items[j])
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Recipe"));

                    }

                    rms[i].setItems(recipes);

                }

                rm.setSections(rms);
            }

        } catch (IllegalArgumentException e) {

            if (e.getMessage().equals("Invalid Token")) {

                //If user cannot be found in the repository based on token obtained info, return ErrorResponse
                return ErrorResponse.builder()
                        .error("Bad Request: Invalid Token")
                        .message("User not found")
                        .build();

            }

        } catch (Exception e) {

            //Catches any other form of exception as unknown error
            return ErrorResponse.builder()
                    .error("Internal Server Error: Unknown Error")
                    .message("An unknown error has occurred! Do try again!")
                    .build();

        }

        return MultipleMenuResponse.builder()
                .returnedMenus(menus)
                .build();

    }
}
