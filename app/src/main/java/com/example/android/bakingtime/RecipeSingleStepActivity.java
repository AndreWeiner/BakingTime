package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.Data.Step;
import com.example.android.bakingtime.Ui.StepInstructionFragment;

import java.util.ArrayList;

public class RecipeSingleStepActivity extends AppCompatActivity {
    private final static String STEP_POSITION = "position";
    private final static String STEPS = "steps";
    private final static String RECIPE_NAME = "recipe_name";
    private final static String INSTRUCTION = "instruction";
    private ArrayList<Step> mSteps;
    private String mName;
    private int mCurrentPos;
    private StepInstructionFragment mInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            mCurrentPos = savedInstanceState.getInt(STEP_POSITION);
            mName = savedInstanceState.getString(RECIPE_NAME, "");
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            mInstruction = (StepInstructionFragment) fragmentManager.getFragment(savedInstanceState, INSTRUCTION);
        } else {
            mCurrentPos = getIntent().getIntExtra(STEP_POSITION, 0);
            mName = getIntent().getStringExtra(RECIPE_NAME);
            mSteps = getIntent().getParcelableArrayListExtra(Step.class.getSimpleName());
            mInstruction = new StepInstructionFragment();
            mInstruction.setSteps(mSteps);
            mInstruction.setPosition(mCurrentPos);
            mInstruction.setTwoPane(false);
            fragmentManager.beginTransaction()
                    .add(R.id.step_instruction_container, mInstruction)
                    .commit();
        }
        setContentView(R.layout.activity_recipe_single_step);
        getSupportActionBar().show();
        setTitle(mName);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_POSITION, mCurrentPos);
        outState.putString(RECIPE_NAME, mName);
        outState.putParcelableArrayList(STEPS, mSteps);
        getSupportFragmentManager().putFragment(outState, INSTRUCTION, mInstruction);
    }


}
