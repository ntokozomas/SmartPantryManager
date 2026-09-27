package com.ntokozo.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.ntokozo.smartpantry.util.SettingsManager;

import java.util.List;

/**
 * 🍳 Suggested Recipes screen.
 * Runs the strict-matching rule against the current pantry and lists ONLY
 * the recipes that can be cooked right now. "Almost there" recipes are shown
 * in a separate, clearly labelled section (optional bonus, can be turned off in Settings).
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private AppDatabase database;
    private SettingsManager settings;

    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;

    private TextView textSummary;
    private TextView textNoMatches;
    private TextView textNoAlmost;
    private RecyclerView recyclerStrict;
    private RecyclerView recyclerAlmost;
    private View sectionAlmostThere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.title_suggestions);

        database = AppDatabase.getInstance(this);
        settings = new SettingsManager(this);

        textSummary = findViewById(R.id.textSummary);
        textNoMatches = findViewById(R.id.textNoMatches);
        textNoAlmost = findViewById(R.id.textNoAlmost);
        recyclerStrict = findViewById(R.id.recyclerStrict);
        recyclerAlmost = findViewById(R.id.recyclerAlmost);
        sectionAlmostThere = findViewById(R.id.sectionAlmostThere);

        strictAdapter = new RecipeAdapter(RecipeAdapter.Mode.READY, this);
        recyclerStrict.setLayoutManager(new LinearLayoutManager(this));
        recyclerStrict.setAdapter(strictAdapter);

        almostAdapter = new RecipeAdapter(RecipeAdapter.Mode.ALMOST_THERE, this);
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlmost.setAdapter(almostAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadSuggestions() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<PantryItem> pantry = database.pantryItemDao().getAll();
            List<RecipeWithIngredients> recipes = database.recipeDao().getAllWithIngredients();

            List<MatchResult> strict = RecipeMatcher.findStrictMatches(pantry, recipes);
            List<MatchResult> almost = RecipeMatcher.findAlmostThere(pantry, recipes);

            runOnUiThread(() -> showResults(pantry.size(), recipes.size(), strict, almost));
        });
    }

    private void showResults(int pantryCount, int recipeCount,
                             List<MatchResult> strict, List<MatchResult> almost) {
        textSummary.setText(getString(R.string.suggest_summary, recipeCount, pantryCount));

        // Strict suggestions (or a friendly red message if there are none)
        strictAdapter.setResults(strict);
        boolean noMatches = strict.isEmpty();
        textNoMatches.setVisibility(noMatches ? View.VISIBLE : View.GONE);
        recyclerStrict.setVisibility(noMatches ? View.GONE : View.VISIBLE);

        // Separate "almost there" bonus section
        if (settings.isAlmostThereEnabled()) {
            sectionAlmostThere.setVisibility(View.VISIBLE);
            almostAdapter.setResults(almost);
            boolean noAlmost = almost.isEmpty();
            textNoAlmost.setVisibility(noAlmost ? View.VISIBLE : View.GONE);
            recyclerAlmost.setVisibility(noAlmost ? View.GONE : View.VISIBLE);
        } else {
            sectionAlmostThere.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClick(MatchResult result) {
        // Pass the chosen recipe's id to the detail screen
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, result.getRecipe().getId());
        startActivity(intent);
    }
}
