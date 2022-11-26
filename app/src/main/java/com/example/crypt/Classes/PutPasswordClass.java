package com.example.crypt.Classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class PutPasswordClass {
    private String NewPass, ConfNewPass, FileName;
    private int Code;
    private Context cContext;

    public PutPasswordClass(String newPass, String confNewPass, String fileName, Context mContext) {
        NewPass = newPass;
        ConfNewPass = confNewPass;
        FileName = fileName;
        cContext = mContext;

        Log.d("CODE", NewPass);

        // Verifica que los campos de EditText no esten vacios
        if (!NewPass.isEmpty() && !ConfNewPass.isEmpty()){
            // Verifica si la nueva contraseña coincide con la de confirmación
            if (NewPass.equals(ConfNewPass) && NewPass.length() > 3){
                Code = 0;
                //Toast.makeText(mContext, "La contraseña se actualizo con exito", Toast.LENGTH_SHORT).show();
            }else if (NewPass.length() <= 3){
                Code = 3;
                Toast.makeText(mContext, "Las contraseñas debe tener almenos 4 caracteres", Toast.LENGTH_SHORT).show();
            } else { // Indica que las contraseñas no coincidieron
                Code = 1;
                Toast.makeText(mContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        }else { // Indica que los campos estan vacios
            Code = 2;
            Toast.makeText(mContext, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void NewPasswordClass(String Pass, String userId){
        StorageReference storage = FirebaseStorage.getInstance().getReference("FilesManager").child(userId).child(FileName);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("Password", Pass).build();
        storage.updateMetadata(metadata).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Toast.makeText(cContext, "Contraseña de " + FileName + " actualizada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(cContext, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("FilesManager");

        db.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()){
                    if (ds.child("fileName").getValue().equals(FileName)){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("filePass", Pass);
                        db.child(userId).child(ds.getKey()).updateChildren(result);
                    }
                }
            }
        });
    }

    public int getCode() {return Code;}
}
