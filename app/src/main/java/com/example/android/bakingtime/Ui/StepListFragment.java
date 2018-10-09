package com.example.android.bakingtime.Ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.Adapter.StepAdapter;
import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepListFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.steps_list_view)
    RecyclerView stepsList;
    @BindView(R.id.ingredients_view)
    TextView ingredientsList;
    OnStepClickListener mCallbacks;
    private Recipe mRecipe;
    private int mCurrentPos;
    private boolean mTwoPane;

    public StepListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_list_fragment, container, false);
        ButterKnife.bind(this, rootView);
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(Recipe.class.getSimpleName());
        }
        if (mRecipe != null) {
            ingredientsList.setText(mRecipe.buildIngredientsList());
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            stepsList.setLayoutManager(manager);
            stepsList.setAdapter(new StepAdapter(mRecipe.getSteps(), mCallbacks, mCurrentPos, mTwoPane));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Recipe.class.getSimpleName(), mRecipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setPosition(int position) {
        mCurrentPos = position;
    }

    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    public interface OnStepClickListener {
        void onStepSelected(View view, int position);
    }
}
