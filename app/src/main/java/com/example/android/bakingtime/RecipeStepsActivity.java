package com.example.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.Data.Step;
import com.example.android.bakingtime.Ui.StepInstructionFragment;
import com.example.android.bakingtime.Ui.StepListFragment;

public class RecipeStepsActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

    private final static String STEP_POSITION = "position";
    private final static String RECIPE_NAME = "recipe_name";
    private final static String INSTRUCTION = "instruction_fragment";
    private final static String LIST = "list_fragment";
    private Recipe mRecipe;
    private boolean mTwoPane;
    private int mCurrentPos;
    private StepInstructionFragment mStepInstruction;
    private StepListFragment mStepList;
    private android.support.v4.app.FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRecipe = getIntent().getExtras().getParcelable(Recipe.class.getSimpleName());
            mCurrentPos = 0;
        }

        mTwoPane = findViewById(R.id.two_pane_linear_layout) != null;

        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(Recipe.class.getSimpleName());
            mStepList = (StepListFragment) mFragmentManager
                    .getFragment(savedInstanceState, LIST);
            mStepList.setTwoPane(mTwoPane);

            if (mTwoPane) {
                mStepInstruction = (StepInstructionFragment) mFragmentManager
                        .getFragment(savedInstanceState, INSTRUCTION);
                mCurrentPos = savedInstanceState.getInt(STEP_POSITION);
                mStepList.setPosition(mCurrentPos);
            }
        } else {
            if (mTwoPane) {
                createTwoPaneLayout();
            } else {
                createOnePaneLayout();
            }
        }
        setTitle(mRecipe.getName());
    }

    private void createOnePaneLayout() {
        mStepList = new StepListFragment();
        mStepList.setRecipe(mRecipe);
        mStepList.setTwoPane(mTwoPane);
        mFragmentManager.beginTransaction()
                .add(R.id.list_container, mStepList)
                .commit();
    }

    private void createTwoPaneLayout() {
        mStepList = new StepListFragment();
        mStepList.setRecipe(mRecipe);
        mStepList.setPosition(0);
        mStepList.setTwoPane(mTwoPane);
        mFragmentManager.beginTransaction()
                .add(R.id.twop_list_container, mStepList)
                .commit();
        mStepInstruction = new StepInstructionFragment();
        mStepInstruction.setSteps(mRecipe.getSteps());
        mStepInstruction.setPosition(mCurrentPos);
        mStepInstruction.setTwoPane(mTwoPane);
        mFragmentManager.beginTransaction()
                .add(R.id.twop_instruction_container, mStepInstruction)
                .commit();
    }

    @Override
    public void onStepSelected(View view, int position) {
        if (mTwoPane) {
            mCurrentPos = position;
            mStepInstruction.setPosition(position);
            mStepList.setPosition(position);
            android.support.v4.app.FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.detach(mStepInstruction);
            ft.attach(mStepInstruction);
            ft.commit();
        } else {
            Intent intent = new Intent(this, RecipeSingleStepActivity.class);
            intent.putParcelableArrayListExtra(Step.class.getSimpleName(), mRecipe.getSteps());
            intent.putExtra(STEP_POSITION, position);
            intent.putExtra(RECIPE_NAME, mRecipe.getName());
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Recipe.class.getSimpleName(), mRecipe);
        getSupportFragmentManager().putFragment(outState, LIST, mStepList);
        if (mTwoPane) {
            getSupportFragmentManager().putFragment(outState, INSTRUCTION, mStepInstruction);
            outState.putInt(STEP_POSITION, mCurrentPos);
        }
    }
}
