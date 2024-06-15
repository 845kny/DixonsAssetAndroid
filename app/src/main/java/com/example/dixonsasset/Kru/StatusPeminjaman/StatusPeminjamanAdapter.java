package com.example.dixonsasset.Kru.StatusPeminjaman;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dixonsasset.Firebase.Instance;
import com.example.dixonsasset.Kru.Home;
import com.example.dixonsasset.R;
import com.example.dixonsasset.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class StatusPeminjamanAdapter extends ArrayAdapter<StatusPeminjamanModel> {
    public StatusPeminjamanAdapter(@NonNull Context context, ArrayList<StatusPeminjamanModel> arrayList){
        super(context,0,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup viewGroup) {
        View currentItemView = view;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_status_layout, viewGroup, false);
        }
        StatusPeminjamanModel statusPeminjamanModel = getItem(position);
        assert statusPeminjamanModel != null;
        TextView textView1 = currentItemView.findViewById(R.id.nama_item_status);
        textView1.setText(statusPeminjamanModel.getNama());
        ImageView imageView = currentItemView.findViewById(R.id.image_item_status);
        Date now = new Date();
        long currentDate = now.getTime();
        String dateTimeStr = statusPeminjamanModel.getDurasi();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy, HH.mm.ss");
        Date date = null;
        TextView note = currentItemView.findViewById(R.id.note);
        Button extend = currentItemView.findViewById(R.id.extend);
        CountdownView countdownView = currentItemView.findViewById(R.id.countdownView);
        try {
            date = sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long durasi = date.getTime();
        long count = durasi - currentDate;
        if (count <= 0){
            extend.setVisibility(View.VISIBLE);
            View finalCurrentItemView = currentItemView;
            extend.setOnClickListener(v -> {
                extendTime(statusPeminjamanModel.getId(), finalCurrentItemView.getContext());
            });
            countdownView.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }else {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.start(count);
            countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                @Override
                public void onEnd(CountdownView cv) {
                    note.setVisibility(View.VISIBLE);
                    extend.setVisibility(View.VISIBLE);
                }
            });
            note.setVisibility(View.GONE);
            extend.setVisibility(View.GONE);
        }
        Glide.with(currentItemView).load(statusPeminjamanModel.getFile()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        return currentItemView;
    }


    public void extendTime(String id,Context context){
        LocalDateTime myDateObj = LocalDateTime.now().plusHours(1);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/M/yyyy, HH.mm.ss");
        String formattedDate = myDateObj.format(myFormatObj);
        (new Instance()).getFirebaseFirestore().collection("item").document(id).update("durasi",formattedDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    (new Util()).alert("!","Gagal Extend waktu", SweetAlertDialog.ERROR_TYPE,context);
                }else {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitle("Pemberitahuan");
                    sweetAlertDialog.setContentText("Berhasil Menambah Waktu");
                    sweetAlertDialog.setConfirmText("Oke");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            context.startActivity(new Intent(context, Home.class));
                        }
                    });
                    sweetAlertDialog.show();
                }
            }
        });
    }
}

