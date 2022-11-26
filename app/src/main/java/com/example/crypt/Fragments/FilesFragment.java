package com.example.crypt.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crypt.Adapter.FileAdapter;
import com.example.crypt.Fragments.Model.FilesModel;
import com.example.crypt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FilesFragment extends Fragment {

    private FileAdapter mFileAdapter;
    private RecyclerView recyclerView;
    private ArrayList<FilesModel> mFiles;

    private Dialog renameDialog;


    FloatingActionButton btnFiles;
    ProgressDialog progressDialog;

    Uri fileUri;
    String UserId;
    String rename;
    String fileName;

    private boolean DuplicateName;

    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser fUser;

    private List<FilesModel> mFile;

    //private StorageReference mStorageRef;

    public static FilesFragment newInstance() {
        return new FilesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_fragment, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        UserId = fUser.getUid();

        recyclerView = view.findViewById(R.id.rvFilesList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        mFile = new ArrayList<>();
        renameDialog = new Dialog(getContext());
        //getData();

        //updateList();

        storage = FirebaseStorage.getInstance(); // Return an object of Firebase Storage
        database = FirebaseDatabase.getInstance(); // Return an object of Firebase DataBase

        configView(view); // Relaciona los Ids en una función

        /** ACTUALIZA LA LISTA CADA QUE UN ELEMENTO CAMBIE EN REALTIME DATABASE FIREBASE **/

        reference = database.getReference("FilesManager").child(UserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFile.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FilesModel file = dataSnapshot.getValue(FilesModel.class);
                    mFile.add(file);
                }
                mFileAdapter = new FileAdapter(getContext(), mFile);
                recyclerView.setAdapter(mFileAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Cancelado: " + error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
        /** FIN ACTUALIZA LA LISTA CADA QUE UN ELEMENTO CAMBIE EN REALTIME DATABASE FIREBASE **/

        // Acción del boton para agregar archivo        -- CONVERTIR EN BOTON FLOTANTE
        btnFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Permiso
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }else {
                    // Requiere una actividad       //Posible error
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });



        return view;
    }

    private void uploadfile(Uri fileUri, String fileName) {
        //DuplicateName = 0;      // 0 = No Exist     // 1 = Exist
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Subiendo archivo...");
        progressDialog.setProgress(0);
        progressDialog.show();

        storageReference = storage.getReference();

        storageReference.child("FilesManager").child(UserId).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()){
                    if (item.getName().equals(fileName)){
                        DuplicateName = true;
                        break;
                    }else {
                        DuplicateName = false;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", "No se pudo completar la tarea   Line:225");
            }
        }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (DuplicateName){
                    Toast.makeText(getContext(), "Ya existe un archivo con ese nombre", Toast.LENGTH_SHORT).show();
                    OpenRenameDialog(fileName, fileUri);
                    progressDialog.dismiss();
                }else {

                    storageReference.child("FilesManager").child(UserId).child(fileName).putFile(fileUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    /*String url = taskSnapshot.getUploadSessionUri().toString(); // Posible error
                                    DatabaseReference reference = database.getReference();*/
                                    getMeta(storageReference, fileName);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error 2", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setProgress(currentProgress);
                        }
                    });
                }
            }
        });
    }

    private void OpenRenameDialog(String fileName, Uri fileUri) {
        renameDialog.setContentView(R.layout.rename_dialog);
        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnAceptar = renameDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = renameDialog.findViewById(R.id.btnCancelar);
        EditText etNewName = renameDialog.findViewById(R.id.etNewName);

        etNewName.setText(fileName);
        etNewName.requestFocus();
        etNewName.selectAll();
        renameDialog.show();

        etNewName.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(etNewName, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 150);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename = etNewName.getText().toString();
                if (rename.equals("")){
                    Toast.makeText(getContext(), "Este campo no puede estar vacio", Toast.LENGTH_SHORT).show();
                }else {
                    rename = etNewName.getText().toString();
                    //Toast.makeText(getContext(), "Nombre establecido: " + rename, Toast.LENGTH_SHORT).show();
                    uploadfile(fileUri, rename);
                    renameDialog.hide();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename = System.currentTimeMillis() + "";
                //Toast.makeText(getContext(), "No se establecio ningun nombre", Toast.LENGTH_SHORT).show();
                //uploadfile(fileUri, rename);
                renameDialog.hide();
            }
        });
    }

    private void getMeta(StorageReference storageReference, String fileName) {
        StorageReference fileRef = storageReference.child("FilesManager").child(UserId).child(fileName);

        fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String Ext = storageMetadata.getContentType();
                assert Ext != null;
                Ext = Ext.substring(Ext.lastIndexOf("/")+1);
                updateMetaData(storageReference, fileName, Ext);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMetaData(StorageReference storageReference, String fileName, String fileExt) {
        StorageReference fileRef = storageReference.child("FilesManager").child(UserId).child(fileName);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("FilesManager").child(UserId);

        String defaultPass = "nullPass";
        StorageMetadata metadata = new StorageMetadata.Builder()    // Encriptar//
                .setCustomMetadata("Password", defaultPass)
                .setCustomMetadata("Type", fileExt)
                .setCustomMetadata("Author", UserId)
                .setCustomMetadata("URL", fileRef.getDownloadUrl().toString())
                .build();

        fileRef.updateMetadata(metadata)
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //Log.d("TAG", "Se actualizo la contraseña");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                HashMap<String, Object> result = new HashMap<>();   // Encriptar
                result.put("fileName", fileName);
                result.put("fileType", fileExt);
                result.put("filePass", defaultPass);
                result.put("fileAuthor", UserId);

                ref.push().setValue(result);

                Toast.makeText(getContext(), "Carga exitosa", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectFile();
        }else{
            Toast.makeText(getContext(), "Debes aceptar los permisos", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        //intent.setType("application/pdf | docx/*");
        intent.setType("*/*");
        //intent.setType("application/pdf"); // Cambiar despues de test
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 86 && resultCode == RESULT_OK && data != null){
            fileUri = data.getData();
            Log.d("URI", "Files: " + fileUri.toString());
            Log.d("TAG","PATH:   " + data.getType());
            /** ---EVITAR BOTON UPLOAD, SUBIR AL SELECCIONARLO--- **/
            if(fileUri != null){
                fileName = getFilename(fileUri) + ""; // Obtiene el nombre del archivo
                OpenRenameDialog(fileName, fileUri);
            }
            else
                Toast.makeText(getContext(), "Selecciona un archivo valido", Toast.LENGTH_SHORT).show();
            /** ---FIN EVITAR BOTON UPLOAD, SUBIR AL SELECCIONARLO--- **/
        }else {
            Toast.makeText(getContext(), "No seleccionaste ningun archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void configView(View view) {
        btnFiles = view.findViewById(R.id.btnUpFile);  // Boton agregar Archivo
    }

    public String getFilename(Uri uri)
    {
        String fileName = null;
        Context context = getContext();
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            String[] proj = { MediaStore.Images.Media.DISPLAY_NAME };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                Log.d("Name", fileName);
                if (fileName == null){
                    try{
                        File file= new File(uri.getPath());
                        String cadena = file.getName();
                        cadena = cadena.substring(cadena.indexOf(":")+1, cadena.lastIndexOf("."));
                        fileName = cadena;
                    }catch (Exception e){
                        fileName = System.currentTimeMillis() + "" ;
                    }
                }
            }
        }
        return fileName;
    }

}