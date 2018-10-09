package com.example.android.bakingtime.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class to store recipe information
 */
public class Recipe implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    private final static String BULLET_UNICODE = "\u2022";
    private int mId;
    private String mName;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private int mServings;
    private String mImageUrl;

    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps,
                  int servings, String imageUrl) {
        mId = id;
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImageUrl = imageUrl;
    }

    public Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mServings = in.readInt();
        mImageUrl = in.readString();
        mIngredients = in.createTypedArrayList(Ingredient.CREATOR);
        mSteps = in.createTypedArrayList(Step.CREATOR);
    }

    public String getId() {
        return Integer.toString(mId);
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Step> getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String buildIngredientsList() {
        String list = "";
        for (Ingredient ing : mIngredients) {
            list += BULLET_UNICODE + " " + ing.getIngredient() + ", "
                    + ing.getQuantity() + " " + ing.getMeasure() + "\n";
        }
        return list.trim();
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeInt(mServings);
        parcel.writeString(mImageUrl);
        parcel.writeTypedList(mIngredients);
        parcel.writeTypedList(mSteps);
    }
}
