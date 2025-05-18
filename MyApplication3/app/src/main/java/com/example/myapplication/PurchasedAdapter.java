package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;                            // ADDED
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PurchasedAdapter
        extends RecyclerView.Adapter<PurchasedAdapter.VH> {

    public interface PurchaseActions {
        void onRemoveAll(CartItem item);
    }

    private final List<CartItem> items;
    private final PurchaseActions listener;

    public PurchasedAdapter(List<CartItem> items,
                            PurchaseActions listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CartItem ci = items.get(pos);
        h.tvName.setText(ci.getName());
        h.tvCount.setText("×" + ci.getCount());
        h.tvTotal.setText("Összesen: " + (ci.getPrice() * ci.getCount()) + " Ft");
        h.btnDeleteAll.setOnClickListener(v -> listener.onRemoveAll(ci)); // ADDED
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvCount, tvTotal;
        ImageView btnDeleteAll;

        VH(@NonNull View v) {
            super(v);
            tvName        = v.findViewById(R.id.tvName);
            tvCount       = v.findViewById(R.id.tvCount);
            tvTotal       = v.findViewById(R.id.tvTotal);
            btnDeleteAll  = v.findViewById(R.id.btnDeleteAll);
        }
    }
}
