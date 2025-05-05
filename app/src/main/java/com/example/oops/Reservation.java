package com.example.oops;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class Reservation extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_reservation);

        EditText etPartySize = findViewById(R.id.etPartySize);
        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker = findViewById(R.id.timePicker);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String partySizeStr = etPartySize.getText().toString().trim();
            if (partySizeStr.isEmpty()) {
                Toast.makeText(this, "Enter party size", Toast.LENGTH_SHORT).show();
                return;
            }
            int partySize = Integer.parseInt(partySizeStr);
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String date = year + "-" + (month < 10 ? "0" + month : month) + "-" +
                    (day < 10 ? "0" + day : day);

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);


        });
    }
}