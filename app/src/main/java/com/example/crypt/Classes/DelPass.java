package com.example.crypt.Classes;

import androidx.annotation.NonNull;

import com.example.crypt.Fragments.Model.FilesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DelPass {
    private FilesModel filesModel;

    public DelPass(FilesModel filesModel) {
        this.filesModel = filesModel;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("FilesManager");
        db.child(filesModel.getFileAuthor()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()){
                    //Log.w("Name", ds.child("fileName").getValue().toString());
                    if (ds.child("fileName").getValue().equals(filesModel.getFileName())){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("filePass", "nullPass");
                        db.child(filesModel.getFileAuthor()).child(ds.getKey()).updateChildren(result);
                    }
                }
            }
        });
    }
}
