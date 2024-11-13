package com.example.hydracebeta;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText editTextAge, editTextHeight, editTextWeight;
    private Spinner spinnerGender;
    private Button buttonSave;
    private TextView textViewProfileInfo;

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        editTextAge = view.findViewById(R.id.age_input);
        editTextHeight = view.findViewById(R.id.height_input);
        editTextWeight = view.findViewById(R.id.weight_input);
        spinnerGender = view.findViewById(R.id.gender_spinner);
        buttonSave = view.findViewById(R.id.save_button);
//        textViewProfileInfo = view.findViewById(R.id.text_view_profile_info);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Load existing profile data if available
        loadUserProfile();

        // Save button action
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        return view;
    }

    // Method to load existing user profile from the database and display it
    private void loadUserProfile() {
        Cursor cursor = databaseHelper.getUserProfile();

        if (cursor.moveToFirst()) {
            String gender = cursor.getString(cursor.getColumnIndex("gender"));
            int age = cursor.getInt(cursor.getColumnIndex("age"));
            double height = cursor.getDouble(cursor.getColumnIndex("height"));
            double weight = cursor.getDouble(cursor.getColumnIndex("weight"));

            // Set the spinner selection based on gender
            if (gender != null) {
                if (gender.equals("Male")) {
                    spinnerGender.setSelection(0);
                } else if (gender.equals("Female")) {
                    spinnerGender.setSelection(1);
                }
            }

            // Set hints for age, height, and weight inputs
            editTextAge.setHint(String.valueOf(age));
            editTextHeight.setHint(String.valueOf(height));
            editTextWeight.setHint(String.valueOf(weight));
        }
        cursor.close();
    }

    // Method to save the user profile to the database
    private void saveProfileData() {
        String gender = spinnerGender.getSelectedItem().toString();

        // Use age hint if editTextAge is empty
        int age = editTextAge.getText().toString().isEmpty() ?
                Integer.parseInt(editTextAge.getHint().toString()) :
                Integer.parseInt(editTextAge.getText().toString());

        // Use height hint if editTextHeight is empty
        double height = editTextHeight.getText().toString().isEmpty() ?
                Double.parseDouble(editTextHeight.getHint().toString()) :
                Double.parseDouble(editTextHeight.getText().toString());

        // Use weight hint if editTextWeight is empty
        double weight = editTextWeight.getText().toString().isEmpty() ?
                Double.parseDouble(editTextWeight.getHint().toString()) :
                Double.parseDouble(editTextWeight.getText().toString());

        // Save data to the database
        databaseHelper.updateUserProfile(gender, age, height, weight);

        // Clear input fields and update hints
        editTextAge.setText("");
        editTextHeight.setText("");
        editTextWeight.setText("");
        editTextAge.setHint(String.valueOf(age));
        editTextHeight.setHint(String.valueOf(height));
        editTextWeight.setHint(String.valueOf(weight));

        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }
}
