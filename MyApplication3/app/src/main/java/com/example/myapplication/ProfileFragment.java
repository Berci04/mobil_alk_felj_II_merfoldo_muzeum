package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private EditText etName, etEmail, etPhone, etPassword;
    private ImageView ivTogglePassword;
    private Button btnSave, btnLogout;


    private boolean isPasswordVisible = false;

    private String actualPassword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.setTranslationX(view.getWidth());
        view.animate().translationX(0).setDuration(300).start();


        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        ivTogglePassword = view.findViewById(R.id.ivTogglePassword);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);


        etEmail.setEnabled(false);

        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
                isPasswordVisible = false;
            } else {
                etPassword.setTransformationMethod(null);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        loadUserData();

        btnSave.setOnClickListener(v -> showSaveConfirmationDialog());

        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }


    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("users").document(userId);

        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            actualPassword = documentSnapshot.getString("password");
            etName.setText(documentSnapshot.getString("fName"));
            etEmail.setText(documentSnapshot.getString("email"));
            etPhone.setText(documentSnapshot.getString("phone"));
            etPassword.setText(documentSnapshot.getString("password"));
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Hiba történt az adatok betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void showSaveConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Megerősítés")
                .setMessage("Biztosan frissíteni szeretnéd az adataidat?")
                .setPositiveButton("Igen", (dialog, which) -> saveUserData())
                .setNegativeButton("Mégse", null)
                .show();
    }


    private void saveUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("users").document(userId);

        java.util.Map<String, Object> updatedData = new java.util.HashMap<>();
        updatedData.put("fName", etName.getText().toString().trim());
        updatedData.put("phone", etPhone.getText().toString().trim());

        userDoc.update(updatedData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Adatok sikeresen frissítve.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Hiba a mentés során: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Kijelentkezés")
                .setMessage("Biztosan ki szeretnél jelentkezni?")
                .setPositiveButton("Igen", (dialog, which) -> logoutUser())
                .setNegativeButton("Mégse", null)
                .show();
    }


    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
