package com.example.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewDishAdapter extends RecyclerView.Adapter<NewDishAdapter.ViewHolder> {

    private List<Dish> dishes = new ArrayList<>();
    private int[] quantities;
    private OnCartActionListener cartActionListener;

    public interface OnCartActionListener {
        void onAddToCart(Dish dish, int quantity, String instructions);
    }

    public NewDishAdapter(List<Dish> dishes, OnCartActionListener listener) {
        this.dishes = dishes != null ? new ArrayList<>(dishes) : new ArrayList<>();
        this.cartActionListener = listener;
        updateQuantities();
    }

    public void updateDishes(List<Dish> newDishes) {
        this.dishes = newDishes != null ? new ArrayList<>(newDishes) : new ArrayList<>();
        updateQuantities();
        notifyDataSetChanged();
    }

    private void updateQuantities() {
        quantities = new int[dishes.size()];
        for (int i = 0; i < quantities.length; i++) {
            quantities[i] = 1;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dishNameTextView;
        public TextView dishPriceTextView;
        public ImageView dishImageView;
        public CardView cardView;
        public ImageButton increaseButton;
        public ImageButton decreaseButton;
        public TextView quantityTextView;
        public EditText instructionsEditText;
        public View addToCartButton;

        public ViewHolder(View view) {
            super(view);
            try {
                dishNameTextView = view.findViewById(R.id.dish_name);
                dishPriceTextView = view.findViewById(R.id.dish_price);
                dishImageView = view.findViewById(R.id.dish_image);
                cardView = view.findViewById(R.id.card_view1);
                increaseButton = view.findViewById(R.id.increaseButton);
                decreaseButton = view.findViewById(R.id.decreaseButton);
                quantityTextView = view.findViewById(R.id.quantityText);
                instructionsEditText = view.findViewById(R.id.instructionsEditText);
                addToCartButton = view.findViewById(R.id.addToCartButton);
            } catch (Exception e) {
                Toast.makeText(view.getContext(), "Error initializing view: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_selected_dish_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= dishes.size() || holder.dishNameTextView == null) {
            return; // Prevent crashes due to invalid position or uninitialized views
        }

        Dish dish = dishes.get(position);
        holder.dishNameTextView.setText(dish.getName());
        holder.dishPriceTextView.setText(dish.getPrice());
        holder.quantityTextView.setText(String.valueOf(quantities[position]));

        // Handle image resource
        int imageResId = dish.getImageResId();
        if (imageResId != 0) {
            holder.dishImageView.setImageResource(imageResId);
        } else {
            holder.dishImageView.setImageDrawable(null);
        }

        // Quantity increment
        holder.increaseButton.setOnClickListener(v -> {
            quantities[position]++;
            holder.quantityTextView.setText(String.valueOf(quantities[position]));
        });

        // Quantity decrement
        holder.decreaseButton.setOnClickListener(v -> {
            if (quantities[position] > 1) {
                quantities[position]--;
                holder.quantityTextView.setText(String.valueOf(quantities[position]));
            }
        });

        // Add to cart
        holder.addToCartButton.setOnClickListener(v -> {
            String instructions = holder.instructionsEditText.getText().toString().trim();
            cartActionListener.onAddToCart(dish, quantities[position], instructions);
            quantities[position] = 1;
            holder.quantityTextView.setText("1");
            holder.instructionsEditText.setText("");
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}