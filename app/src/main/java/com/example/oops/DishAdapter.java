package com.example.oops;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {

    private List<Dish> dishes;

    // To keep track of which dishes were added and their Firebase keys
    private Map<Integer, String> addedDishesMap = new HashMap<>();

    public DishAdapter(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dishNameTextView;
        public TextView dishPriceTextView;
        public ImageView dishImageView;
        public CardView cardView;
        public Button add, remove;

        public ViewHolder(View view) {
            super(view);
            dishNameTextView = view.findViewById(R.id.dish_name);
            dishPriceTextView = view.findViewById(R.id.dish_price);
            dishImageView = view.findViewById(R.id.dish_image);
            cardView = view.findViewById(R.id.card_view1);
            add = view.findViewById(R.id.button);    // '+' button
            remove = view.findViewById(R.id.button2); // '-' button
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_dish_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishNameTextView.setText(dish.getName());
        holder.dishPriceTextView.setText(dish.getPrice());
        holder.dishImageView.setImageResource(dish.getImageResId());

        // Set button states based on whether item is added
        if (addedDishesMap.containsKey(position)) {
            holder.add.setEnabled(false);
            holder.add.setAlpha(0.5f); // dim
            holder.remove.setEnabled(true);
            holder.remove.setAlpha(1.0f);
        } else {
            holder.add.setEnabled(true);
            holder.add.setAlpha(1.0f);
            holder.remove.setEnabled(false);
            holder.remove.setAlpha(0.5f);
        }

        // Add button
        holder.add.setOnClickListener(v -> {
            if (!addedDishesMap.containsKey(position)) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance()
                        .getReference("Selected Menu")
                        .push();
                String dishId = dbRef.getKey();
                dbRef.setValue(dish);

                // Store the Firebase ID to allow removal later
                addedDishesMap.put(position, dishId);

                // Update UI
                holder.add.setEnabled(false);
                holder.add.setAlpha(0.5f);
                holder.remove.setEnabled(true);
                holder.remove.setAlpha(1.0f);
            }
        });

        // Remove button
        holder.remove.setOnClickListener(v -> {
            if (addedDishesMap.containsKey(position)) {
                String dishId = addedDishesMap.get(position);
                FirebaseDatabase.getInstance()
                        .getReference("Selected Menu")
                        .child(dishId)
                        .removeValue();

                addedDishesMap.remove(position);

                // Update UI
                holder.add.setEnabled(true);
                holder.add.setAlpha(1.0f);
                holder.remove.setEnabled(false);
                holder.remove.setAlpha(0.5f);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
