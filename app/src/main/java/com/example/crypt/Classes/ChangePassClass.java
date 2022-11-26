package com.example.crypt.Classes;

import android.content.Context;
import android.widget.Toast;

import com.example.crypt.Fragments.Model.FilesModel;


public class ChangePassClass {
    private String LastPass, NewPass, ConfPass;
    private int CODE;
    private Context mContext;
    private FilesModel model;

    public ChangePassClass(String lastPass, String newPass, String confPass, Context mContext, FilesModel model) {
        LastPass = lastPass;
        NewPass = newPass;
        ConfPass = confPass;
        this.mContext = mContext;
        this.model = model;

        if (!NewPass.isEmpty() && !ConfPass.isEmpty() && !LastPass.isEmpty()) {
            if (NewPass.length() <= 3) {
                CODE = 3;
                Toast.makeText(mContext, "Las contraseñas debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            } else if (!NewPass.equals(ConfPass)) {
                CODE = 1;
                Toast.makeText(mContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else { CODE = 0; }
        }else {
            CODE = 2;
            Toast.makeText(mContext, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
    public int getCODE() { return CODE; }
}
