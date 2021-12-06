package com.pravinglkp.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ForgetActivity extends AppCompatActivity {

    private TextInputEditText editTextForget;
    private Button buttonForget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        editTextForget=findViewById(R.id.editTextForget);
        buttonForget=findViewById(R.id.buttonForget);
    }
}