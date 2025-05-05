package com.example.oops;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.List;

public class vieworders extends AppCompatActivity {

    private static final String TAG = "ViewOrders";
    private List<ViewOrder> ordersList = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private Button clearButton;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworders);

        // Initialize Firebase orders reference
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_orders);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "RecyclerView not found in layout", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up empty state TextView
        emptyStateTextView = findViewById(R.id.empty_state_text);
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "emptyStateTextView not found in layout");
        }

        // Set up ProgressBar
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "progressBar not found in layout");
        }

        // Set up clear button (tick mark)
        clearButton = findViewById(R.id.clear_button);
        if (clearButton != null) {
            clearButton.setOnClickListener(v -> {
                ordersList.clear();
                orderAdapter.notifyDataSetChanged();
                updateUI();
            });
        } else {
            Log.e(TAG, "clearButton not found in layout");
        }

        // Initialize adapter
        orderAdapter = new OrderAdapter(ordersList);
        recyclerView.setAdapter(orderAdapter);

        // Fetch orders from Firebase
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange called");
                ordersList.clear();
                try {
                    for (DataSnapshot orderSnap : snapshot.getChildren()) {
                        for (DataSnapshot itemSnap : orderSnap.child("items").getChildren()) {
                            ViewOrder order = itemSnap.getValue(ViewOrder.class);
                            if (order != null) {
                                Log.d(TAG, "Order loaded: " + order.getName());
                                order.setPrepared(false); // Initialize as not prepared
                                ordersList.add(order);
                            } else {
                                Log.w(TAG, "Failed to parse order from snapshot: " + itemSnap.toString());
                            }
                        }
                    }
                    orderAdapter.notifyDataSetChanged();
                    updateUI();
                } catch (Exception e) {
                    Log.e(TAG, "Error in onDataChange: " + e.getMessage(), e);
                    Toast.makeText(vieworders.this, "Error loading orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load orders: " + error.getMessage());
                Toast.makeText(vieworders.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(ordersList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view_orders);
        if (recyclerView != null) {
            recyclerView.setVisibility(ordersList.isEmpty() ? View.GONE : View.VISIBLE);
        }
        if (clearButton != null) {
            clearButton.setVisibility(ordersList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }
}

class ViewOrder {
    private String name;
    private String price; // Changed from int to String to match Firebase data
    private String instructions;
    private int quantity;
    private int imageResId;
    private boolean isPrepared;

    // Default constructor for Firebase
    public ViewOrder() {}

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public boolean isPrepared() { return isPrepared; }
    public void setPrepared(boolean prepared) { isPrepared = prepared; }
}

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String TAG = "OrderAdapter";
    private List<ViewOrder> orders;

    public OrderAdapter(List<ViewOrder> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ViewOrder order = orders.get(position);
        holder.dishName.setText(order.getName());
        // Handle price as a String; assume it's a valid number for display
        String priceText = order.getPrice() != null ?   order.getPrice() : "₹0";
        holder.dishPrice.setText(priceText);
        holder.dishInstructions.setText("Instructions: " + order.getInstructions());
        try {
            holder.dishImage.setImageResource(order.getImageResId());
        } catch (Exception e) {
            Log.e(TAG, "Failed to set image resource for " + order.getName() + ": " + e.getMessage());
            holder.dishImage.setImageResource(android.R.drawable.ic_menu_gallery); // Fallback image
        }
        holder.checkBox.setChecked(order.isPrepared());
        holder.itemView.setAlpha(order.isPrepared() ? 0.5f : 1.0f);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            order.setPrepared(isChecked);
            holder.itemView.setAlpha(isChecked ? 0.5f : 1.0f);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName;
        TextView dishPrice;
        TextView dishInstructions;
        CheckBox checkBox;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.dish_image);
            dishName = itemView.findViewById(R.id.dish_name);
            dishPrice = itemView.findViewById(R.id.dish_price);
            dishInstructions = itemView.findViewById(R.id.dish_instructions);
            checkBox = itemView.findViewById(R.id.checkbox_prepared);
        }
    }
}