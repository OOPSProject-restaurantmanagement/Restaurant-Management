package com.example.oops;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class Menuitems extends AppCompatActivity {

    private List<Dish> dishes = new ArrayList<>();
    private Set<String> addedDishNames = new HashSet<>();
    private Map<String, String> addedDishIds = new HashMap<>(); // Track Firebase keys
    private DishAdapter dishAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitems);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample dish list
        dishes.add(new Dish("IDLY", R.drawable.idly, "₹50"));
        dishes.add(new Dish("VADA", R.drawable.vada, "₹70"));
        dishes.add(new Dish("DOSA", R.drawable.dosa, "₹120"));
        dishes.add(new Dish("CHOLE BHATURE", R.drawable.cholebature, "₹60"));
        dishes.add(new Dish("POHA", R.drawable.poha, "₹40"));
        dishes.add(new Dish("CHOWMEIN", R.drawable.chowmein, "₹60"));
        dishes.add(new Dish("COFFEE", R.drawable.coffee, "₹25"));
        dishes.add(new Dish("TEA", R.drawable.tea, "₹20"));
        dishes.add(new Dish("BIRYANI", R.drawable.biryani, "₹180"));
        dishes.add(new Dish("TOMATO CURRY", R.drawable.tomatocurry, "₹70"));

        // Initialize adapter with both addedDishNames and addedDishIds
        dishAdapter = new DishAdapter(dishes, addedDishNames, addedDishIds);
        recyclerView.setAdapter(dishAdapter);

        // Fetch existing selected dishes from Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Selected Menu");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addedDishNames.clear();
                addedDishIds.clear(); // Clear previous IDs
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Dish dish = snap.getValue(Dish.class);
                    if (dish != null && dish.getName() != null) {
                        addedDishNames.add(dish.getName());
                        addedDishIds.put(dish.getName(), snap.getKey()); // Store Firebase key
                    }
                }
                dishAdapter.notifyDataSetChanged(); // Refresh adapter to reflect state
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}