package com.example.oops;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class Reservation extends Activity {

    private DatabaseReference reservationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_reservation);

        // Initialize Firebase Database reference
        reservationsRef = FirebaseDatabase.getInstance().getReference("Reservations");

        EditText etPartySize = findViewById(R.id.etPartySize);
        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker = findViewById(R.id.timePicker);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String partySizeStr = etPartySize.getText().toString().trim();
            if (partySizeStr.isEmpty()) {
                Toast.makeText(this, "Enter number of people", Toast.LENGTH_SHORT).show();
                return;
            }

            int partySize;
            try {
                partySize = Integer.parseInt(partySizeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number of people", Toast.LENGTH_SHORT).show();
                return;
            }

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String date = year + "-" + (month < 10 ? "0" + month : month) + "-" +
                    (day < 10 ? "0" + day : day);

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);

            // Create a data map to store in Firebase
            Map<String, Object> reservationData = new HashMap<>();
            reservationData.put("partySize", partySize);
            reservationData.put("date", date);
            reservationData.put("time", time);

            // Push the data to Firebase
            String reservationId = reservationsRef.push().getKey();
            if (reservationId != null) {
                reservationsRef.child(reservationId).setValue(reservationData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Successfully Reserved", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to reserve: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Failed to generate reservation ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}