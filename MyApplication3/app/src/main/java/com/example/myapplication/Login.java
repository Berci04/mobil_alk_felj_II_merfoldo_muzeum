package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private ImageView ivTogglePassword;
    private Button mLoginBtn;
    private TextView mCreateBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;


    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Módosított layout

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }


        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);


        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPasswordVisible) {

                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivTogglePassword.setImageResource(R.drawable.ic_visibility);
                    isPasswordVisible = false;
                } else {

                    mPassword.setTransformationMethod(null);
                    ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                    isPasswordVisible = true;
                }

                mPassword.setSelection(mPassword.getText().length());
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Az e-mail megadása kötelező.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("A jelszó megadása kötelező.");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("A jelszónak legalább 6 karakter hosszúnak kell lennie.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Bejelentkezés Firebase Auth segítségével
                fAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}
