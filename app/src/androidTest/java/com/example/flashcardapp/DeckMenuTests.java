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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
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

        // Delete the deck
        onView(withId(R.id.delete_button)).perform(scrollTo(), click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Check that delete was successful
        onView(withText(deckName)).check(doesNotExist());
    }

    /**
     * Test adding a new deck with course and school data.
     */
    @Test
    public void testAddDeckMetadata() {
        String deckName = "Test Deck";
        String courseName = "CSE 5236";
        String schoolName = "OSU";

        // Add the new deck
        onView(withId(R.id.add_deck_button)).perform(scrollTo(), click());
        onView(withId(R.id.LaunchDeckButton)).perform(click());

        // Type the title, course and school
        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_course_name)).perform(typeText(courseName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_school_name)).perform(typeText(schoolName));
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
        onView(withId(R.id.course_name_label)).check(matches(withText(courseName)));
        onView(withId(R.id.school_name_label)).check(matches(withText(schoolName)));

        // Delete the deck
        onView(withId(R.id.delete_button)).perform(scrollTo(), click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Check that delete was successful
        onView(withText(deckName)).check(doesNotExist());
    }


    /**
     * Test adding a new deck with flashcard data.
     */
    @Test
    public void testAddDeckFlashcards() {
        String deckName = "Test Deck";
        String[] terms = {"t1", "t2", "t3", "t4", "t5"};
        String[] defs = {"d1", "d2", "d3", "d4", "d5"};

        // Add the new deck
        onView(withId(R.id.add_deck_button)).perform(scrollTo(), click());
        onView(withId(R.id.LaunchDeckButton)).perform(click());

        // Type the title, course and school
        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();

        // Save the new deck
        onView(withId(R.id.save_deck_metadata)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));

        // Add the flashcards
        for (int i = 0; i < terms.length; i++) {
            onView(withId(R.id.add_flashcard_button)).perform(scrollTo(), click());
            onView(allOf(withHint(R.string.TermString), withText(""))).perform(typeText(terms[i]));
            closeSoftKeyboard();
            onView(allOf(withHint(R.string.DefinitionString), withText(""))).perform(typeText(defs[i]));
            closeSoftKeyboard();
        }

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

        // Check that the flashcards saved
        for (int i = 0; i < terms.length; i++) {
            onView(withText(terms[i])).check(matches(isDisplayed()));
            onView(withText(defs[i])).check(matches(isDisplayed()));
        }

        // Delete the deck
        onView(withId(R.id.delete_button)).perform(scrollTo(), click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Check that delete was successful
        onView(withText(deckName)).check(doesNotExist());
    }

}
