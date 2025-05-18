package com.example.myapplication;

import com.example.myapplication.Ticket;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Tasks;



import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirestoreRepository {
    private static final String CART_COLLECTION = "carts";
    private final FirebaseFirestore db;
    private final String userId;

    public FirestoreRepository(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    public void addToCart(Ticket ticket, OnCompleteListener<Void> listener) {
        CollectionReference itemsRef = db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items");

        itemsRef
                .whereEqualTo("name", ticket.getName())
                .whereEqualTo("purchased", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.getDocuments().isEmpty()) {
                        Map<String,Object> data = new HashMap<>();
                        data.put("name", ticket.getName());
                        data.put("description", ticket.getDescription());
                        data.put("price", ticket.getPrice());
                        data.put("count", ticket.getCount());
                        data.put("purchased", false);
                        data.put("addedAt", FieldValue.serverTimestamp());
                        itemsRef.add(data)
                                .addOnCompleteListener(addTask -> {
                                    if (addTask.isSuccessful()) {
                                        listener.onComplete(Tasks.forResult(null));
                                    } else {
                                        listener.onComplete(Tasks.forException(addTask.getException()));
                                    }
                                });
                    } else {

                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        int updated = doc.getLong("count").intValue() + ticket.getCount();
                        itemsRef.document(doc.getId())
                                .update("count", updated)
                                .addOnCompleteListener(listener);
                    }
                })
                .addOnFailureListener(e -> {

                    listener.onComplete(Tasks.forException(e));
                });
    }


    public void listenCartItems(EventListener<QuerySnapshot> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .whereEqualTo("purchased", false)
                .orderBy("addedAt", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }

    public void purchaseCartItems(List<CartItem> cartItems,
                                  OnCompleteListener<Void> listener) {
        WriteBatch batch = db.batch();
        CollectionReference itemsRef = db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items");
        for (CartItem item : cartItems) {
            batch.update(itemsRef.document(item.getId()),
                    "purchased", true,
                    "purchaseDate", FieldValue.serverTimestamp());
        }
        // itt tesszük rá a callback-et
        batch.commit()
                .addOnCompleteListener(listener);
    }


    public void listenPurchasedItems(EventListener<QuerySnapshot> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .whereEqualTo("purchased", true)
                .orderBy("purchaseDate", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }


    public void deletePurchasedItem(String itemId,
                                    OnCompleteListener<Void> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .document(itemId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void updateCartItemCount(String itemId, int newCount,
                                    OnCompleteListener<Void> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .document(itemId)
                .update("count", newCount)
                .addOnCompleteListener(listener);
    }


    public void deleteCartItem(String itemId,
                               OnCompleteListener<Void> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .document(itemId)
                .delete()
                .addOnCompleteListener(listener);
    }


    public void clearCart(OnCompleteListener<Void> listener) {
        db.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit().addOnCompleteListener(listener);
                })
                .addOnFailureListener(e -> {
                    listener.onComplete( com.google.android.gms.tasks.Tasks.forException(e) );
                });
    }

}
