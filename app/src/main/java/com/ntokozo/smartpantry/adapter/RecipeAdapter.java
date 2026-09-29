package com.ntokozo.smartpantry.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ntokozo.smartpantry.R;
import com.ntokozo.smartpantry.data.Recipe;
import com.ntokozo.smartpantry.data.RecipeIngredient;
import com.ntokozo.smartpantry.logic.MatchResult;
import com.ntokozo.smartpantry.util.Formatters;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter for recipe cards.
 * Used on the suggestions screen (strict matches + "almost there") and the Recipe Book screen.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /** READY = strict matches, ALMOST_THERE = missing one item, BOOK = every recipe. */
    public enum Mode { READY, ALMOST_THERE, BOOK }

    public interface OnRecipeClickListener {
        void onRecipeClick(MatchResult result);
    }

    private final List<MatchResult> results = new ArrayList<>();
    private final Mode mode;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(Mode mode, OnRecipeClickListener listener) {
        this.mode = mode;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setResults(List<MatchResult> newResults) {
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textEmoji;
        private final TextView textName;
        private final TextView textSubtitle;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmoji = itemView.findViewById(R.id.textRecipeEmoji);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSubtitle = itemView.findViewById(R.id.textRecipeSubtitle);
        }

        void bind(MatchResult result) {
            Context context = itemView.getContext();
            Recipe recipe = result.getRecipe();
            textEmoji.setText(recipe.getEmoji());
            textName.setText(recipe.getName());

            if (mode == Mode.BOOK) {
                bindBookSubtitle(context, result);
            } else if (mode == Mode.READY) {
                int count = result.getRecipeWithIngredients().getIngredients().size();
                textSubtitle.setText(context.getResources()
                        .getQuantityString(R.plurals.recipe_ready_subtitle, count, count));
                textSubtitle.setTextColor(ContextCompat.getColor(context, R.color.success_mint));
            } else {
                RecipeIngredient missing = result.getMissingIngredients().get(0);
                textSubtitle.setText(context.getString(R.string.recipe_missing_subtitle,
                        Formatters.quantity(missing.getQuantity()), missing.getUnit(),
                        missing.getName()));
                textSubtitle.setTextColor(ContextCompat.getColor(context, R.color.warning_yellow));
            }

            itemView.setOnClickListener(v -> listener.onRecipeClick(result));
        }

        /** Recipe Book: show whether the recipe is ready, or how many ingredients are missing. */
        private void bindBookSubtitle(Context context, MatchResult result) {
            int missing = result.getMissingCount();
            int total = result.getRecipeWithIngredients().getIngredients().size();
            if (result.isStrictMatch()) {
                textSubtitle.setText(R.string.book_ready);
                textSubtitle.setTextColor(ContextCompat.getColor(context, R.color.success_mint));
            } else {
                textSubtitle.setText(context.getResources()
                        .getQuantityString(R.plurals.book_missing, missing, missing, total));
                int color = missing == 1 ? R.color.warning_yellow : R.color.text_secondary;
                textSubtitle.setTextColor(ContextCompat.getColor(context, color));
            }
        }
    }
}
