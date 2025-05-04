package com.example.oops;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Entry extends AppCompatActivity {

    Button managementButton;
    Button coustemerButton;
    ImageView image ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entry);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        managementButton = findViewById(R.id.button);
        coustemerButton = findViewById(R.id.button2);
        image = findViewById(R.id.imageView2);

        Animation slideleft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        managementButton.post(() -> managementButton.startAnimation(slideleft));
        Animation slideright = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        coustemerButton.post(() -> coustemerButton.startAnimation(slideright));
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        image.post(() -> image.startAnimation(fadein));
        managementButton.setOnClickListener(v -> {
            Intent intent = new Intent(Entry.this, ManagementLogin.class);
            startActivity(intent);
        });

        coustemerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Entry.this, CustomerDashboard.class);
            startActivity(intent);
        });
    }
}
