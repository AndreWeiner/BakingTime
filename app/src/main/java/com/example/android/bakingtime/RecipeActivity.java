package com.example.android.bakingtime;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.Loader.RecipeLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity {

    private final static int RECIPE_LOADER = 43;

    @BindView(R.id.progress_view)
    ProgressBar mProgressView;
    @BindView(R.id.message_view)
    TextView mMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        LoaderManager loaderManager = getLoaderManager();
        Loader<ArrayList<Recipe>> recipeLoader = loaderManager.getLoader(RECIPE_LOADER);
        if (internetAvailable()) {
            mMessageView.setVisibility(View.GONE);
            if (recipeLoader == null) {
                loaderManager.initLoader(RECIPE_LOADER, new Bundle(), new RecipeLoader(this));
            } else {
                loaderManager.restartLoader(RECIPE_LOADER, new Bundle(), new RecipeLoader(this));
            }
        } else {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(getResources().getString(R.string.no_connection));
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mProgressView.setVisibility(View.GONE);
    }

    private boolean internetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
