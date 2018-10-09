package com.example.android.bakingtime.Loader;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.bakingtime.Data.DataUtils;
import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.R;

import java.util.ArrayList;

public class RecipeAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    private ArrayList<Recipe> mRecipes;
    private ProgressBar mProgressView;

    public RecipeAsyncTaskLoader(Context context) {
        super(context);
        if (mRecipes == null) {
            mProgressView = ((Activity) context).findViewById(R.id.progress_view);
            forceLoad();
        } else {
            deliverResult(mRecipes);
        }

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        mRecipes = DataUtils.getRecipes();
        return mRecipes;
    }
}
