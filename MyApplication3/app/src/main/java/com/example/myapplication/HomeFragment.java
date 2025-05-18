package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvTickets;
    private TextView tvEmptyHome;
    private RecyclerView rvHomePurchased;
    private PurchasedAdapter adapter;
    private List<CartItem> purchased = new ArrayList<>();
    private FirestoreRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGreeting      = view.findViewById(R.id.tvGreeting);
        tvTickets       = view.findViewById(R.id.tvTickets);
        tvEmptyHome     = view.findViewById(R.id.tvEmptyHome);
        rvHomePurchased = view.findViewById(R.id.rvHomePurchased);


        rvHomePurchased.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PurchasedAdapter(
                purchased,
                item -> showDeleteConfirmation(item)
        );
        rvHomePurchased.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            tvGreeting.setText("Üdv, kérlek jelentkezz be!");
            rvHomePurchased.setVisibility(View.GONE);
            tvTickets.setText("A megvásárolt jegy(ek) itt fog(nak) megjelenni:");
            return;
        }

        repo = new FirestoreRepository(user.getUid());


        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("fName");
                    tvGreeting.setText(
                            (name != null && !name.isEmpty())
                                    ? "Üdv, " + name + "!"
                                    : "Üdv, felhasználó!"
                    );
                })
                .addOnFailureListener(e ->
                        tvGreeting.setText("Üdv, felhasználó!")
                );


        listenPurchased();
    }

    private void listenPurchased() {
        repo.listenPurchasedItems((snap, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),
                        "Hiba: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, CartItem> merged = new LinkedHashMap<>();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                CartItem ci = doc.toObject(CartItem.class);
                if (ci == null) continue;
                ci.setId(doc.getId());
                String key = ci.getName();
                if (merged.containsKey(key)) {
                    CartItem old = merged.get(key);
                    old.setCount(old.getCount() + ci.getCount());
                } else {
                    merged.put(key, ci);
                }
            }
            purchased.clear();
            purchased.addAll(merged.values());
            adapter.notifyDataSetChanged();

            boolean empty = purchased.isEmpty();
            tvTickets.setText(
                    empty
                            ? "A megvásárolt jegy(ek) itt fog(nak) megjelenni:"
                            : "A megvásárolt jegy(ek):"
            );
            tvEmptyHome.setVisibility(View.GONE);
            rvHomePurchased.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }


    private void showDeleteConfirmation(CartItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Tétel törlése")
                .setMessage("Biztosan törlöd az összes \"" + item.getName() + "\" jegyet?")
                .setPositiveButton("Igen", (d, w) ->
                        repo.deletePurchasedItem(item.getId(), task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(),
                                        "Törlés hiba: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .setNegativeButton("Mégse", null)
                .show();
    }
}
