package com.example.flashcardapp;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.flashcardapp.Activities.BottomNavActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeckMenuTests  {
    @Rule
    public ActivityTestRule<BottomNavActivity> activityRule = new ActivityTestRule<>(BottomNavActivity.class);

    /**
     * Test adding a new deck with no additional metadata.
     */
    @Test
    public void testAddDeckNoMetadata() {
        String deckName = "Test Deck";

        // Add the new deck
        onView(withId(R.id.add_deck_button)).perform(scrollTo(), click());
        onView(withId(R.id.LaunchDeckButton)).perform(click());

        // Type the title
        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();

        // Save the new deck
        onView(withId(R.id.save_deck_metadata)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));

        // Go back to menu
        onView(withId(R.id.save_deck_metadata)).perform(click());
        onView(withId(R.id.back_button)).perform(scrollTo(), click());

        // Check that deck button is there
        try {
            onView(withText(deckName)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            fail();
            return;
        }

        // Return to deck home
        onView(withText(deckName)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));
    }
}
