package com.example.flashcardapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.TypeTextAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.flashcardapp.Activities.DeckHomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<DeckHomeActivity> activityRule = new ActivityTestRule<>(DeckHomeActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.flashcardapp", appContext.getPackageName());
    }

    /**
     * Test that the deck name, course, school change after click save changes.
     */
    @Test
    public void testDeckMetaDataChanged() {
        String deckName = "Test Deck";
        String courseName = "CSE 5236";
        String schoolName = "OSU";
        onView(withId(R.id.LaunchDeckButton)).perform(click());
        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_course_name)).perform(typeText(courseName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_school_name)).perform(typeText(schoolName));
        closeSoftKeyboard();
        onView(withId(R.id.save_deck_metadata)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));
        onView(withId(R.id.course_name_label)).check(matches(withText(courseName)));
        onView(withId(R.id.school_name_label)).check(matches(withText(schoolName)));
    }

    /**
     * Test the add professors functionality.
     */
    @Test
    public void testAddProfs() {
        String deckName = "Test Deck";
        String courseName = "CSE 5236";
        String schoolName = "OSU";
        String[] profs = {"Professor X", "Professor Y", "Professor Z"};
        onView(withId(R.id.LaunchDeckButton)).perform(click());

        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_course_name)).perform(typeText(courseName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_school_name)).perform(typeText(schoolName));
        closeSoftKeyboard();

        for (int i = 0; i < profs.length; i++) {
            onView(withId(R.id.add_professor)).perform(scrollTo(), click());
            onView(withText("")).perform(typeText(profs[i]));
            closeSoftKeyboard();
        }

        onView(withId(R.id.save_deck_metadata)).perform(scrollTo(), click());

        // Check that all the text matches
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));
        onView(withId(R.id.course_name_label)).check(matches(withText(courseName)));
        onView(withId(R.id.school_name_label)).check(matches(withText(schoolName)));

        for (int i = 0; i < profs.length; i++) {
            onView(withId(R.id.professor_name_label)).perform(scrollTo(), click()).check(matches(withText(profs[i])));
        }
    }

    /**
     * Test the add professors functionality.
     */
    @Test
    public void testAddCategories() {
        String deckName = "Test Deck";
        String courseName = "CSE 5236";
        String schoolName = "OSU";
        String[] categories = {"Computer Science", "Android App Development", "Programming", "Lifestyle"};

        onView(withId(R.id.LaunchDeckButton)).perform(click());

        onView(withId(R.id.edit_deck_name)).perform(typeText(deckName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_course_name)).perform(typeText(courseName));
        closeSoftKeyboard();
        onView(withId(R.id.edit_school_name)).perform(typeText(schoolName));
        closeSoftKeyboard();

        for (int i = 0; i < categories.length; i++) {
            onView(withId(R.id.add_category)).perform(scrollTo(), click());
            onView(withText("")).perform(typeText(categories[i]));
            closeSoftKeyboard();
        }

        onView(withId(R.id.save_deck_metadata)).perform(scrollTo(), click());

        // Check that all the text matches
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.deck_name_label)).check(matches(withText(deckName)));
        onView(withId(R.id.course_name_label)).check(matches(withText(courseName)));
        onView(withId(R.id.school_name_label)).check(matches(withText(schoolName)));

        for (int i = 0; i < categories.length; i++) {
            onView(withId(R.id.category_name_label)).perform(scrollTo(), click()).check(matches(withText(categories[i])));
        }
    }

}
