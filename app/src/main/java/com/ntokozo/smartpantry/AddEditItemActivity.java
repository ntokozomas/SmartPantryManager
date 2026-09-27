package com.ntokozo.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ntokozo.smartpantry.data.AppDatabase;
import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.util.EmojiHelper;
import com.ntokozo.smartpantry.util.Formatters;

import java.time.LocalDate;

/**
 * ✏️ Add / Edit Ingredient screen.
 * No EXTRA_ITEM_ID in the Intent = add a new item; with an id = edit that item.
 */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.ntokozo.smartpantry.EXTRA_ITEM_ID";

    private static final long NO_ID = -1L;
    private static final int MAX_NAME_LENGTH = 40;
    private static final double MAX_QUANTITY = 100_000;
    private static final String STATE_EXPIRY = "state_expiry";

    private AppDatabase database;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private Spinner spinnerUnit;
    private MaterialButton buttonPickExpiry;
    private MaterialButton buttonClearExpiry;
    private TextView textFormEmoji;

    private long itemId = NO_ID;
    /** Selected expiry date as an epoch day, or null for "no expiry". */
    private Long selectedExpiryEpochDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = AppDatabase.getInstance(this);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        buttonPickExpiry = findViewById(R.id.buttonPickExpiry);
        buttonClearExpiry = findViewById(R.id.buttonClearExpiry);
        textFormEmoji = findViewById(R.id.textFormEmoji);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonDelete = findViewById(R.id.buttonDelete);

        // Unit dropdown filled from the string-array in strings.xml
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, R.layout.item_spinner);
        unitAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerUnit.setAdapter(unitAdapter);

        // Clear the error as soon as the user starts fixing a field; update the emoji live
        editName.addTextChangedListener(new SimpleWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                layoutName.setError(null);
                textFormEmoji.setText(EmojiHelper.forIngredient(s.toString()));
            }
        });
        editQuantity.addTextChangedListener(new SimpleWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                layoutQuantity.setError(null);
            }
        });

        buttonPickExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearExpiry.setOnClickListener(v -> {
            selectedExpiryEpochDay = null;
            updateExpiryButton();
        });
        buttonSave.setOnClickListener(v -> saveItem());
        buttonDelete.setOnClickListener(v -> confirmDelete());

        // Read the data passed in through the Intent
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        boolean isEditMode = itemId != NO_ID;
        setTitle(isEditMode ? R.string.title_edit_item : R.string.title_add_item);
        buttonDelete.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        if (savedInstanceState != null) {
            // Screen was rotated: keep what the user already chose
            if (savedInstanceState.containsKey(STATE_EXPIRY)) {
                selectedExpiryEpochDay = savedInstanceState.getLong(STATE_EXPIRY);
            }
        } else if (isEditMode) {
            loadItem();
        }
        updateExpiryButton();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedExpiryEpochDay != null) {
            outState.putLong(STATE_EXPIRY, selectedExpiryEpochDay);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadItem() {
        AppDatabase.databaseExecutor.execute(() -> {
            PantryItem item = database.pantryItemDao().getById(itemId);
            runOnUiThread(() -> {
                if (item == null) {
                    Toast.makeText(this, R.string.toast_item_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                editName.setText(item.getName());
                editQuantity.setText(Formatters.quantity(item.getQuantity()));
                selectUnit(item.getUnit());
                selectedExpiryEpochDay = item.getExpiryEpochDay();
                updateExpiryButton();
            });
        });
    }

    private void selectUnit(String unit) {
        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(unit)) {
                spinnerUnit.setSelection(i);
                return;
            }
        }
    }

    private void showDatePicker() {
        LocalDate initial = selectedExpiryEpochDay != null
                ? LocalDate.ofEpochDay(selectedExpiryEpochDay)
                : LocalDate.now();
        // DatePicker months are 0-based, LocalDate months are 1-based
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedExpiryEpochDay = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay();
                    updateExpiryButton();
                },
                initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth())
                .show();
    }

    private void updateExpiryButton() {
        if (selectedExpiryEpochDay == null) {
            buttonPickExpiry.setText(R.string.expiry_pick);
            buttonClearExpiry.setVisibility(View.GONE);
        } else {
            buttonPickExpiry.setText(getString(R.string.expiry_selected,
                    Formatters.date(selectedExpiryEpochDay)));
            buttonClearExpiry.setVisibility(View.VISIBLE);
        }
    }

    // ---- Input validation ----

    /** Checks every field, shows friendly errors, and returns true only if all are valid. */
    private boolean validateInput() {
        boolean valid = true;

        String name = textOf(editName);
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() > MAX_NAME_LENGTH) {
            layoutName.setError(getString(R.string.error_name_too_long, MAX_NAME_LENGTH));
            valid = false;
        } else if (!name.matches(".*\\p{L}.*")) {
            layoutName.setError(getString(R.string.error_name_letters));
            valid = false;
        } else {
            layoutName.setError(null);
        }

        String quantityText = textOf(editQuantity);
        Double quantity = parseQuantity(quantityText);
        if (quantityText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else if (quantity == null) {
            layoutQuantity.setError(getString(R.string.error_quantity_invalid));
            valid = false;
        } else if (quantity <= 0) {
            layoutQuantity.setError(getString(R.string.error_quantity_positive));
            valid = false;
        } else if (quantity > MAX_QUANTITY) {
            layoutQuantity.setError(getString(R.string.error_quantity_too_large,
                    Formatters.quantity(MAX_QUANTITY)));
            valid = false;
        } else {
            layoutQuantity.setError(null);
        }

        return valid;
    }

    /** Accepts both "1.5" and "1,5". Returns null if the text is not a number. */
    private static Double parseQuantity(String text) {
        try {
            double value = Double.parseDouble(text.replace(',', '.'));
            return Double.isNaN(value) || Double.isInfinite(value) ? null : value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    // ---- Save / delete ----

    private void saveItem() {
        if (!validateInput()) {
            return;
        }
        String name = textOf(editName);
        Double quantity = parseQuantity(textOf(editQuantity));
        String unit = spinnerUnit.getSelectedItem().toString();

        PantryItem item = new PantryItem(name, quantity != null ? quantity : 0, unit,
                selectedExpiryEpochDay);
        boolean isEditMode = itemId != NO_ID;
        if (isEditMode) {
            item.setId(itemId);
        }

        AppDatabase.databaseExecutor.execute(() -> {
            if (isEditMode) {
                database.pantryItemDao().update(item);
            } else {
                database.pantryItemDao().insert(item);
            }
            runOnUiThread(() -> {
                int message = isEditMode ? R.string.toast_updated : R.string.toast_added;
                Toast.makeText(this, getString(message, name), Toast.LENGTH_SHORT).show();
                finish(); // back to the pantry list, which reloads in onResume()
            });
        });
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, textOf(editName)))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deleteItem())
                .show();
    }

    private void deleteItem() {
        String name = textOf(editName);
        AppDatabase.databaseExecutor.execute(() -> {
            database.pantryItemDao().deleteById(itemId);
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.toast_deleted, name),
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    /** TextWatcher with empty defaults so we only override what we need. */
    private abstract static class SimpleWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}
