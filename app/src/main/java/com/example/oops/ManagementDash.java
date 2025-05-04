package com.example.oops;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

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
            Intent intent= new Intent(ManagementDash.this,SelectedMenuActivity.class);
            startActivity(intent);
        });

    }

    public static class DishUtils {
        private static final Map<String, Integer> dishImageMap = new HashMap<>();

        static {
            dishImageMap.put("IDLY", R.drawable.idly);
            dishImageMap.put("VADA", R.drawable.vada);
            dishImageMap.put("DOSA", R.drawable.dosa);
            dishImageMap.put("CHOLE BHATURE", R.drawable.cholebature);
            dishImageMap.put("POHA", R.drawable.poha);
            dishImageMap.put("CHOWMEIN", R.drawable.chowmein);
            dishImageMap.put("COFFEE", R.drawable.coffee);
            dishImageMap.put("TEA", R.drawable.tea);
            dishImageMap.put("BIRYANI", R.drawable.biryani);
            dishImageMap.put("TOMATO CURRY", R.drawable.tomatocurry);
        }

        public static int getImageResId(String dishName) {
            Integer imageResId = dishImageMap.get(dishName);
            return imageResId != null ? imageResId : 0; // Return 0 if dish name not found
        }
    }
}