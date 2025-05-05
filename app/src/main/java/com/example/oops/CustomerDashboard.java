package com.example.oops;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboard extends AppCompatActivity {

    Button viewMenuBtn, reservationBtn, paymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_customer_dashboard);

        viewMenuBtn = findViewById(R.id.viewMenuBtn);
        reservationBtn = findViewById(R.id.ReservationBtn);
        paymentBtn = findViewById(R.id.PayamentBtn);

        viewMenuBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerDashboard.this, SelectedMenuActivity.class);
            startActivity(intent);
        });

        reservationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerDashboard.this, Reservation.class);
            startActivity(intent);
        });

        paymentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerDashboard.this, Payment.class);
            startActivity(intent);
        });
    }
}