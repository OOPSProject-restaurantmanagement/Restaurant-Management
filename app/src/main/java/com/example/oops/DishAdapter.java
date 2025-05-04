package com.example.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Button;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {
    private List<Dish> dishes;

    public DishAdapter(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dishNameTextView;
        public TextView dishPriceTextView;
        public ImageView dishImageView;
        public CardView cardView;
        public Button add,remove;
        public ViewHolder(View view) {
            super(view);
            dishNameTextView = view.findViewById(R.id.dish_name);
            dishPriceTextView = view.findViewById(R.id.dish_price);
            dishImageView = view.findViewById(R.id.dish_image);
            cardView = view.findViewById(R.id.card_view1);  // Make sure it matches XML ID
            add=view.findViewById(R.id.button);
            remove=view.findViewById(R.id.button2);
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
        holder.add.setOnClickListener(v -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("Selected Menu")
                    .push();
            dbRef.setValue(dish);
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
