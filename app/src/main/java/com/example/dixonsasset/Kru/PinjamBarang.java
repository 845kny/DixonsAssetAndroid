package com.example.dixonsasset.Kru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PinjamBarang extends AppCompatActivity {
    CodeScannerView codeScannerView;
    CodeScanner codeScanner;
    ImageView imageView;
    TextView nameItem;
    Util util;
    String id = null,username,namabarang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjam_barang);
        util = new Util();
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 2000);
        }
        nameItem = findViewById(R.id.nama_item_scan);
        imageView = findViewById(R.id.image_item_scan);
        codeScannerView = findViewById(R.id.scan);
        codeScanner = new CodeScanner(this,codeScannerView);
        codeScanner.setTouchFocusEnabled(true);
        codeScanner.setDecodeCallback(result -> this.runOnUiThread(() -> {
            id = result.getText();
            if (id.contains("kantor")){
                util.alert("Pemberitahuan","Untuk meminjam barang, silahkan Scan QR pada Barang", SweetAlertDialog.WARNING_TYPE,this);
                invisible();
                id = null;
                return;
            }
            try {
                new Instance().getFirebaseFirestore().collection("item").document(id).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        util.alert("Pemberitahuan","Gagal Ambil Data", SweetAlertDialog.WARNING_TYPE,this);
                        invisible();
                        id = null;
                        return;
                    }
                    if (!task.getResult().exists()){
                        util.alert("Pemberitahuan","Barang Tidak Ada", SweetAlertDialog.WARNING_TYPE,this);
                        invisible();
                        id = null;
                        return;
                    }
//                  berarti barang ada
                    String status = task.getResult().getString("status");
                    assert status != null;
                    if (status.equals("meminjam") || status.equals("rusak") || status.equals("hilang")){
                        util.alert("Pemberitahuan","Barang " + status == "meminjam" ? "Sedang Dipinjam" : status, SweetAlertDialog.WARNING_TYPE,this);
                        invisible();
                        id = null;
                        return;
                    }
                    id = result.getText();
                    namabarang = task.getResult().getString("nama");
                    visible();
                    Glide.with(this).load(task.getResult().getString("file")).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                    nameItem.setText(task.getResult().getString("nama"));
                });
            }catch (Exception exception){
                util.alert("Pemberitahuan","Silahkan Restart Hp Anda Jika Tidak Muncul Scanner",SweetAlertDialog.WARNING_TYPE,this);
            }
        }));
        codeScanner.startPreview();
        codeScannerView.setOnClickListener(view1 -> codeScanner.startPreview());
        findViewById(R.id.back_btn_pinjam_barang).setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
        findViewById(R.id.scan_pinjam_barang_button).setOnClickListener(v -> updateStatus(namabarang));
    }

    public void invisible(){
        findViewById(R.id.text_meminjam).setVisibility(View.INVISIBLE);
        findViewById(R.id.scan_layout).setVisibility(View.INVISIBLE);
    }
    public void visible(){
        findViewById(R.id.text_meminjam).setVisibility(View.VISIBLE);
        findViewById(R.id.scan_layout).setVisibility(View.VISIBLE);
    }
    private void updateStatus(String barang) {
        if (id == null || barang == null) {
            util.alert("Pemberitahuan","Tidak Dapat Meminjam Barang", SweetAlertDialog.WARNING_TYPE,PinjamBarang.this);
            return;
        }
        new Instance().getFirebaseFirestore().collection("peminjaman").whereEqualTo("id_item",id).whereEqualTo("username",username).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                util.alert("Pemberitahuan","Koneksi Internet Buruk", SweetAlertDialog.WARNING_TYPE,PinjamBarang.this);
                return;
            }
            if (!task.getResult().isEmpty()){
                util.alert("Pemberitahuan","Anda Telah Melakukan Request Peminjaman Sebelumnya, Silahkan Hubungi Admin!", SweetAlertDialog.WARNING_TYPE,PinjamBarang.this);
            }else {
                add(barang);
            }
        });
    }

    private void add(String barang){
        HashMap hashMap = new HashMap();
        hashMap.put("id_item",id);
        hashMap.put("barang",barang);
        hashMap.put("username",username);
        new Instance().getFirebaseFirestore().collection("peminjaman").add(hashMap).addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                util.alert("Pemberitahuan","Koneksi Internet Buruk", SweetAlertDialog.WARNING_TYPE,PinjamBarang.this);
                return;
            }
            util.alert("Pemberitahuan","Menunggu Konfirmasi Dari Admin", SweetAlertDialog.SUCCESS_TYPE,PinjamBarang.this);
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