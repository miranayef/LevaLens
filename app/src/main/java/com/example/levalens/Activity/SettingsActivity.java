package com.example.levalens.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.levalens.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.settings));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            SwitchPreferenceCompat torchPreference = findPreference("torch_on_startup");
            ListPreference languagePreference = findPreference("language");
            ListPreference cameraPreference = findPreference("lens_facing");

            if (torchPreference != null) {
                torchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isEnabled = (Boolean) newValue;
                    String torchState = Boolean.TRUE.equals(isEnabled) ? getString(R.string.torchEnabled) : getString(R.string.torchDisabled);
                    Toast.makeText(getActivity(), getString(R.string.torchPreferenceChanged) + " " + torchState, Toast.LENGTH_SHORT).show();
                    return true;
                });
            }

            if (languagePreference != null) {
                languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String language = (String) newValue;
                    changeLanguage(language);
                    return true;
                });
            }

            if (cameraPreference != null) {
                cameraPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String camera = (String) newValue;
                    changeCamera(camera);
                    return true;
                });
            }
        }

        private void changeLanguage(String language) {
            if (getActivity() != null) {
                String languageName = language.equals("bg") ? getString(R.string.bulgarian) : getString(R.string.english);
                Toast.makeText(getActivity(), getString(R.string.language_changed_to) + " " + languageName, Toast.LENGTH_SHORT).show();

                // Restart the activity to apply the new language
                restartApp();
            }
        }

        private void changeCamera(String camera) {
            if (getActivity() != null) {
                String cameraName = camera.equals("front") ? getString(R.string.camera_front) : getString(R.string.camera_back);
                Toast.makeText(getActivity(), getString(R.string.camera_changed_to) + " " + cameraName, Toast.LENGTH_SHORT).show();

                restartApp();
            }
        }

        private void restartApp() {
            if (getActivity() != null) {
                Intent intent = getActivity().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        }
    }

}
