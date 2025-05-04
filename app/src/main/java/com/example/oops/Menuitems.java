package com.example.oops;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Menuitems extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitems);

        // Sample data: Replace with your dish names, images, and prices
        List<Dish> dishes = new ArrayList<>();

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
        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DishAdapter(dishes));
    }
}
