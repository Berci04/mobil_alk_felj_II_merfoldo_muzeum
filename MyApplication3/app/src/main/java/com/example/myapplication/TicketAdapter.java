package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;


public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> ticketList;
    private final FirestoreRepository repository;

    public TicketAdapter(List<Ticket> ticketList, String userId) {
        this.ticketList = ticketList;
        this.repository = new FirestoreRepository(userId);
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        holder.bind(ticketList.get(position));
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketName, tvTicketDescription, tvTicketPrice, tvCount;
        TextView btnDecrease, btnIncrease;
        ImageView btnAddTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketName        = itemView.findViewById(R.id.tvTicketName);
            tvTicketDescription = itemView.findViewById(R.id.tvTicketDescription);
            tvTicketPrice       = itemView.findViewById(R.id.tvTicketPrice);
            tvCount             = itemView.findViewById(R.id.tvCount);
            btnDecrease         = itemView.findViewById(R.id.btnDecrease);
            btnIncrease         = itemView.findViewById(R.id.btnIncrease);
            btnAddTicket        = itemView.findViewById(R.id.btnAddTicket);

            btnDecrease.setOnClickListener(v -> {
                Ticket ticket = ticketList.get(getAdapterPosition());
                int count = ticket.getCount();
                if (count > 0) {
                    ticket.setCount(count - 1);
                    tvCount.setText(String.valueOf(ticket.getCount()));
                }
            });

            btnIncrease.setOnClickListener(v -> {
                Ticket ticket = ticketList.get(getAdapterPosition());
                ticket.setCount(ticket.getCount() + 1);
                tvCount.setText(String.valueOf(ticket.getCount()));
            });

            btnAddTicket.setOnClickListener(v -> {
                Ticket ticket = ticketList.get(getAdapterPosition());
                int count = ticket.getCount();
                if (count <= 0) {
                    Toast.makeText(v.getContext(), "Először állíts be darabszámot!", Toast.LENGTH_SHORT).show();
                    return;
                }
                repository.addToCart(ticket, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            int total = ticket.getPrice() * ticket.getCount();
                            Toast.makeText(v.getContext(),
                                    ticket.getName() + " hozzáadva a kosárhoz. Összesen: " + total + " Ft",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(),
                                    "Hiba: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        }

        public void bind(Ticket ticket) {
            tvTicketName.setText(ticket.getName());
            tvTicketDescription.setText(ticket.getDescription());
            tvTicketDescription.setTextColor(0xFF800080);
            tvTicketDescription.setTextAppearance(itemView.getContext(), android.R.style.TextAppearance_Material_Body2);
            tvTicketDescription.setTypeface(tvTicketDescription.getTypeface(), android.graphics.Typeface.ITALIC);
            tvTicketPrice.setText(ticket.getPrice() + " Ft");
            tvCount.setText(String.valueOf(ticket.getCount()));
        }
    }
}
