package com.ntokozo.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ntokozo.smartpantry.adapter.RecipeAdapter;
import com.ntokozo.smartpantry.data.AppDatabase;
import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.data.RecipeWithIngredients;
import com.ntokozo.smartpantry.logic.MatchResult;
import com.ntokozo.smartpantry.logic.RecipeMatcher;
import com.ntokozo.smartpantry.logic.UnitConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 📖 Recipe Book screen: browse EVERY recipe in the database.
 * This is separate from the Suggested Recipes screen - here nothing is filtered out,
 * each card just shows whether it's ready to cook or how many ingredients are missing.
 */
public class RecipeBookActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private AppDatabase database;
    private RecipeAdapter adapter;
    private TextView textSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_book);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.title_recipe_book);

        database = AppDatabase.getInstance(this);
        textSummary = findViewById(R.id.textBookSummary);

        RecyclerView recycler = findViewById(R.id.recyclerRecipeBook);
        adapter = new RecipeAdapter(RecipeAdapter.Mode.BOOK, this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    /** Reload each time the screen is shown, in case the pantry changed. */
    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadRecipes() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<RecipeWithIngredients> recipes = database.recipeDao().getAllWithIngredients();
            List<PantryItem> pantry = database.pantryItemDao().getAll();

            // Check every recipe against the pantry, but keep ALL of them in the list
            Map<String, List<UnitConverter.Amount>> index = RecipeMatcher.buildPantryIndex(pantry);
            List<MatchResult> results = new ArrayList<>();
            for (RecipeWithIngredients recipe : recipes) {
                results.add(RecipeMatcher.evaluate(recipe, index));
            }

            runOnUiThread(() -> {
                textSummary.setText(getString(R.string.book_summary, results.size()));
                adapter.setResults(results);
            });
        });
    }

    @Override
    public void onRecipeClick(MatchResult result) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, result.getRecipe().getId());
        startActivity(intent);
    }
}
