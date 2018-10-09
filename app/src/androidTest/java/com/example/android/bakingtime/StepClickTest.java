package com.example.android.bakingtime;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingtime.Data.Ingredient;
import com.example.android.bakingtime.Data.Recipe;
import com.example.android.bakingtime.Data.Step;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class StepClickTest {
    @Rule
    public ActivityTestRule<RecipeStepsActivity> mStepsTestRule
            = new ActivityTestRule<>(RecipeStepsActivity.class, true, false);
    private boolean mIsScreenSw600dp;

    @Before
    public void initIntents() {
        ArrayList<Step> steps = new ArrayList<>();
        steps.add(new Step(0, "Introduction", "firstUrl", "secondUrl", "thirdUrl"));
        Recipe recipe = new Recipe(0, "testRecipe", new ArrayList<Ingredient>(),
                steps, 2, "testUrl");
        Intent i = new Intent();
        i.putExtra(Recipe.class.getSimpleName(), recipe);
        mStepsTestRule.launchActivity(i);
        Intents.init();
    }

    @Test
    public void clickStep_OpenInstructionActivity() {
        onView(withId(R.id.steps_list_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Intents.intended(hasComponent(RecipeSingleStepActivity.class.getName()));
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }
}
