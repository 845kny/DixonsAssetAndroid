package com.example.dixonsasset.Kru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KembalikanBarang extends AppCompatActivity {
    CodeScannerView codeScannerView;
    CodeScanner codeScanner;
    ImageView imageView;
    RadioGroup radioGroup;
    RadioButton radioBaik,RadioRusak;
    TextView nameItem;
    Button button;
    Util util;
    String id = null, posisi, username,idhistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kembalikan_barang);
        util = new Util();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 2000);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        button = findViewById(R.id.scan_kembalikan_barang_button);
        username = sharedPreferences.getString("username", null);
        nameItem = findViewById(R.id.nama_item_kembalikan_scan);
        imageView = findViewById(R.id.image_item_kembalikan_scan);
        codeScannerView = findViewById(R.id.scan_Kembalikan);
        radioBaik = findViewById(R.id.radioBaik);
        RadioRusak = findViewById(R.id.radioRusak);
        radioGroup = findViewById(R.id.groupRadio);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setTouchFocusEnabled(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkRadio();
            }
        });
        codeScanner.setDecodeCallback(result -> this.runOnUiThread(() -> {
            if (!result.getText().contains("kantor")) {
                util.alert("Pemberitahuan", "Kamu Sedang Tidak Di Kantor", SweetAlertDialog.WARNING_TYPE, this);
                invisible();
                id = null;
                return;
            }
            String[] arrOfStr = result.getText().split("_", 0);
            id = arrOfStr[0];
            posisi = arrOfStr[1];
            try {
                new Instance().getFirebaseFirestore().collection("item").document(id).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        util.alert("Pemberitahuan", "Koneksi Internet Buruk", SweetAlertDialog.WARNING_TYPE, this);
                        invisible();
                        id = null;
                        return;
                    }
                    if (!task.getResult().exists()) {
                        util.alert("Pemberitahuan", "Barang Tidak Ada", SweetAlertDialog.WARNING_TYPE, this);
                        invisible();
                        id = null;
                        return;
                    }
                    String status = task.getResult().getString("status");
                    assert status != null;
                    if (status.equals("ready")) {
                        util.alert("Pemberitahuan", "Anda tidak meminjam barang ini", SweetAlertDialog.WARNING_TYPE, this);
                        invisible();
                        id = null;
                        return;
                    }
                    if (!Objects.equals(task.getResult().getString("namapeminjam"), username)) {
                        util.alert("Pemberitahuan", "Kamu Bukan Peminjam", SweetAlertDialog.WARNING_TYPE, this);
                        invisible();
                        id = null;
                        return;
                    }
                    idhistory = task.getResult().getString("idhistory");
                    visible();
                    Glide.with(this).load(task.getResult().getString("file")).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                    nameItem.setText(task.getResult().getString("nama"));
                });
            } catch (Exception exception) {
                util.alert("Pemberitahuan", "Silahkan Restart Hp Anda Jika Tidak Muncul Scanner", SweetAlertDialog.WARNING_TYPE, this);
            }
        }));
        codeScanner.startPreview();
        codeScannerView.setOnClickListener(view1 -> codeScanner.startPreview());
        findViewById(R.id.back_btn_kembalikan_barang).setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
    }

    public void checkRadio(){
        if (RadioRusak.isChecked()){
            button.setText("Laporkan");
            button.setOnClickListener(v -> laporkan(id));
        }else if (radioBaik.isChecked()){
            button.setText("Kembalikan");
            button.setOnClickListener(v -> updateStatus(id));
        }
        button.setVisibility(View.VISIBLE);
    }

    private void laporkan(String id) {
        if (idhistory == "" || idhistory == null || nameItem.getText() == null || nameItem.getText() == ""){
            util.alert("Peringatan","Silahkan Scan Dulu",SweetAlertDialog.WARNING_TYPE,this);
            return;
        }
        Intent intent = new Intent(this, laporan.class);
        intent.putExtra("id",id);
        intent.putExtra("nama",nameItem.getText());
        intent.putExtra("idhistory",idhistory);
        startActivity(intent);
    }


    public void invisible() {
        findViewById(R.id.text_kembalikan).setVisibility(View.INVISIBLE);
        findViewById(R.id.scan_kembalikan_layout).setVisibility(View.INVISIBLE);
    }

    public void visible() {
        findViewById(R.id.text_kembalikan).setVisibility(View.VISIBLE);
        findViewById(R.id.scan_kembalikan_layout).setVisibility(View.VISIBLE);
    }

    private void updateStatus(String id) {
        if (id == null) {
            util.alert("Pemberitahuan", "Tidak Dapat Mengembalikan Barang", SweetAlertDialog.WARNING_TYPE, KembalikanBarang.this);
            return;
        }
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        new Instance().getFirebaseFirestore().collection("history").document(idhistory).update("tgl_kembali", formattedDate).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                util.alert("Pemberitahuan", "Pengembalian Gagal", SweetAlertDialog.WARNING_TYPE, KembalikanBarang.this);
                return;
            }}
        );
        new Instance().getFirebaseFirestore().collection("item").document(id).update("status", "ready",
                "namapeminjam", "","durasi","", "tgl_dipinjam", "","idhistory","", "tgl_kembali", formattedDate).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                util.alert("Pemberitahuan", "Pengembalian Gagal", SweetAlertDialog.WARNING_TYPE, KembalikanBarang.this);
                return;
            }
            util.alert("Pemberitahuan", "Berhasil Mengembalikan Barang", SweetAlertDialog.SUCCESS_TYPE, KembalikanBarang.this);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        codeScanner.startPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        codeScanner.startPreview();
    }
}