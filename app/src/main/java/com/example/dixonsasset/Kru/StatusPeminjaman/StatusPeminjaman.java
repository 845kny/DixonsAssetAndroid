package com.example.dixonsasset.Kru.StatusPeminjaman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class StatusPeminjaman extends AppCompatActivity {
    ListView listView;
    ProgressBar progressBar;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_peminjaman);
        findViewById(R.id.back_btn_status_peminjaman).setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);
        listView = findViewById(R.id.liststatuspeminjaman);
        progressBar = findViewById(R.id.progressstatuspeminjaman);
        final ArrayList<StatusPeminjamanModel> arrayList = new ArrayList<>();
        new Instance().getFirebaseFirestore().collection("item").whereEqualTo("status","meminjam").whereEqualTo("namapeminjam",username).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list){
                            StatusPeminjamanModel statusPeminjamanModel = new StatusPeminjamanModel(documentSnapshot.getId(), documentSnapshot.getString("nama"), documentSnapshot.getString("file"),documentSnapshot.getString("durasi"));
                            arrayList.add(statusPeminjamanModel);
                        }
                        StatusPeminjamanAdapter statusPeminjamanAdapter = new StatusPeminjamanAdapter(this,arrayList);
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(statusPeminjamanAdapter);
                    }else {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> new Util().alert("Informasi","Error", SweetAlertDialog.ERROR_TYPE,this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}