package com.example.dixonsasset.Kru.CekGudang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dixonsasset.Kru.CekGudang.Detail.Detail;
import com.example.dixonsasset.R;
import java.util.ArrayList;

public class CekGudangAdapter extends ArrayAdapter<CekGudangModel> {
    public CekGudangAdapter(@NonNull Context context, ArrayList<CekGudangModel> arrayList){
        super(context,0,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup viewGroup) {
        View currentItemView = view;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_gudang_layout, viewGroup, false);
        }
        CekGudangModel cekGudangModel = getItem(position);
        assert cekGudangModel != null;
        TextView textView1 = currentItemView.findViewById(R.id.nama_item_gudang);
        textView1.setText(cekGudangModel.getNama());
        TextView textView2 = currentItemView.findViewById(R.id.category_item_gudang);
        textView2.setText(cekGudangModel.getKategori());
        ImageView imageView = currentItemView.findViewById(R.id.image_item_gudang);
        Glide.with(currentItemView).load(cekGudangModel.getFile()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        currentItemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Detail.class);
            intent.putExtra("nama",cekGudangModel.getNama());
            intent.putExtra("kategori",cekGudangModel.getKategori());
            intent.putExtra("deskripsi",cekGudangModel.getDeskripsi());
            intent.putExtra("file",cekGudangModel.getFile());
            intent.putExtra("lokasi",cekGudangModel.getLokasi());
            intent.putExtra("namapeminjam",cekGudangModel.getNamapeminjam());
            intent.putExtra("tanggalpeminjam",cekGudangModel.getTanggalpeminjam());
            v.getContext().startActivity(intent);
            ((Activity)v.getContext()).finish();
        });
        return currentItemView;
    }
}
