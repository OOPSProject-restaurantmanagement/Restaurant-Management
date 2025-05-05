package com.example.oops;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Payment extends AppCompatActivity {

    private LinearLayout billItemsLayout, layoutQr, layoutCard;
    private TextView tvTotal;
    private Button btnQrPay, btnCardPay, btnSimulateQrPay, btnPay;
    private EditText etCardNumber, etCardExpiry, etCardCVV;
    private RatingBar ratingBar;

    private List<CartItem> orderItems = new ArrayList<>();
    private double orderTotalCost = 0.0;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_payment);

        billItemsLayout = findViewById(R.id.billItemsLayout);
        tvTotal = findViewById(R.id.tvTotal);
        layoutQr = findViewById(R.id.layoutQr);
        layoutCard = findViewById(R.id.layoutCard);
        btnQrPay = findViewById(R.id.btnQrPay);
        btnCardPay = findViewById(R.id.btnCardPay);
        btnSimulateQrPay = findViewById(R.id.btnSimulateQrPay);
        btnPay = findViewById(R.id.btnPay);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardExpiry = findViewById(R.id.etCardExpiry);
        etCardCVV = findViewById(R.id.etCardCVV);
        ratingBar = findViewById(R.id.ratingBar);

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Fetch the latest order from Firebase
        fetchLatestOrder();

        btnQrPay.setOnClickListener(v -> showQrLayout());
        btnCardPay.setOnClickListener(v -> showCardLayout());

        btnSimulateQrPay.setOnClickListener(v ->
                Toast.makeText(this, "QR Payment Successful!", Toast.LENGTH_LONG).show());

        btnPay.setOnClickListener(v -> processCardPayment());

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                Toast.makeText(this, "Thank you for rating: " + (int) rating + " stars!", Toast.LENGTH_SHORT).show();
            }
        });

        showQrLayout();
    }

    private void fetchLatestOrder() {
        ordersRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItems.clear();
                orderTotalCost = 0.0;

                if (snapshot.exists()) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        // Get total cost
                        Double totalCost = orderSnapshot.child("totalCost").getValue(Double.class);
                        if (totalCost != null) {
                            orderTotalCost = totalCost;
                        }

                        // Get items
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartItem item = itemSnapshot.getValue(CartItem.class);
                            if (item != null && item.getName() != null) {
                                orderItems.add(item);
                            }
                        }

                        // Get table number (optional, for display)
                        Integer tableNumber = orderSnapshot.child("tableNumber").getValue(Integer.class);
                        if (tableNumber != null) {
                            addBillLine("Table Number", tableNumber);
                        }
                    }
                    displayBill();
                } else {
                    Toast.makeText(Payment.this, "No orders found", Toast.LENGTH_SHORT).show();
                    tvTotal.setText("Grand Total: Rs. 0.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Payment.this, "Failed to load order: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                tvTotal.setText("Grand Total: Rs. 0.00");
            }
        });
    }

    private void displayBill() {
        billItemsLayout.removeAllViews();
        double subtotal = orderTotalCost;

        for (CartItem item : orderItems) {
            String name = item.getName();
            String priceStr = item.getPrice().replace("₹", "").trim();
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }
            int qty = item.getQuantity();
            double itemTotal = price * qty;

            TextView tv = new TextView(this);
            tv.setText(name + " x" + qty + " - Rs. " + String.format("%.2f", itemTotal));
            tv.setTextSize(15);
            tv.setPadding(8, 8, 8, 8);
            billItemsLayout.addView(tv);
        }

        double cgst = subtotal * 0.02;
        double sgst = subtotal * 0.02;
        double serviceCharge = subtotal * 0.01;
        double grandTotal = subtotal + cgst + sgst + serviceCharge;

        addBillLine("Subtotal", subtotal);
        addBillLine("CGST (2%)", cgst);
        addBillLine("SGST (2%)", sgst);
        addBillLine("Service Charge (1%)", serviceCharge);

        tvTotal.setText("Grand Total: Rs. " + String.format("%.2f", grandTotal));
    }

    private void addBillLine(String label, double value) {
        TextView tv = new TextView(this);
        tv.setText(label + ": Rs. " + String.format("%.2f", value));
        tv.setTextSize(15);
        tv.setPadding(8, 2, 8, 2);
        billItemsLayout.addView(tv);
    }

    private void addBillLine(String label, int value) {
        TextView tv = new TextView(this);
        tv.setText(label + ": " + value);
        tv.setTextSize(15);
        tv.setPadding(8, 2, 8, 2);
        billItemsLayout.addView(tv);
    }

    private void showQrLayout() {
        layoutQr.setVisibility(LinearLayout.VISIBLE);
        layoutCard.setVisibility(LinearLayout.GONE);
        btnQrPay.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        btnCardPay.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        btnQrPay.setTextColor(getResources().getColor(android.R.color.white));
        btnCardPay.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void showCardLayout() {
        layoutQr.setVisibility(LinearLayout.GONE);
        layoutCard.setVisibility(LinearLayout.VISIBLE);
        btnCardPay.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        btnQrPay.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        btnCardPay.setTextColor(getResources().getColor(android.R.color.white));
        btnQrPay.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void processCardPayment() {
        String cardNumber = etCardNumber.getText().toString().trim();
        String expiry = etCardExpiry.getText().toString().trim();
        String cvv = etCardCVV.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
            etCardNumber.setError("Enter a valid 16-digit card number");
            etCardNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(expiry) || !expiry.matches("\\d{2}/\\d{2}")) {
            etCardExpiry.setError("Enter expiry in MM/YY format");
            etCardExpiry.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            etCardCVV.setError("Enter a valid 3-digit CVV");
            etCardCVV.requestFocus();
            return;
        }

        Toast.makeText(this, "Card Payment Successful!", Toast.LENGTH_LONG).show();
    }
}