package com.example.oops;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter;
    private TextView emptyStateTextView;
    private TextView totalCostTextView;
    private ProgressBar progressBar;
    private DatabaseReference cartRef;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase references
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_cart);
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

        // Set up ProgressBar
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Set up total cost TextView
        totalCostTextView = findViewById(R.id.total_cost_text);
        if (totalCostTextView != null) {
            totalCostTextView.setText("Total: ₹0");
        }

        // Initialize adapter
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        // Set up order button
        Button orderButton = findViewById(R.id.order_button);
        if (orderButton != null) {
            orderButton.setOnClickListener(v -> placeOrder());
        }

        // Fetch cart items from Firebase
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                double totalCost = 0.0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CartItem item = snap.getValue(CartItem.class);
                    if (item != null && item.getName() != null) {
                        cartItems.add(item);
                        // Parse price (e.g., "₹50") and calculate total
                        try {
                            String priceStr = item.getPrice().replace("₹", "").trim();
                            double price = Double.parseDouble(priceStr);
                            totalCost += price * item.getQuantity();
                        } catch (NumberFormatException e) {
                            // Handle invalid price format
                        }
                    }
                }
                cartAdapter.notifyDataSetChanged();
                // Update total cost
                if (totalCostTextView != null) {
                    totalCostTextView.setText(String.format("Total: ₹%.2f", totalCost));
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view_cart);
        if (recyclerView != null) {
            recyclerView.setVisibility(cartItems.isEmpty() ? View.GONE : View.VISIBLE);
        }
        // Hide footer when cart is empty
        LinearLayout footerLayout = findViewById(R.id.footer_layout);
        if (footerLayout != null) {
            footerLayout.setVisibility(cartItems.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total cost
        double totalCost = 0.0;
        for (CartItem item : cartItems) {
            try {
                String priceStr = item.getPrice().replace("₹", "").trim();
                double price = Double.parseDouble(priceStr);
                totalCost += price * item.getQuantity();
            } catch (NumberFormatException e) {
                // Handle invalid price format
            }
        }

        // Create order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("items", cartItems);
        orderData.put("totalCost", totalCost);
        orderData.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        // Save to Orders node
        String orderId = ordersRef.push().getKey();
        if (orderId == null) {
            Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ordersRef.child(orderId).setValue(orderData)
                .addOnSuccessListener(aVoid -> {
                    // Clear the cart
                    cartRef.removeValue()
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(CartActivity.this, "Order has been accepted!", Toast.LENGTH_SHORT).show();
                                // Cart will be cleared via Firebase listener
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CartActivity.this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}