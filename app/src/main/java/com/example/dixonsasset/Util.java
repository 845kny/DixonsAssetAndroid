package com.example.dixonsasset;

import android.content.Context;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Util {
    public SweetAlertDialog sweetAlertDialog;

    public String hashSHA256(String input)
            throws NoSuchAlgorithmException {
        return Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
    }
    public void alert(String title, String message, int type, Context context){
        sweetAlertDialog = new SweetAlertDialog(context,type);
        sweetAlertDialog.setTitle(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmText("Oke");
        sweetAlertDialog.show();
    }
}
