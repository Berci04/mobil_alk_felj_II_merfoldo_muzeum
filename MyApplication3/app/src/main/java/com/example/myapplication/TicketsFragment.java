package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {

    private RecyclerView rvTickets;
    private TicketAdapter ticketAdapter;
    private List<Ticket> ticketList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTickets = view.findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));


        ticketList = new ArrayList<>();
        ticketList.add(new Ticket("Normál belépőjegy", "Hozzáférés az állandó kiállításhoz", 1500));
        ticketList.add(new Ticket("Diák jegy", "Diák kedvezmény", 1000));
        ticketList.add(new Ticket("Nyugdíjas jegy", "Nyugdíjas kedvezmény", 800));
        ticketList.add(new Ticket("Csoportos jegy", "Csoportos kedvezmény 10 fő felett", 1200));
        ticketList.add(new Ticket("Különleges kiállítás jegy", "Tematikus kiállítás belépője", 2000));
        ticketList.add(new Ticket("Vezetett túra jegy", "Vezetett séta a múzeumban", 2500));
        ticketList.add(new Ticket("Audio guide jegy", "Hangos útmutató extra díj ellenében", 500));
        ticketList.add(new Ticket("VIP jegy", "VIP belépő és exkluzív előadás", 3500));
        ticketList.add(new Ticket("Workshop jegy", "Interaktív workshop a múzeumban", 1800));


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ticketAdapter = new TicketAdapter(ticketList, userId);
        rvTickets.setAdapter(ticketAdapter);


        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(300).start();
    }
}
