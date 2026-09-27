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
import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.util.EmojiHelper;
import com.ntokozo.smartpantry.util.Formatters;
import com.ntokozo.smartpantry.util.SettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter: turns each PantryItem from the database into a card on screen.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Lets the Activity react to taps without the adapter knowing about Activities. */
    public interface OnPantryItemActionListener {
        void onPantryItemClick(PantryItem item);

        void onPantryItemDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemActionListener listener;
    private boolean highlightExpiring = true;

    public PantryAdapter(OnPantryItemActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setHighlightExpiring(boolean highlightExpiring) {
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Holds references to the views of one card so they are not looked up again on scroll. */
    class PantryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textEmoji;
        private final TextView textName;
        private final TextView textQuantity;
        private final TextView textExpiry;
        private final TextView buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmoji = itemView.findViewById(R.id.textEmoji);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(PantryItem item) {
            Context context = itemView.getContext();
            textEmoji.setText(EmojiHelper.forIngredient(item.getName()));
            textName.setText(item.getName());
            textQuantity.setText(context.getString(R.string.pantry_quantity,
                    Formatters.quantity(item.getQuantity()), item.getUnit()));
            bindExpiry(context, item.getExpiryEpochDay());

            itemView.setOnClickListener(v -> listener.onPantryItemClick(item));
            buttonDelete.setOnClickListener(v -> listener.onPantryItemDelete(item));
        }

        private void bindExpiry(Context context, Long expiryEpochDay) {
            int normal = ContextCompat.getColor(context, R.color.text_secondary);
            int warning = ContextCompat.getColor(context, R.color.warning_yellow);
            int danger = ContextCompat.getColor(context, R.color.no_match_red);

            if (expiryEpochDay == null) {
                textExpiry.setText(R.string.expiry_none);
                textExpiry.setTextColor(normal);
                return;
            }

            long days = Formatters.daysUntil(expiryEpochDay);
            String date = Formatters.date(expiryEpochDay);

            if (days < 0) {
                textExpiry.setText(context.getString(R.string.expiry_expired, date));
                textExpiry.setTextColor(highlightExpiring ? danger : normal);
            } else if (days == 0) {
                textExpiry.setText(R.string.expiry_today);
                textExpiry.setTextColor(highlightExpiring ? warning : normal);
            } else if (days <= SettingsManager.EXPIRY_WARNING_DAYS) {
                textExpiry.setText(context.getResources()
                        .getQuantityString(R.plurals.expiry_soon, (int) days, (int) days));
                textExpiry.setTextColor(highlightExpiring ? warning : normal);
            } else {
                textExpiry.setText(context.getString(R.string.expiry_on, date));
                textExpiry.setTextColor(normal);
            }
        }
    }
}
