package com.example.oops;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedMenuActivity extends AppCompatActivity {

    private List<Dish> selectedDishes = new ArrayList<>();
    private NewDishAdapter dishAdapter;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_menu);

        // Initialize Firebase cart reference
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_selected);
        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView not found in layout", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up empty state TextView
        emptyStateTextView = findViewById(R.id.empty_state_text);
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(View.GONE);
        }

        // Set up ProgressBar (optional)
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Set up cart button
        ImageView cartButton = findViewById(R.id.cart_button);
        if (cartButton != null) {
            cartButton.setOnClickListener(v -> {
                Intent intent = new Intent(SelectedMenuActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        // Initialize adapter with selectedDishes
        dishAdapter = new NewDishAdapter(selectedDishes, new NewDishAdapter.OnCartActionListener() {
            @Override
            public void onAddToCart(Dish dish, int quantity, String instructions) {
                addToCart(dish, quantity, instructions);
            }
        });
        recyclerView.setAdapter(dishAdapter);

        // Fetch dishes from Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Selected Menu");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedDishes.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Dish dish = snap.getValue(Dish.class);
                    if (dish != null && dish.getName() != null) {
                        dish.setImageResId(Dish_Utils.getImageResId(dish.getName().toUpperCase()));
                        selectedDishes.add(dish);
                    }
                }
                dishAdapter.updateDishes(selectedDishes); // Update adapter with new data
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectedMenuActivity.this, "Failed to load dishes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(selectedDishes.isEmpty() ? View.VISIBLE : View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view_selected);
        if (recyclerView != null) {
            recyclerView.setVisibility(selectedDishes.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void addToCart(Dish dish, int quantity, String instructions) {
        String cartItemId = cartRef.push().getKey();
        if (cartItemId == null) {
            Toast.makeText(this, "Failed to generate cart item ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("name", dish.getName());
        orderData.put("price", dish.getPrice());
        orderData.put("quantity", quantity);
        orderData.put("instructions", instructions);
        orderData.put("imageResId", dish.getImageResId());

        cartRef.child(cartItemId).setValue(orderData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SelectedMenuActivity.this, "Added to cart: " + dish.getName(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SelectedMenuActivity.this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}