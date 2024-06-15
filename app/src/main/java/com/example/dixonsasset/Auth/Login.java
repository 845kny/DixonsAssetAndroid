package com.example.dixonsasset.Auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.Kru.Home;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    TextInputLayout layout_username,layout_password;
    TextInputEditText edittext_username,editText_password;
    MaterialButton login;
//            hubungi_admin;
    PackageManager packageManager;
    Intent i;
    String username,password,password_db,name;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        i = new Intent(Intent.ACTION_VIEW);
        util = new Util();
        packageManager = getPackageManager();
        layout_password = findViewById(R.id.TextLayout_Password);
        layout_username = findViewById(R.id.TextLayout_Username);
        editText_password = findViewById(R.id.TextEdit_Password);
        edittext_username = findViewById(R.id.TextEdit_Username);
        login = findViewById(R.id.button_login);
//        hubungi_admin = findViewById(R.id.button_whatsapp);

//        onclick hubungi admin
//        hubungi_admin.setOnClickListener(v -> {
//            if (cek_whatsapp("com.whatsapp")){
//                startActivity(i);
//            } else if (cek_whatsapp("com.whatsapp.w4b")) {
//                startActivity(i);
//            }else{
//                util.alert("Error","Whatsapp Tidak Ditemukan!",SweetAlertDialog.WARNING_TYPE,this);
//            }
//        });
//        onclick login
        login.setOnClickListener(v -> {
            if (!validasiinput()){
                util.alert("Pemberitahuan","Username dan password harus diisi",SweetAlertDialog.WARNING_TYPE,this);
            }else {
                username = Objects.requireNonNull(edittext_username.getText()).toString();
                try {
                    password = util.hashSHA256(editText_password.getText().toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                validasiakun();
            }
        });
    }
    private void validasiakun() {
        util.alert("Tunggu","",SweetAlertDialog.PROGRESS_TYPE,this);
        new Instance().getFirebaseFirestore().collection("pegawai").document(username).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                util.sweetAlertDialog.cancel();
                util.alert("Error","Gagal Menyambungkan Ke Internet",SweetAlertDialog.ERROR_TYPE,this);
                return;
            }
            if (!task.getResult().exists() || Objects.equals(task.getResult().getString("status"), "non-active")){
                util.sweetAlertDialog.cancel();
                util.alert("Pemberitahuan","Akun tidak ada",SweetAlertDialog.WARNING_TYPE,this);
                return;
            }
            name = task.getResult().getString("name");
            password_db = task.getResult().getString("password");
            if (password.equals(password_db)){
                makesession();
            }else {
                util.sweetAlertDialog.cancel();
                util.alert("Pemberitahuan","Username atau Password Salah",SweetAlertDialog.WARNING_TYPE,this);
            }
        });
    }

    private void makesession() {
        SharedPreferences sharedPreferences = getSharedPreferences("akun", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("name",name);
        if (editor.commit()){
            startActivity(new Intent(this, Home.class));
            finish();
        }else {
            util.alert("Error","Gagal Login, Silahkan Ulangi Lagi",SweetAlertDialog.ERROR_TYPE,this);
        }
    }

    private boolean validasiinput() {
        return Objects.requireNonNull(edittext_username.getText()).length() > 0 && Objects.requireNonNull(editText_password.getText()).length() >= 8;
    }


    protected boolean cek_whatsapp(String packages){
        i.setPackage(packages);
        i.setData(Uri.parse("https://api.whatsapp.com/send?phone=6282220918886"));
        return i.resolveActivity(packageManager) != null;
    }
}