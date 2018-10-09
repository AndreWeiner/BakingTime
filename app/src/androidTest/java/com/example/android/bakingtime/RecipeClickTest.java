package com.example.android.bakingtime;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class RecipeClickTest {
    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule
            = new ActivityTestRule<>(RecipeActivity.class);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @Test
    public void clickRecipeCard_OpenRecipeStepsActivity() {

        onData(anything()).inAdapterView(withId(R.id.recipe_grid_view)).atPosition(0).
                onChildView(withId(R.id.recipe_image_view)).perform(click());
        Intents.intended(hasComponent(RecipeStepsActivity.class.getName()));
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }
}
