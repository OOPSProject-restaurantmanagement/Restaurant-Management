package com.example.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {

    private List<Dish> dishes;
    private Set<String> addedDishNames;  // Names of already added dishes
    private Map<String, String> addedDishIds; // Map of dish names to Firebase keys

    public DishAdapter(List<Dish> dishes, Set<String> addedDishNames, Map<String, String> addedDishIds) {
        this.dishes = dishes;
        this.addedDishNames = addedDishNames;
        this.addedDishIds = addedDishIds;
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

        boolean isAlreadyAdded = addedDishNames.contains(dish.getName());

        holder.add.setEnabled(!isAlreadyAdded);
        holder.add.setAlpha(isAlreadyAdded ? 0.5f : 1.0f);
        holder.remove.setEnabled(isAlreadyAdded);
        holder.remove.setAlpha(isAlreadyAdded ? 1.0f : 0.5f);

        holder.add.setOnClickListener(v -> {
            if (!addedDishNames.contains(dish.getName())) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance()
                        .getReference("Selected Menu")
                        .push();
                String dishId = dbRef.getKey();
                dbRef.setValue(dish);

                addedDishNames.add(dish.getName());          // Track as added
                addedDishIds.put(dish.getName(), dishId);    // Store Firebase key
                notifyItemChanged(holder.getAdapterPosition()); // Refresh UI
            }
        });

        holder.remove.setOnClickListener(v -> {
            if (addedDishNames.contains(dish.getName())) {
                String id = addedDishIds.get(dish.getName());
                if (id != null) {
                    FirebaseDatabase.getInstance()
                            .getReference("Selected Menu")
                            .child(id)
                            .removeValue();

                    addedDishNames.remove(dish.getName());
                    addedDishIds.remove(dish.getName());
                    notifyItemChanged(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}