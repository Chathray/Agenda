package com.chath.agenda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public final class AppUtilities {

    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static int pxToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Drawable tinting(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        wrappedDrawable.mutate();
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static int circleRange(int min, int max, int value) {
        if (value > max || value < min) value = min;
        return value;
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setDefaults(String key, int value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getDefaultString(String key, Context context, String df) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, df);
    }

    public static int getDefaultInterger(String key, Context context, int df) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, df);
    }

    public static void restartApp(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        context.finishAffinity();
    }

    public static void hideKeyboard(Context cx) {
        View view = ((Activity) cx).findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) cx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context cx) {
        InputMethodManager inputMethodManager = (InputMethodManager) cx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void initializeLanguage(String key, Activity activity) {
        // Create a new Locale object
        Locale locale = new Locale(key);
        Locale.setDefault(locale);

        // Create a new configuration object
        Configuration config = new Configuration();

        // Set the locale of the new configuration
        config.locale = locale;

        // Update the configuration of the Accplication context
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    public static void addFragment(AppCompatActivity context, Fragment fragment, String tag, int id) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.add(id, fragment, tag);
        // transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void replaceFragment(AppCompatActivity context, Fragment fragment, String tag, int id) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(id, fragment, tag);
        // transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void buttonTinting(AppCompatActivity c,Button b, String color) {
        Drawable dr = AppCompatResources.getDrawable(c, R.drawable.ic_tb_search);
        b.setCompoundDrawablesWithIntrinsicBounds(AppUtilities.tinting(dr, Color.parseColor(color)), null, null, null);
    }
}
