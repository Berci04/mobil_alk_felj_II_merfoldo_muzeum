package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> items;
    private final CartActionsListener listener;


    public interface CartActionsListener {
        void onQuantityChanged(CartItem item, int newCount);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> items, CartActionsListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvTicketName.setText(item.getName());
        holder.tvCount.setText(String.valueOf(item.getCount()));

        holder.btnDecrease.setOnClickListener(v -> {
            int newCount = item.getCount() - 1;
            if (newCount >= 0) listener.onQuantityChanged(item, newCount);
        });
        holder.btnIncrease.setOnClickListener(v -> {
            listener.onQuantityChanged(item, item.getCount() + 1);
        });
        holder.btnDelete.setOnClickListener(v -> listener.onItemRemoved(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketName, tvCount;
        TextView btnDecrease, btnIncrease;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketName = itemView.findViewById(R.id.tvTicketName);
            btnDecrease  = itemView.findViewById(R.id.btnDecrease);
            tvCount      = itemView.findViewById(R.id.tvCount);
            btnIncrease  = itemView.findViewById(R.id.btnIncrease);
            btnDelete    = itemView.findViewById(R.id.btnDelete);
        }
    }
}
