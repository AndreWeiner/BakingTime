package com.example.android.bakingtime.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class to fetch recipes from server
 */
public final class DataUtils {

    private static final String JSON_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private final static String DELIMITER = "\\A";
    private final static String RECIPE_ID = "id";
    private final static String RECIPE_NAME = "name";
    private final static String RECIPE_INGREDIENTS = "ingredients";
    private final static String RECIPE_SERVINGS = "servings";
    private final static String RECIPE_IMAGE = "image";
    private final static String INGREDIENT_QUANTITY = "quantity";
    private final static String INGREDIENT_MEASURE = "measure";
    private final static String INGREDIENT_NAME = "ingredient";
    private final static String RECIPE_STEPS = "steps";
    private final static String STEP_ID = "id";
    private final static String STEP_SHORT_DESCRIPTION = "shortDescription";
    private final static String STEP_DESCRIPTION = "description";
    private final static String STEP_VIDEO = "videoURL";
    private final static String STEP_THUMBNAIL = "thumbnailURL";


    private DataUtils() {
    }

    public static ArrayList<Recipe> getRecipes() {
        String httpResponse = null;
        try {
            httpResponse = getHttpResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return parseRecipeJson(httpResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getHttpResponse() throws IOException {
        URL url = null;
        try {
            url = new URL(JSON_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(DELIMITER);
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    private static ArrayList<Recipe> parseRecipeJson(String recipeJson) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(recipeJson);

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recipe = jsonArray.getJSONObject(i);
                recipes.add(new Recipe(
                        recipe.getInt(RECIPE_ID),
                        recipe.getString(RECIPE_NAME),
                        parseIngredients(recipe.getJSONArray(RECIPE_INGREDIENTS)),
                        parseSteps(recipe.getJSONArray(RECIPE_STEPS)),
                        recipe.getInt(RECIPE_SERVINGS),
                        recipe.getString(RECIPE_IMAGE)
                ));
            }

        }
        return recipes;
    }

    private static ArrayList<Ingredient> parseIngredients(JSONArray ingredientsJson) throws JSONException {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsJson != null) {
            for (int i = 0; i < ingredientsJson.length(); i++) {
                JSONObject ing = ingredientsJson.getJSONObject(i);
                ingredients.add(new Ingredient(
                        ing.getInt(INGREDIENT_QUANTITY),
                        ing.getString(INGREDIENT_MEASURE),
                        ing.getString(INGREDIENT_NAME)
                ));
            }
        }
        return ingredients;
    }

    private static ArrayList<Step> parseSteps(JSONArray stepsJson) throws JSONException {
        ArrayList<Step> steps = new ArrayList<>();
        if (stepsJson != null) {
            for (int i = 0; i < stepsJson.length(); i++) {
                JSONObject step = stepsJson.getJSONObject(i);
                steps.add(new Step(
                        step.getInt(STEP_ID),
                        step.getString(STEP_SHORT_DESCRIPTION),
                        step.getString(STEP_DESCRIPTION),
                        step.getString(STEP_VIDEO),
                        step.getString(STEP_THUMBNAIL)
                ));
            }
        }
        return steps;
    }

}
