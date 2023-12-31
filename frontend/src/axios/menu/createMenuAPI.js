import Cookies from "js-cookie";
import { protectedInstance as instance } from "../instance";
import handleImageUpload from "../../functions/uploadImage";

const createMenu = async (data) => {
    try {
        const { image, ...other } = data;
        const imgUrl = await handleImageUpload(
            image,
            `menu/${other.name}/menu_image${Date.now()}`
        );

        const result = await instance.post(
            `/user/createMenu`,
            { ...other, image: imgUrl },
            {
                headers: { Authorization: `Bearer ${Cookies.get("token")}` },
                // headers: {
                //     Authorization: `Bearer ${"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZXNseS5jaGF1LjIwMjJAc2Npcy5zbXUuZWR1LnNnIiwiaWF0IjoxNjg5Njk4MDkzLCJleHAiOjE2ODk3ODQ0OTN9.u0fM6KsZQ9tJbYSe1BWw3mgRndBIBBORM_TLrzviGb4"}`,
                // },
            }
        );
        return result.data.response;
    } catch (e) {
        let msg = "";
        if (!e.response) {
            msg =
                "Create menu unsuccessful due to network error!\nPlease check your internet connection!";
        } else {
            const { error, message } = e.response.data;
            msg = error + "\n" + message;
        }

        throw msg;
    }
};

export default createMenu;
