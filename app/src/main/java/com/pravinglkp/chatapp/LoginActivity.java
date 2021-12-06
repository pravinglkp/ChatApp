package com.pravinglkp.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail,editTextPassword;
    private Button buttonSignin,buttonSignup;
    private TextView textViewForgot;


    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        buttonSignin=findViewById(R.id.buttonSignin);
        buttonSignup=findViewById(R.id.buttonSignUp);
        textViewForgot=findViewById(R.id.textViewForgot);

        auth=FirebaseAuth.getInstance();

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextEmail.getText().toString();
                String password=editTextPassword.getText().toString();

                if(email!="" && password!=""){
                    signIn(email,password);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Enter Email and Password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
        textViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(i);
            }
        });

    }

    public void signIn(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                    Toast.makeText(LoginActivity.this,"Sign in Successful",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Sign in Failure",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}