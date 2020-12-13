package com.example.photoresolution;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.photoresolution.Register.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RejestrationActivityTest {
    @Rule
    public ActivityTestRule<RegisterActivity> activityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void test_is_password_empty() {
        onView(withId(R.id.username)).perform(typeText("invalid"), closeSoftKeyboard());
        onView(withId(R.id.register_button_reg)).perform(click());
        onView(withText("Please write your password..."))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_is_login_empty() {
        onView(withId(R.id.register_button_reg)).perform(click());
        onView(withText("Please write your username..."))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_is_password_has_6_characters() {
        onView(withId(R.id.username)).perform(typeText("valid@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abcd"), closeSoftKeyboard());
        onView(withId(R.id.register_button_reg)).perform(click());
        onView(withText("Password must have at least 6 characters..."))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}