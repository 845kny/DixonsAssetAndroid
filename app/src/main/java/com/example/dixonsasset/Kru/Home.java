package com.example.dixonsasset.Kru;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.dixonsasset.Auth.Login;
import com.example.dixonsasset.Kru.CekGudang.CekGudang;
import com.example.dixonsasset.Kru.StatusPeminjaman.StatusPeminjaman;
import com.example.dixonsasset.R;

public class Home extends AppCompatActivity {
    String name;
    TextView welcomemsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        welcomemsg = findViewById(R.id.welcome_msg);
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name",null);
        if (name != null){
            welcomemsg.setText("Hai, "+name);
        }
        findViewById(R.id.logout_button).setOnClickListener(v -> logout());
        findViewById(R.id.status_peminjaman_button).setOnClickListener(v -> startActivity(new Intent(this, StatusPeminjaman.class)));
        findViewById(R.id.pinjam_barang_button).setOnClickListener(v -> startActivity(new Intent(this,PinjamBarang.class)));
        findViewById(R.id.cek_gudang_button).setOnClickListener(v -> startActivity(new Intent(this, CekGudang.class)));
        findViewById(R.id.kembalikan_barang_button).setOnClickListener(v -> startActivity(new Intent(this,KembalikanBarang.class)));
    }

    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.logout_layout,null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        Button positive = customLayout.findViewById(R.id.konfirmasi);
        Button negative = customLayout.findViewById(R.id.batal);
        positive.setOnClickListener(view -> {
            SharedPreferences mSettings = getSharedPreferences("akun", Context.MODE_PRIVATE);
            mSettings.edit().clear().apply();
            dialog.dismiss();
            startActivity(new Intent(this, Login.class));
            finish();
        });
        negative.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.7f);
        layoutParams.width = dialogWindowWidth;
        dialog.getWindow().setAttributes(layoutParams);
    }

}