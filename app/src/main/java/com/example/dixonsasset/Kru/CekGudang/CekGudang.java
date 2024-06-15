package com.example.dixonsasset.Kru.CekGudang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.Kru.Home;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CekGudang extends AppCompatActivity {
    ListView listView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_gudang);
        findViewById(R.id.back_btn_cek_gudang).setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
        listView = findViewById(R.id.listcekgudang);
        progressBar = findViewById(R.id.progresscekgudang);
        final ArrayList<CekGudangModel> arrayList = new ArrayList<>();
        new Instance().getFirebaseFirestore().collection("item").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list){
                            CekGudangModel cekGudangModel = new CekGudangModel(documentSnapshot.getId(), documentSnapshot.getString("nama"), documentSnapshot.getString("kategori"),documentSnapshot.getString("file"),documentSnapshot.getString("deskripsi"),documentSnapshot.getString("status"),documentSnapshot.getString("namapeminjam"), documentSnapshot.getString("tgl_dipinjam"),documentSnapshot.getString("lokasi"));
                            arrayList.add(cekGudangModel);
                        }
                        CekGudangAdapter cekGudangAdapter = new CekGudangAdapter(this,arrayList);
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(cekGudangAdapter);
                    }else {
                        progressBar.setVisibility(View.GONE);
                        new Util().alert("Informasi","Data Gudang Kosong", SweetAlertDialog.WARNING_TYPE,this);
                    }
                })
                .addOnFailureListener(e -> {
                        new Util().alert("Informasi","Data Gudang Kosong", SweetAlertDialog.WARNING_TYPE,this);
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}