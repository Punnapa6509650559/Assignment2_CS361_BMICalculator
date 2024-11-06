package com.example.bmicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText weightInput, heightInput;
    private TextView bmiResult, bmiCategory;
    public Button calculateButton;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "BMI_HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weightInput = findViewById(R.id.etWeight);
        heightInput = findViewById(R.id.etHeight);
        bmiResult = findViewById(R.id.tvBmiResult);
        bmiCategory = findViewById(R.id.tvBmiCategory);
        calculateButton = findViewById(R.id.btnCalculate);
        ImageButton historyButton = findViewById(R.id.btnHistory); // เพิ่ม ImageButton
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        weightInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        heightInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        calculateButton.setOnClickListener(v -> calculateBMI());

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void calculateBMI() {
        String weightStr = weightInput.getText().toString();
        String heightStr = heightInput.getText().toString();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr) / 100;

            double bmi = weight / (height * height);
            DecimalFormat df = new DecimalFormat("#.##");
            bmiResult.setText(df.format(bmi));

            String category;
            int backgroundColor;

            if (bmi < 16) {
                category = getString(R.string.severe);
                backgroundColor = getColor(R.color.severeThinness);
            } else if (bmi < 17) {
                category = getString(R.string.moderate);
                backgroundColor = getColor(R.color.moderateThinness);
            } else if (bmi < 18.5) {
                category = getString(R.string.mild);
                backgroundColor = getColor(R.color.mildThinness);
            } else if (bmi < 23) {
                category = getString(R.string.normal);
                backgroundColor = getColor(R.color.normal);
            } else if (bmi < 25) {
                category = getString(R.string.overweight);
                backgroundColor = getColor(R.color.overweight);
            } else if (bmi < 30) {
                category = getString(R.string.obese1);
                backgroundColor = getColor(R.color.obeseClass1);
            } else if (bmi < 40) {
                category = getString(R.string.obese2);
                backgroundColor = getColor(R.color.obeseClass2);
            } else {
                category = getString(R.string.obese3);
                backgroundColor = getColor(R.color.obeseClass3);
            }

            bmiCategory.setText(category);
            bmiCategory.setBackgroundColor(backgroundColor);

            saveToHistory(weightStr, df.format(bmi), category);
        }
    }

    private void saveToHistory(String weight, String bmi, String category) {
        try {
            String historyJson = sharedPreferences.getString("history", "[]");
            JSONArray historyArray = new JSONArray(historyJson);

            JSONObject entry = new JSONObject();
            entry.put("date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date())); // จัดรูปแบบวันที่
            entry.put("weight", weight);
            entry.put("bmi", bmi);
            entry.put("category", category);
            historyArray.put(entry);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("history", historyArray.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class DecimalDigitsInputFilter implements InputFilter {
        private final Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("^[0-9]{0," + (digitsBeforeZero) + "}(\\.[0-9]{0," + (digitsAfterZero) + "})?$");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String newString = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
            if (!mPattern.matcher(newString).matches()) {
                return "";
            }
            return null;
        }
    }
}