package com.example.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewDishAdapter extends RecyclerView.Adapter<NewDishAdapter.ViewHolder> {

    private List<Dish> dishes;

    public NewDishAdapter(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dishNameTextView;
        public TextView dishPriceTextView;
        public ImageView dishImageView;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            dishNameTextView = view.findViewById(R.id.dish_name);
            dishPriceTextView = view.findViewById(R.id.dish_price);
            dishImageView = view.findViewById(R.id.dish_image);
            cardView = view.findViewById(R.id.card_view1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_selected_dish_item, parent, false); // Use new layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishNameTextView.setText(dish.getName());
        holder.dishPriceTextView.setText(dish.getPrice());

        // Handle image resource
        int imageResId = dish.getImageResId();
        if (imageResId != 0) {
            holder.dishImageView.setImageResource(imageResId);
        } else {
            holder.dishImageView.setImageDrawable(null); // Clear image if no resource
        }

        // Hide add/remove buttons (only needed if using layout_dish_item.xml)
        View addButton = holder.itemView.findViewById(R.id.button);
        View removeButton = holder.itemView.findViewById(R.id.button2);
        if (addButton != null) addButton.setVisibility(View.GONE);
        if (removeButton != null) removeButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}