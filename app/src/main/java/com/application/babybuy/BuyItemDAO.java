package com.application.babybuy;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class BuyItemDAO {
    private DatabaseReference databaseReference;


    public BuyItemDAO() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(BuyItem.class.getSimpleName());
    }

    public Task<Void> add(BuyItem buyItem) {
        return databaseReference.push().setValue(buyItem);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

    public Query get(){
        return databaseReference;
    }
}
