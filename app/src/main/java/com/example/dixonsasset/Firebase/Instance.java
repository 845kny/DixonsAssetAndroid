package com.example.dixonsasset.Firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class Instance {
    FirebaseFirestore firebaseFirestore;
    public FirebaseFirestore getFirebaseFirestore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        return firebaseFirestore;
    }
}
