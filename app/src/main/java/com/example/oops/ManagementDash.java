package com.example.oops;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManagementDash extends AppCompatActivity {
    Button menu;
    Button orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management_dash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menu = findViewById(R.id.button5);
        orders=findViewById(R.id.button6);

        menu.setOnClickListener(v->{
            Intent intent = new Intent( ManagementDash.this,Menuitems.class);
            startActivity(intent);
        });
        orders.setOnClickListener(v->{
            Intent intent= new Intent(ManagementDash.this,ViewOrders.class);
            startActivity(intent);
        });

    }
}