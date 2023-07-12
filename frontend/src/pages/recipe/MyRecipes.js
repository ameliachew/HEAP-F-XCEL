import { Fragment, useState } from "react";
import HeadingOne from "../../components/common/heading/HeadingOne";
import TextIconButton from "../../components/common/button/TextIconButton";
import { useLoaderData, useNavigate } from "react-router-dom";
import Grid from "@mui/material/Grid";
import RecipeCard from "../../components/recipe/card/RecipeCard";

const MyRecipes = () => {
    const navigate = useNavigate();
    const { recipes } = useLoaderData();

    return (
        <Fragment>
            <HeadingOne divider={true}>My Recipes</HeadingOne>
            <Grid container direction="row-reverse" alignItems="flex-end">
                <TextIconButton
                    type="primary"
                    onClick={() => navigate("/create-recipe")}
                >
                    Create
                </TextIconButton>
            </Grid>

            <br />
            <Grid container direction="row" columnGap={4} rowGap={5}>
                {recipes.map((recipe, index) => (
                    <Grid item>
                        <RecipeCard key={index} recipe={recipe} />
                    </Grid>
                ))}
            </Grid>
        </Fragment>
    );
};

export default MyRecipes;
