package com.example.android.bakingtime.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        mContext = context;
        mRecipes = recipes;
    }

    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final Recipe recipe = mRecipes.get(position);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.recipe_card, null);
        }

        final ImageView thumbnail = view.findViewById(R.id.recipe_image_view);
        final TextView name = view.findViewById(R.id.recipe_name_view);
        final TextView description = view.findViewById(R.id.recipe_description_view);

        name.setText(recipe.getName());
        int ser = recipe.getServings();
        String servings = mContext.getResources()
                .getQuantityString(R.plurals.servings, ser, ser);
        description.setText(servings);
        String thumbnailUrl = recipe.getImageUrl();
        if (thumbnailUrl.equals("")) {
            thumbnail.setImageResource(R.drawable.recipe_120);
        } else {
            Picasso.with(mContext).load(recipe.getImageUrl())
                    .placeholder(R.drawable.recipe_120)
                    .error(R.drawable.recipe_120)
                    .into(thumbnail);
        }

        return view;
    }
}
