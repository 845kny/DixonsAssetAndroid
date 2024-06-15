package com.example.dixonsasset.Kru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class laporan extends AppCompatActivity {
    Button imagebtn;
    String format, id, idhistory, namaBarang, kondisi, deskripsi, nama;
    ImageView imageView;
    Util util;
    TextView namaBarangTXT;
    RadioGroup radioGroup;
    EditText deskripsiED;
    RadioButton radioHilang, radioRusak;
    Uri returnUri = Uri.parse("");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        Intent intent = getIntent();
        util = new Util();
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        id = intent.getStringExtra("id");
        namaBarang = intent.getStringExtra("nama");
        idhistory = intent.getStringExtra("idhistory");
        nama = sharedPreferences.getString("name", null);
        format = nama + namaBarang;
        namaBarangTXT = findViewById(R.id.namaBarangLaporan);
        radioGroup = findViewById(R.id.groupRadioLaporan);
        radioHilang = findViewById(R.id.radioHilangLaporan);
        radioRusak = findViewById(R.id.radioRusakLaporan);
        namaBarangTXT.setText(namaBarang);
        imagebtn = findViewById(R.id.image_laporan_btn);
        imageView = findViewById(R.id.imageDetailLaporan);
        deskripsiED = findViewById(R.id.deskripsiLaporan);
        findViewById(R.id.back_btn_detail).setOnClickListener(v -> {
            startActivity(new Intent(this, KembalikanBarang.class));
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioHilang.isChecked()) {
                    kondisi = "hilang";
                } else if (radioRusak.isChecked()) {
                    kondisi = "rusak";
                }
            }
        });
        imagebtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 2000);
            } else {
                SelectImage();
            }
        });

        findViewById(R.id.laporanBTN).setOnClickListener(v -> {
            deskripsi = deskripsiED.getText().toString();
            check();
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (data != null) {
                    returnUri = data.getData();
                    imageView.setImageURI(returnUri);
                }
            }
        }
    }

    private void check() {
        StorageReference save = storageReference.child("laporan/" + format);
        save.getDownloadUrl().addOnSuccessListener(uri -> {
            save.delete();
            uploadImage();
        }).addOnFailureListener(e -> uploadImage());
    }

    private void uploadImage() {
        if (returnUri != null) {
            StorageReference save = storageReference.child("imageLaporan/" + format);
            save.putFile(returnUri).addOnSuccessListener(taskSnapshot -> getUrlImage()).addOnFailureListener(e -> {
                (new Util()).alert("error", "Gagal MengUpload Gambar", SweetAlertDialog.ERROR_TYPE, this);
            });
        } else {
            (new Util()).alert("Peringatan", "Silahkan Pilih Gambar", SweetAlertDialog.WARNING_TYPE, this);
        }
    }

    private void getUrlImage() {
        storageReference.child("imageLaporan/" + format).getDownloadUrl().addOnSuccessListener(uri -> save(uri.toString())).addOnFailureListener(e -> {
            (new Util()).alert("error", "Gagal Dapatkan Gambar!", SweetAlertDialog.ERROR_TYPE, this);
        });
    }

    private void save(String url) {
        HashMap hashMap = new HashMap<>();
        hashMap.put("namaBarang", namaBarang);
        hashMap.put("id", id);
        hashMap.put("deskripsi", deskripsi);
        hashMap.put("gambar", url);
        hashMap.put("username", nama);
        hashMap.put("kondisi", kondisi);
        new Instance().getFirebaseFirestore().collection("laporan").document(id).set(hashMap).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        util.alert("Pemberitahuan", "Pengembalian Gagal", SweetAlertDialog.WARNING_TYPE, this);
                        return;
                    }
                    updateStatus();
                }
        );
    }

    private void updateStatus() {
        if (id == null) {
            util.alert("Pemberitahuan", "Tidak Dapat Mengembalikan Barang", SweetAlertDialog.WARNING_TYPE, this);
            return;
        }
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        new Instance().getFirebaseFirestore().collection("history").document(idhistory).update("tgl_kembali", formattedDate).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        util.alert("Pemberitahuan", "Pengembalian Gagal", SweetAlertDialog.WARNING_TYPE, this);
                        return;
                    }
                }
        );
        new Instance().getFirebaseFirestore().collection("item").document(id).update("status", "ready",
                "namapeminjam", "", "tgl_dipinjam", "", "idhistory", "", "tgl_kembali", formattedDate).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                util.alert("Pemberitahuan", "Pengembalian Gagal", SweetAlertDialog.WARNING_TYPE, this);
                return;
            }
            util.alert("Pemberitahuan", "Berhasil Mengembalikan Barang", SweetAlertDialog.SUCCESS_TYPE, this);
        });
    }
}