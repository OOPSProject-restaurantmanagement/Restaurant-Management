package com.example.oops;

import android.os.Bundle;
import android.view.View;
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
import java.util.List;

public class SelectedMenuActivity extends AppCompatActivity {

    private List<Dish> selectedDishes = new ArrayList<>();
    private NewDishAdapter dishAdapter;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_menu);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_selected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up empty state TextView
        emptyStateTextView = findViewById(R.id.empty_state_text);
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(View.GONE);
        }

        // Initialize adapter with selectedDishes
        dishAdapter = new NewDishAdapter(selectedDishes);
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
                        // Use DishUtils to get the correct image resource ID
                        dish.setImageResId(DishUtils.getImageResId(dish.getName().toUpperCase()));
                        selectedDishes.add(dish);
                    }
                }
                dishAdapter.notifyDataSetChanged(); // Refresh adapter

                // Show/hide empty state
                if (emptyStateTextView != null) {
                    emptyStateTextView.setVisibility(selectedDishes.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(selectedDishes.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectedMenuActivity.this, "Failed to load dishes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}