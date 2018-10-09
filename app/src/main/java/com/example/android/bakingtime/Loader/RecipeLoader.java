package com.example.android.bakingtime.Loader;

import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingtime.Adapter.RecipeAdapter;
import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.IngredientsListWidget;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeStepsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeLoader implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    Context mContext;
    @BindView(R.id.progress_view)
    ProgressBar mProgressView;
    @BindView(R.id.message_view)
    TextView mMessageView;
    @BindView(R.id.recipe_grid_view)
    GridView mGridView;

    public RecipeLoader(Context context) {
        mContext = context;
        ButterKnife.bind(this, (Activity) context);
    }

    public static void updateIngredientsWidget(Context context, Recipe recipe) {
        Intent intent = new Intent(context, IngredientsListWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(IngredientsListWidget.RECIPE_SERVICE, recipe);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, IngredientsListWidget.class));
        if (ids != null && ids.length > 0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
        }

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new RecipeAsyncTaskLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, final ArrayList<Recipe> recipes) {
        mProgressView.setVisibility(View.GONE);
        if (recipes == null) {
            mMessageView.setText(mContext.getResources().getText(R.string.no_recipes));
        } else {
            mGridView.setAdapter(new RecipeAdapter(mContext, recipes));
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    updateIngredientsWidget(mContext, recipes.get(position));
                    Intent intent = new Intent(mContext, RecipeStepsActivity.class);
                    intent.putExtra(Recipe.class.getSimpleName(), recipes.get(position));
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }
}
