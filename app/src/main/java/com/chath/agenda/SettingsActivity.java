package com.chath.agenda;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AgendaSettings);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        String lang = AppUtilities.getDefaultString(getString(R.string.key_lang), this, "default");
        AppUtilities.initializeLanguage(lang, this);

        AppUtilities.addFragment(this, new SettingsFragment(), "AS", R.id.settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            actionBar.setTitle(R.string.title_options);
            actionBar.setElevation(0);

            setOverflowButtonColor(this);
        }
    }

    public void setOverflowButtonColor(final Activity activity) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow=(AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(getResources().getColor(R.color.colorOnPrimary));
                removeOnGlobalLayoutListener(decorView,this);
            }
        });
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
        else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    public void resetAllSetting(MenuItem item) {

    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        SwitchPreferenceCompat lang;
        SwitchPreferenceCompat dark_mode;
        ListPreference arrange;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            lang = findPreference("language");
            dark_mode = findPreference("dark_theme");
            arrange = findPreference("arrange");
        }

        @Override
        public void onStart() {
            super.onStart();
            lang.setOnPreferenceChangeListener(this);
            dark_mode.setOnPreferenceChangeListener(this);
            arrange.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case "language":
                    Toast.makeText(getContext(), R.string.language_change_prompt, Toast.LENGTH_SHORT).show();

                    String lang = ((boolean) newValue) ? "default" : "vi";
                    AppUtilities.setDefaults(getString(R.string.key_lang), lang, getContext());

                    break;
                case "arrange":
                    int mode = Integer.parseInt(String.valueOf(newValue));
                    AppUtilities.setDefaults(getString(R.string.key_arrange_subject), mode, getContext());
                    break;

                case "dark_theme":
                    Toast.makeText(getContext(), R.string.theme_change_prompt, Toast.LENGTH_SHORT).show();

                    int dark = ((boolean) newValue) ? 2 : 1;
                    AppUtilities.setDefaults(getString(R.string.key_dark_theme), dark, getContext());
                    AppCompatDelegate.setDefaultNightMode(dark == 1 ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
            return true;
        }
    }
}