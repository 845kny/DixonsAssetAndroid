package com.example.dixonsasset.Kru.CekGudang.Detail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dixonsasset.Auth.Login;
import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.Kru.CekGudang.CekGudang;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;

import org.w3c.dom.Text;

public class Detail extends AppCompatActivity {
    String nama,kategori,file,deskripsi,namapeminjam,tanggalpeminjam,lokasi;
    ImageView imageView;
    TextView namaTXT,kategoriTXT,deskripsiTXT,lokasiTXT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        findViewById(R.id.back_btn_detail).setOnClickListener(v -> {
            startActivity(new Intent(this, CekGudang.class));
            finish();
        });
        namaTXT = findViewById(R.id.detail_nama);
        kategoriTXT = findViewById(R.id.detail_kategori);
        deskripsiTXT = findViewById(R.id.detail_deskripsi);
        imageView = findViewById(R.id.detail_file);
        lokasiTXT = findViewById(R.id.detail_lokasi);
        nama = getIntent().getStringExtra("nama");
        kategori = getIntent().getStringExtra("kategori");
        deskripsi = getIntent().getStringExtra("deskripsi");
        file = getIntent().getStringExtra("file");
        lokasi = getIntent().getStringExtra("lokasi");
        namapeminjam = getIntent().getStringExtra("namapeminjam");
        tanggalpeminjam = getIntent().getStringExtra("tanggalpeminjam");
        Glide.with(this).load(file).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        namaTXT.setText(nama);
        kategoriTXT.setText(kategori);
        deskripsiTXT.setText(deskripsi);
        lokasiTXT.setText(lokasi);

        findViewById(R.id.detail_lihat_peminjam).setOnClickListener(v -> {
            alertDetail();
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, CekGudang.class));
        finish();
    }

    public void alertDetail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.detail_layout,null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        ConstraintLayout box = customLayout.findViewById(R.id.box_detail);
        TextView tdkadapeminjaman = customLayout.findViewById(R.id.text_tidak_ada_peminjam);
        if (namapeminjam.equals("") || tanggalpeminjam.equals("")){
            box.setVisibility(View.GONE);
            tdkadapeminjaman.setVisibility(View.VISIBLE);
        }else {
            TextView nama = customLayout.findViewById(R.id.nama_peminjam);
            nama.setText(namapeminjam);
            TextView tanggal = customLayout.findViewById(R.id.tanggal_peminjam);
            tanggal.setText(tanggalpeminjam);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        layoutParams.width = dialogWindowWidth;
        dialog.getWindow().setAttributes(layoutParams);
    }
}