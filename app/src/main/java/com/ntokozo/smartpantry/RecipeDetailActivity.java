package com.ntokozo.smartpantry;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.ntokozo.smartpantry.data.AppDatabase;
import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.data.Recipe;
import com.ntokozo.smartpantry.data.RecipeIngredient;
import com.ntokozo.smartpantry.data.RecipeWithIngredients;
import com.ntokozo.smartpantry.logic.RecipeMatcher;
import com.ntokozo.smartpantry.logic.UnitConverter;
import com.ntokozo.smartpantry.util.Formatters;

import java.util.List;
import java.util.Map;

/**
 * 🧾 Recipe Detail screen: full ingredient list (✅ have / ❌ missing) and the method.
 * The recipe id arrives through the Intent from the suggestions screen.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.ntokozo.smartpantry.EXTRA_RECIPE_ID";
    private static final long NO_ID = -1L;

    private AppDatabase database;

    private TextView textEmoji;
    private TextView textName;
    private TextView textDescription;
    private TextView textStatus;
    private TextView textIngredients;
    private TextView textSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.title_recipe);

        database = AppDatabase.getInstance(this);

        textEmoji = findViewById(R.id.textDetailEmoji);
        textName = findViewById(R.id.textDetailName);
        textDescription = findViewById(R.id.textDetailDescription);
        textStatus = findViewById(R.id.textDetailStatus);
        textIngredients = findViewById(R.id.textDetailIngredients);
        textSteps = findViewById(R.id.textDetailSteps);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, NO_ID);
        if (recipeId == NO_ID) {
            Toast.makeText(this, R.string.toast_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadRecipe(recipeId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadRecipe(long recipeId) {
        AppDatabase.databaseExecutor.execute(() -> {
            RecipeWithIngredients recipe = database.recipeDao().getById(recipeId);
            List<PantryItem> pantry = database.pantryItemDao().getAll();
            runOnUiThread(() -> {
                if (recipe == null) {
                    Toast.makeText(this, R.string.toast_recipe_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                showRecipe(recipe, pantry);
            });
        });
    }

    private void showRecipe(RecipeWithIngredients recipeWithIngredients, List<PantryItem> pantry) {
        Recipe recipe = recipeWithIngredients.getRecipe();
        textEmoji.setText(recipe.getEmoji());
        textName.setText(recipe.getName());
        textDescription.setText(recipe.getDescription());

        // Ingredient list: missing ones are coloured red
        Map<String, List<UnitConverter.Amount>> pantryIndex = RecipeMatcher.buildPantryIndex(pantry);
        int red = ContextCompat.getColor(this, R.color.no_match_red);
        SpannableStringBuilder ingredientsText = new SpannableStringBuilder();
        int missingCount = 0;

        for (RecipeIngredient ingredient : recipeWithIngredients.getIngredients()) {
            boolean haveEnough = RecipeMatcher.hasEnough(pantryIndex, ingredient);
            String line = getString(
                    haveEnough ? R.string.ingredient_have : R.string.ingredient_missing,
                    Formatters.quantity(ingredient.getQuantity()), ingredient.getUnit(),
                    ingredient.getName());

            if (ingredientsText.length() > 0) {
                ingredientsText.append('\n');
            }
            int start = ingredientsText.length();
            ingredientsText.append(line);
            if (!haveEnough) {
                missingCount++;
                ingredientsText.setSpan(new ForegroundColorSpan(red), start,
                        ingredientsText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textIngredients.setText(ingredientsText);

        // Status pill
        if (missingCount == 0) {
            textStatus.setText(R.string.detail_status_ready);
            textStatus.setTextColor(ContextCompat.getColor(this, R.color.success_mint));
        } else {
            textStatus.setText(getResources().getQuantityString(
                    R.plurals.detail_status_missing, missingCount, missingCount));
            textStatus.setTextColor(red);
        }

        // Numbered method steps
        StringBuilder stepsText = new StringBuilder();
        List<String> steps = recipe.getStepList();
        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) {
                stepsText.append("\n\n");
            }
            stepsText.append(getString(R.string.step_format, i + 1, steps.get(i)));
        }
        textSteps.setText(stepsText.toString());
    }
}
