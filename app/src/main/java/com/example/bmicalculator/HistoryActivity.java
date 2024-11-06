package com.example.bmicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = findViewById(R.id.listView);
        Button backButton = findViewById(R.id.btnBackToMain);

       
        SharedPreferences sharedPreferences = getSharedPreferences("BMI_HISTORY", MODE_PRIVATE);
        String historyData = sharedPreferences.getString("history", "[]");


     
        Gson gson = new Gson();
        ArrayList<HashMap<String, String>> historyList = gson.fromJson(
                historyData,
                new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType()
        );

        if (historyList == null || historyList.isEmpty()) {
            historyList = new ArrayList<>();
            HashMap<String, String> exampleEntry = new HashMap<>();
            exampleEntry.put("date", "31/10/2567");
            exampleEntry.put("weight", "65");
            exampleEntry.put("bmi", "23.03");
            exampleEntry.put("category", "ปกติ");
            historyList.add(exampleEntry);
        }

       
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                historyList,
                R.layout.activity_column,
                new String[]{"date", "weight", "bmi", "category"},
                new int[]{R.id.col_date, R.id.col_weight, R.id.col_bmi, R.id.col_category}
        );
        Collections.reverse(historyList);
        listView.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  
        });
    }
}