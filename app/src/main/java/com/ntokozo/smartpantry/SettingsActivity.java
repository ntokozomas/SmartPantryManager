package com.ntokozo.smartpantry;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.ntokozo.smartpantry.data.AppDatabase;
import com.ntokozo.smartpantry.util.SettingsManager;

/**
 * ⚙️ Settings screen: app preferences saved with SharedPreferences.
 */
public class SettingsActivity extends AppCompatActivity {

    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.title_settings);

        settings = new SettingsManager(this);

        MaterialSwitch switchExpiry = findViewById(R.id.switchExpiryHighlight);
        switchExpiry.setChecked(settings.isExpiryHighlightEnabled());
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                settings.setExpiryHighlightEnabled(isChecked));

        MaterialSwitch switchAlmost = findViewById(R.id.switchAlmostThere);
        switchAlmost.setChecked(settings.isAlmostThereEnabled());
        switchAlmost.setOnCheckedChangeListener((button, isChecked) ->
                settings.setAlmostThereEnabled(isChecked));

        MaterialButton buttonClear = findViewById(R.id.buttonClearPantry);
        buttonClear.setOnClickListener(v -> confirmClearPantry());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void confirmClearPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_message)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_clear, (dialog, which) -> clearPantry())
                .show();
    }

    private void clearPantry() {
        AppDatabase database = AppDatabase.getInstance(this);
        AppDatabase.databaseExecutor.execute(() -> {
            database.pantryItemDao().deleteAll();
            runOnUiThread(() ->
                    Toast.makeText(this, R.string.toast_pantry_cleared, Toast.LENGTH_SHORT).show());
        });
    }
}
