package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvEmptyCart, tvTotalPrice;
    private Button btnEmptyCart, btnPurchase;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private FirestoreRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repo = new FirestoreRepository(uid);


        rvCart       = view.findViewById(R.id.rvCart);
        tvEmptyCart  = view.findViewById(R.id.tvEmptyCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnEmptyCart = view.findViewById(R.id.btnEmptyCart);
        btnPurchase  = view.findViewById(R.id.btnPurchase);


        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.CartActionsListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newCount) {

                item.setCount(newCount);
                cartAdapter.notifyDataSetChanged();
                updateUi();


                repo.updateCartItemCount(item.getId(), newCount, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Frissítés sikertelen: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onItemRemoved(CartItem item) {

                cartItems.remove(item);
                cartAdapter.notifyDataSetChanged();
                updateUi();


                repo.deleteCartItem(item.getId(), task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Tétel törlése sikertelen: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        rvCart.setAdapter(cartAdapter);


        btnEmptyCart.setOnClickListener(v ->
                repo.clearCart(task -> {
                    if (task.isSuccessful()) {
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateUi();
                    } else {
                        Toast.makeText(getContext(),
                                "Kosár ürítése sikertelen: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
        );


        btnPurchase.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "A kosár üres!", Toast.LENGTH_SHORT).show();
                return;
            }

            repo.purchaseCartItems(cartItems, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "Sikeres vásárlás!",
                            Toast.LENGTH_SHORT).show();
                    cartItems.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateUi();
                } else {
                    Toast.makeText(requireContext(),
                            "Hiba: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            repo.purchaseCartItems(cartItems, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Vásárlás sikeres!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });


        repo.listenCartItems(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(),
                            "Hiba a kosár lekérésekor: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                cartItems.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    CartItem item = doc.toObject(CartItem.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        cartItems.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateUi();
            }
        });
    }


    private void updateUi() {
        boolean empty = cartItems.isEmpty();
        tvEmptyCart.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvCart.setVisibility(empty ? View.GONE : View.VISIBLE);

        int sum = 0;
        for (CartItem item : cartItems) {
            sum += item.getPrice() * item.getCount();
        }
        tvTotalPrice.setText("Összeg: " + sum + " Ft");
    }
}
