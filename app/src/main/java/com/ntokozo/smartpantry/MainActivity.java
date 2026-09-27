package com.ntokozo.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.ntokozo.smartpantry.adapter.PantryAdapter;
import com.ntokozo.smartpantry.data.AppDatabase;
import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.util.SettingsManager;

import java.util.List;

/**
 * 🧺 Pantry List screen (launcher Activity).
 * Shows every pantry item from the database in a RecyclerView.
 */
public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemActionListener {

    private AppDatabase database;
    private SettingsManager settings;
    private PantryAdapter adapter;

    private RecyclerView recyclerPantry;
    private TextView textEmptyPantry;
    private TextView textPantryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_pantry);

        database = AppDatabase.getInstance(this);
        settings = new SettingsManager(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);
        textPantryCount = findViewById(R.id.textPantryCount);

        adapter = new PantryAdapter(this);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        // Explicit Intent -> Add ingredient screen (no extra = "add" mode)
        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditItemActivity.class)));

        MaterialButton buttonCook = findViewById(R.id.buttonWhatCanICook);
        buttonCook.setOnClickListener(v -> openSuggestions());
    }

    /**
     * onResume runs every time this screen comes back into view
     * (e.g. after adding or editing an item), so the list is always fresh.
     */
    @Override
    protected void onResume() {
        super.onResume();
        adapter.setHighlightExpiring(settings.isExpiryHighlightEnabled());
        loadPantry();
    }

    private void loadPantry() {
        // Database work on the background thread, UI updates back on the main thread
        AppDatabase.databaseExecutor.execute(() -> {
            List<PantryItem> items = database.pantryItemDao().getAll();
            runOnUiThread(() -> showPantry(items));
        });
    }

    private void showPantry(List<PantryItem> items) {
        adapter.setItems(items);
        boolean isEmpty = items.isEmpty();
        textEmptyPantry.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        textPantryCount.setText(getResources()
                .getQuantityString(R.plurals.pantry_count, items.size(), items.size()));
    }

    private void openSuggestions() {
        startActivity(new Intent(this, SuggestedRecipesActivity.class));
    }

    // ---- Toolbar menu (navigation element) ----

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_suggestions) {
            openSuggestions();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---- Adapter callbacks ----

    @Override
    public void onPantryItemClick(PantryItem item) {
        // Pass the item's id to the edit screen through the Intent
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onPantryItemDelete(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, item.getName()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deleteItem(item))
                .show();
    }

    private void deleteItem(PantryItem item) {
        AppDatabase.databaseExecutor.execute(() -> {
            database.pantryItemDao().deleteById(item.getId());
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.toast_deleted, item.getName()),
                        Toast.LENGTH_SHORT).show();
                loadPantry();
            });
        });
    }
}
