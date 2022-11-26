package com.example.crypt.Adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crypt.Classes.ChangePassClass;
import com.example.crypt.Classes.DelPass;
import com.example.crypt.Classes.PutPasswordClass;
import com.example.crypt.Fragments.Model.FilesModel;
import com.example.crypt.Fragments.Model.User;
import com.example.crypt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Context mContext;
    private List<FilesModel> mFiles;

    private String DbPass = "";
    private String StPass = "";
    private String rename;
    boolean DuplicateItem;
    private  Uri UriFileRename;
    private UserSendFileAdapter userAdapter;
    private List<User> mUsers;

    private String password="g4sr7t";
    private int counterPassError;

    private Dialog passDialog, optionsDialog, ValidationDialog, ConfirmDialog,
            renameDialog, changePassDialog, userListDialog, forgothPassDialog;

    public FileAdapter(Context mContext, List<FilesModel> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.file_item_pdf, parent, false);
        passDialog = new Dialog(mContext);
        optionsDialog = new Dialog(mContext);
        ValidationDialog = new Dialog(mContext);
        ConfirmDialog = new Dialog(mContext);
        renameDialog = new Dialog(mContext);
        changePassDialog = new Dialog(mContext);
        userListDialog = new Dialog(mContext);
        forgothPassDialog = new Dialog(mContext);

        mUsers = new ArrayList<>();
        return new FileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilesModel filesModel = mFiles.get(position);
        String typeFile = filesModel.getFileType();

        if (filesModel.getFilePass().equals("nullPass")){ holder.FileLock.setVisibility(View.GONE); }
        else { holder.FileLock.setVisibility(View.VISIBLE); }

        if (typeFile != null){
            switch (typeFile) {
                case "pdf":
                    holder.FileType.setImageResource(R.drawable.pdf);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "doc":
                    holder.FileType.setImageResource(R.drawable.doc);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "ppt":
                    holder.FileType.setImageResource(R.drawable.ppt);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "jpg":
                case "jpeg":
                    holder.FileType.setImageResource(R.drawable.jpg);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "png":
                    holder.FileType.setImageResource(R.drawable.png);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "zip":
                case "rar":
                    holder.FileType.setImageResource(R.drawable.zip);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "mp3":
                    holder.FileType.setImageResource(R.drawable.mp3);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "mp4":
                    holder.FileType.setImageResource(R.drawable.mp4);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "avi":
                    holder.FileType.setImageResource(R.drawable.avi);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "raw":
                    holder.FileType.setImageResource(R.drawable.raw);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                case "wmv":
                    holder.FileType.setImageResource(R.drawable.wmv);
                    holder.FileName.setText(filesModel.getFileName());
                    break;
                default:
                    holder.FileType.setImageResource(R.drawable.unknown);
                    holder.FileName.setText(filesModel.getFileName() + "." + filesModel.getFileType());
                    break;
            }
        }else {
            holder.FileType.setImageResource(R.drawable.txt);
        }

        holder.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: // Presionado
                        ChangeCardBackground(holder, mContext.getColor(R.color.light_blue));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ChangeCardBackground(holder, Color.WHITE);
                        break;
                }
                return false;
            }
        });

        // Accion al presionar el item del archivo
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, filesModel.getFileName(), Toast.LENGTH_SHORT).show();
                if (filesModel.getFilePass().equals("nullPass")){
                    //Toast.makeText(mContext, "Podras establecer una contraseña a este archivo", Toast.LENGTH_SHORT).show();

                    OpenOptionsDg(filesModel, filesModel.getFileName(), 0);
                    holder.FileLock.setVisibility(View.GONE);

                }else {
                    openValidPass(filesModel);
                }
                //return true;
            }
        });
        // Accion al mantener presionado el item del archivo
        /*holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //holder.view.setSelected(false);
                //showPopUpMenu(v);

            }
        });
*/
    }

    private void DownloadFile(FilesModel filesModel) throws IOException {

        StorageReference ref = FirebaseStorage.getInstance().getReference("FilesManager")
                .child(filesModel.getFileAuthor()).child(filesModel.getFileName());

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String type = storageMetadata.getContentType();
                        if (type.equals("image/jpeg") || type.equals("image/png")){
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri.toString()));
                            //String title = URLUtil.guessFileName(filesModel.getFileName(), null, null);
                            request.setTitle(filesModel.getFileName());
                            request.setDescription("Descargando...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS
                                    , filesModel.getFileName()+filesModel.getFileType());
                            DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                            downloadManager.enqueue(request);
                        }else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(intent);
                        }
                    }
                });

                Toast.makeText(mContext, "Descargando...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void OpenOptionsDg(FilesModel filesModel, String fileName, int type) {

        //  si type == 0    No tiene contraseña
        //  si type == 1    Tiene contraseña
        optionsDialog.setContentView(R.layout.menu_dialog);
        optionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        optionsDialog.setCanceledOnTouchOutside(true);

        TextView txtTitle = optionsDialog.findViewById(R.id.txtTitleDialog);
        TextView txtDwld = optionsDialog.findViewById(R.id.txtDowloadDialog);
        TextView txtShare = optionsDialog.findViewById(R.id.txtShareDialog);
        TextView txtRename = optionsDialog.findViewById(R.id.txtChangeName);
        TextView txtDelFile = optionsDialog.findViewById(R.id.txtDelFile);
        TextView txtNPass = optionsDialog.findViewById(R.id.txtNewPassDialog);
        TextView txtCPass = optionsDialog.findViewById(R.id.txtChangePassDialog);
        TextView txtDPass = optionsDialog.findViewById(R.id.txtDelPassDialog);


        String filePass = filesModel.getFilePass();

        if (type == 0){
            ImageView ivLock = optionsDialog.findViewById(R.id.LockItemDg);

            ivLock.setVisibility(View.GONE);
            txtCPass.setVisibility(View.GONE);
            txtDPass.setVisibility(View.GONE);
            txtRename.setVisibility(View.GONE);
        }else if (type == 1){
            txtNPass.setVisibility(View.GONE);
            txtRename.setVisibility(View.GONE);
        }

        txtTitle.setText(fileName);

        optionsDialog.show();

        /**
         * txtDwld    = 1
         * txtShare   = 2
         * txtRename  = 3
         * txtDelFile = 4
         * txtNPass   = 5
         * txtCPass   = 6
         * txtDPass   = 7
         * **/
        txtDwld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControllerCases(1, filesModel);
                optionsDialog.hide();
            }});
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControllerCases(2, filesModel);
                optionsDialog.hide();
            }});
        txtRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControllerCases(3, filesModel);
                optionsDialog.hide();
            }
        });
        txtDelFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControllerCases(4, filesModel);
                optionsDialog.hide();
            }
        });
        txtNPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // CODE = 3;
                ControllerCases(5, filesModel);
                optionsDialog.hide();
            }});
        txtCPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CODE = 4;
                ControllerCases(6, filesModel);
                optionsDialog.hide();
            }});
        txtDPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CODE = 5;
                ControllerCases(7, filesModel);
                optionsDialog.hide();
            }});
    }

    public void ControllerCases(int code, FilesModel filesModel){
        switch (code){
            case 1:
                try {
                    DownloadFile(filesModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                //Toast.makeText(mContext, "Compartir", Toast.LENGTH_SHORT).show();
                CompartirFile(filesModel);
                break;
            case 3:
                //Toast.makeText(mContext, "Renombrar archivo", Toast.LENGTH_SHORT).show();
                RenameFile(filesModel);
                break;
            case 4:
                //Toast.makeText(mContext, "Eliminar archivo", Toast.LENGTH_SHORT).show();
                String txtDesc4 = "¿Seguro qué quieres eliminar este archivo?";
                DelFile(txtDesc4, filesModel);
                break;
            case 5:
                openDialogPass(filesModel);
                //Toast.makeText(mContext, "Nueva Contraseña", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(mContext, "Cambiar contraseña", Toast.LENGTH_SHORT).show();
                ChangePass(filesModel);
                break;
            case 7:
                //Toast.makeText(mContext, "Eliminar contraseña", Toast.LENGTH_SHORT).show();
                String txtDesc7 = "¿Seguro qué quieres eliminar la contraseña de este archivo?";
                DelPassItem(txtDesc7, filesModel);
                break;
            default:
                break;
        }
    }

    private void ChangePass(FilesModel model) {
        changePassDialog.setContentView(R.layout.changepass_dialog);
        changePassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changePassDialog.setCanceledOnTouchOutside(false);

        EditText etLastPass  = changePassDialog.findViewById(R.id.etLastPass);
        EditText etNewPass   = changePassDialog.findViewById(R.id.etNewPass);
        EditText etConfPass  = changePassDialog.findViewById(R.id.etConfPass);
        TextView btnAceptar  = changePassDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = changePassDialog.findViewById(R.id.btnCancelar);

        changePassDialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference ref = FirebaseStorage.getInstance().getReference("FilesManager")
                        .child(model.getFileAuthor()).child(model.getFileName());
                ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        String StPass = storageMetadata.getCustomMetadata("Password");
                        try {
                            if (etLastPass.getText().toString().equals(desencriptar(StPass, password))){
                                ChangePassClass ChangePass = new ChangePassClass(
                                        etLastPass.getText().toString(),
                                        etNewPass.getText().toString(),
                                        etConfPass.getText().toString(),
                                        mContext, model
                                );
                                int Code = ChangePass.getCODE();
                                ControllCaseChangePass(Code, changePassDialog, model);
                            }else if (!etLastPass.getText().toString().equals(StPass)){
                                Log.d("ChangePass", "No es la contraseña correcta");
                                Toast.makeText(mContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                EditTextError(etLastPass);
                                etLastPass.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                                    @Override
                                    public void afterTextChanged(Editable s) { EditTextDefaul(etLastPass); }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //Log.d("ChangePass", "Cases: " + String.valueOf(Code));

            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDefaul(etLastPass);
                EditTextDefaul(etNewPass);
                EditTextDefaul(etConfPass);

                optionsDialog.show();
                changePassDialog.hide();
            }
        });
    }

    private void ControllCaseChangePass(int Code, Dialog dialog, FilesModel model) throws Exception {
        EditText etLastPass = dialog.findViewById(R.id.etLastPass);
        EditText etNewPass = dialog.findViewById(R.id.etNewPass);
        EditText etConfPass = dialog.findViewById(R.id.etConfPass);
        Log.d("ChangePass", String.valueOf(Code));
        switch (Code){
            case 0:
                Log.d("CODE", "Todo bien!");
                new PutPasswordClass(
                        etNewPass.getText().toString(), etConfPass.getText().toString()
                        , model.getFileName(), mContext)
                        .NewPasswordClass(encriptar(etNewPass.getText().toString(), password), model.getFileAuthor());
                //putPasswordClass.NewPasswordClass(Pass, fileAuthor);
                changePassDialog.hide();
                break;
            case 1:
                Log.d("CODE", "Las contraseñas no coinciden");
                EditTextError(etConfPass);

                etConfPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditTextDefaul(etConfPass);
                    }
                });
                break;
            case 2:
                Log.d("CODE", "Campos vacios");
                EditTextError(etNewPass);
                EditTextError(etConfPass);

                etLastPass.requestFocus();

                etLastPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditTextDefaul(etLastPass);
                        EditTextDefaul(etNewPass);
                        EditTextDefaul(etConfPass);
                    }
                });
                break;
            case 3:
                Log.d("ChangePass", "Caracteres insuficientes");
                EditTextError(etNewPass);
                EditTextError(etConfPass);
                EditTextDefaul(etLastPass);

                etNewPass.requestFocus();

                etNewPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditTextDefaul(etNewPass);
                        EditTextDefaul(etConfPass);
                    }
                });
                break;
            case 4:
                EditTextError(etLastPass);
                etLastPass.requestFocus();
                etLastPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditTextDefaul(etLastPass);
                    }
                });
                break;
            default:
                Log.d("CODE", "Algo salio mal");
                break;
        }
    }
    private void CompartirFile(FilesModel model) {
        Toast.makeText(mContext, "Al implementarlo en la app", Toast.LENGTH_SHORT).show();
        userListDialog.setContentView(R.layout.dialog_userfiles);
        userListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = userListDialog.findViewById(R.id.txtTitleDialog);

        String filePass = model.getFilePass();
        if (model.getFilePass().equals("nullPass")){
            ImageView ivLock = userListDialog.findViewById(R.id.LockItemDg);
            ivLock.setVisibility(View.GONE);
        }

        /*if (type == 0){
            ImageView ivLock = optionsDialog.findViewById(R.id.LockItemDg);

            ivLock.setVisibility(View.GONE);
            txtCPass.setVisibility(View.GONE);
            txtDPass.setVisibility(View.GONE);
            txtRename.setVisibility(View.GONE);
        }else if (type == 1){
            txtNPass.setVisibility(View.GONE);
            txtRename.setVisibility(View.GONE);
        }*/

        txtTitle.setText(model.getFileName());

        RecyclerView listadeUsers = userListDialog.findViewById(R.id.userlist);
        listadeUsers.setHasFixedSize(true);
        listadeUsers.setLayoutManager(new LinearLayoutManager(mContext));

        userListDialog.show();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    assert fUser != null;
                    if(!user.getIdUser().equals(fUser.getUid())){
                        mUsers.add(user);
                        //Log.d("USERID", );
                        rootFile(model, fUser.getUid(), listadeUsers);
                        Log.d("USERID", user.getName());
                    }
                }

            }
        });
    }
    private void rootFile(FilesModel model, String uid, RecyclerView listadeUsers){
        String idFile;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FilesManager").child(uid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()){
                    FilesModel file = ds.getValue(FilesModel.class);
                    if (file.getFileName().equals(model.getFileName())){
                        userAdapter = new UserSendFileAdapter(mContext, mUsers, ds.getKey(), uid, false);
                        listadeUsers.setAdapter(userAdapter);
                    }

                }

            }
        });
    }

    private void RenameFile(FilesModel model) {
        renameDialog.setContentView(R.layout.rename_dialog);
        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnAceptar = renameDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = renameDialog.findViewById(R.id.btnCancelar);
        EditText etNewName = renameDialog.findViewById(R.id.etNewName);

        etNewName.setText(model.getFileName());
        etNewName.requestFocus();
        etNewName.selectAll();
        renameDialog.show();

        etNewName.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(etNewName, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 150);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename = etNewName.getText().toString();

                if (rename.equals("")){
                    Toast.makeText(mContext, "Este campo no puede estar vacio", Toast.LENGTH_SHORT).show();
                    EditTextError(etNewName);
                    etNewName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) { }
                        @Override
                        public void afterTextChanged(Editable s) { EditTextDefaul(etNewName); }
                    });
                }else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference("FilesManager")
                            .child(model.getFileAuthor());
                    ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference item : listResult.getItems()){
                                if (item.getName().equals(rename)){
                                    DuplicateItem = true;
                                    break;
                                }else { DuplicateItem = false; }
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                            if (DuplicateItem){
                                Toast.makeText(mContext, "Ya existe un archivo con ese nombre", Toast.LENGTH_SHORT).show();
                                EditTextError(etNewName);
                                etNewName.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                                    @Override
                                    public void afterTextChanged(Editable s) { EditTextDefaul(etNewName); }
                                });
                            }else {
                                UpdateNameFile(model, rename);
                                renameDialog.hide();
                            }

                        }
                    });
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDefaul(etNewName);
                renameDialog.hide();
            }
        });
    }

    private void OpenRenameFileDg(FilesModel model) {


    }
    /** NO FUNCIONA **/
    private void UpdateNameFile(FilesModel model, String rename) {

        StorageReference storageSource = FirebaseStorage.getInstance().getReference("FilesManager")
                .child(model.getFileAuthor()).child(model.getFileName());

        StorageReference storageDestin = FirebaseStorage.getInstance().getReference("FilesManager")
                .child(model.getFileAuthor()).child("Rename").child(rename);



        storageSource.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                UriFileRename = uri;

                storageDestin.putFile(UriFileRename).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("URI", "Se logro...");
                        Toast.makeText(mContext, "El archivo se renombro", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "No se logro renombrar el archivo // No funciona", Toast.LENGTH_SHORT).show();
                        Log.d("URI", "No se logro...");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "No se logro renombrar el archivo", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("FilesManager")
                .child(model.getFileAuthor());

        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()){
                    if (ds.child("fileName").getValue().equals(model.getFileName())){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("fileName", rename);
                        db.child(ds.getKey()).updateChildren(result);
                    }
                }
            }
        });
    }

    private void DelFile(String txtDesc, FilesModel filesModel) {
        ConfirmDialog.setContentView(R.layout.confirmation_dialog);
        ConfirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ConfirmDialog.setCanceledOnTouchOutside(false);

        TextView txtDescription =  ConfirmDialog.findViewById(R.id.txtDescription);
        TextView btnAceptar = ConfirmDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = ConfirmDialog.findViewById(R.id.btnCancelar);

        txtDescription.setText(txtDesc);

        ConfirmDialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelFileInit(filesModel);
                ConfirmDialog.hide();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.show();
                ConfirmDialog.hide();
            }
        });
    }

    private void DelFileInit(FilesModel filesModel) {

        // Eliminar archivo de Storage
        StorageReference reference = FirebaseStorage.getInstance().getReference("FilesManager")
                .child(filesModel.getFileAuthor()).child(filesModel.getFileName());
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Archivo eliminado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "St: El archivo no se encontro", Toast.LENGTH_SHORT).show();
            }
        });

        // Eliminar archivo de Database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("FilesManager")
                .child(filesModel.getFileAuthor());
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()){
                    if (ds.child("fileName").getValue().equals(filesModel.getFileName())){
                        db.child(ds.getKey()).setValue(null);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Db: El archivo no se encontro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DelPassItem(String txtDesc, FilesModel filesModel) {
        ConfirmDialog.setContentView(R.layout.confirmation_dialog);
        ConfirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ConfirmDialog.setCanceledOnTouchOutside(false);

        TextView txtDescription =  ConfirmDialog.findViewById(R.id.txtDescription);
        TextView btnAceptar = ConfirmDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = ConfirmDialog.findViewById(R.id.btnCancelar);

        txtDescription.setText(txtDesc);

        ConfirmDialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noPass = "nullPass";
                DelPass delPass = new DelPass(filesModel);
                /*PutPasswordClass putPasswordClass = new PutPasswordClass(noPass, noPass, filesModel.getFileName(), mContext);
                putPasswordClass.NewPasswordClass(noPass, filesModel.getFileAuthor());*/
                ConfirmDialog.hide();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.show();
                ConfirmDialog.hide();
            }
        });

    }

    // Cambia el color del Item para representar que esta presionado
    public void ChangeCardBackground(ViewHolder holder, int color){
        Drawable drawable = holder.ItemBack.getBackground();
        drawable.setTint(color);
        holder.ItemBack.setBackground(drawable);
    }

    // Abre el cuadro para introducir una nueva contraseña
    private void openDialogPass(FilesModel filesModel) {

        String fileName = filesModel.getFileName();
        String fileAuthor = filesModel.getFileAuthor();

        passDialog.setContentView(R.layout.pass_dialog);
        passDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passDialog.setCanceledOnTouchOutside(false);

        TextView btnAceptar = passDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = passDialog.findViewById(R.id.btnCancelar);
        EditText etPass = passDialog.findViewById(R.id.etPass);
        EditText etConfPass = passDialog.findViewById(R.id.etConfPass);

        passDialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Pass, ConfPass;
                Pass = etPass.getText().toString();
                ConfPass = etConfPass.getText().toString();
                // Toast.makeText(mContext, "Contraseña establecida", Toast.LENGTH_SHORT).show();
                // passDialog.hide();
                PutPasswordClass putPasswordClass = null;
                try {
                    putPasswordClass = new PutPasswordClass(Pass, ConfPass, fileName, mContext);
                    Log.d("USERID", encriptar(Pass,password));
                    Log.d("USERID", encriptar(ConfPass,password));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int Code = putPasswordClass.getCode();

                switch (Code){
                    case 0:
                        Log.d("CODE", "Todo bien!");
                        try {
                            putPasswordClass.NewPasswordClass(encriptar(Pass,password), fileAuthor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        passDialog.hide();
                        break;
                    case 1:
                        Log.d("CODE", "Las contraseñas no coinciden");
                        EditTextError(etConfPass);

                        etConfPass.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                            @Override
                            public void afterTextChanged(Editable s) {
                                EditTextDefaul(etConfPass);
                            }
                        });
                        break;
                    case 2:
                        Log.d("CODE", "Campos vacios");
                        EditTextError(etPass);
                        EditTextError(etConfPass);

                        etPass.requestFocus();

                        etPass.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                            @Override
                            public void afterTextChanged(Editable s) {
                                EditTextDefaul(etPass);
                                EditTextDefaul(etConfPass);
                            }
                        });
                        etConfPass.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                            @Override
                            public void afterTextChanged(Editable s) {
                                EditTextDefaul(etPass);
                                EditTextDefaul(etConfPass);
                            }
                        });
                        break;
                    case 3:
                        Log.d("CODE", "Caracteres insuficientes");
                        EditTextError(etPass);
                        EditTextError(etConfPass);

                        etPass.requestFocus();

                        etPass.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                            @Override
                            public void afterTextChanged(Editable s) {
                                EditTextDefaul(etPass);
                                EditTextDefaul(etConfPass);
                            }
                        });
                    default:
                        Log.d("CODE", "Algo salio mal");
                        break;
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "No se establecio contraseña", Toast.LENGTH_SHORT).show();
                EditTextDefaul(etPass);
                EditTextDefaul(etConfPass);
                passDialog.hide();
                optionsDialog.show();
            }
        });
    }

    // Modifican los EditText para marcar un error
    public void EditTextError(EditText editText){
        Drawable drawable = editText.getBackground();
        drawable.setTint(Color.RED);
        editText.setBackground(drawable);
        editText.setTextColor(Color.RED);
    }
    public void EditTextDefaul(EditText editText){
        Drawable drawable = editText.getBackground();
        drawable.setTint(mContext.getColor(R.color.classic_blue));
        editText.setBackground(drawable);
        editText.setTextColor(Color.BLACK);
    }

    // Inicia el cuadro para introducir la contrasña de un Item Protegido
    public Dialog ValidDgInit(){
        ValidationDialog.setContentView(R.layout.valpass_dialog);
        ValidationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ValidationDialog.setCanceledOnTouchOutside(false);

        EditText etPass = ValidationDialog.findViewById(R.id.etPass);

        etPass.requestFocus();
        etPass.selectAll();

        ValidationDialog.show();

        etPass.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(etPass, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 150);
        return ValidationDialog;
    }

    // Valida que la contraseña de un Item protegido sea la correcta
    public void openValidPass(FilesModel filesModel){
        counterPassError = 0;

        ValidationDialog = ValidDgInit();

        TextView btnAceptar = ValidationDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = ValidationDialog.findViewById(R.id.btnCancelar);
        TextView txtforgot = ValidationDialog.findViewById(R.id.forgothPass);
        EditText etPass = ValidationDialog.findViewById(R.id.etPass);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Pass = etPass.getText().toString();


                if (Pass.isEmpty()){
                    Toast.makeText(mContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    EditTextError(etPass);
                    etPass.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) { }
                        @Override
                        public void afterTextChanged(Editable s) { EditTextDefaul(etPass); }
                    });
                }else {
                    SearhData(filesModel, etPass.getText().toString(), etPass, txtforgot);
                }


            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDefaul(etPass);
                counterPassError = 0;
                ValidationDialog.hide();
            }

        });

    }
    // Verifica si la contraseña es correcta cuando un Item estan protegido
    public void SearhData(FilesModel filesModel, String pass, EditText etPass, TextView txtforgot){
        // Extraer los datos
        StorageReference storage = FirebaseStorage.getInstance().getReference("FilesManager")
                .child(filesModel.getFileAuthor()).child(filesModel.getFileName());

        // Inicio la funcion para leer la metadata del archivo
        storage.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                StPass = storageMetadata.getCustomMetadata("Password");
                Log.w("Key2", "2 " + StPass);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "No se pudo obtener la Metadata del archivo", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                // Inicia la busqueda de datos en Db
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("FilesManager")
                        .child(filesModel.getFileAuthor());

                db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot ds : task.getResult().getChildren()){
                            if (ds.child("fileName").getValue().equals(filesModel.getFileName())){
                                DbPass = ds.child("filePass").getValue().toString();
                                /*Log.w("USERID", "Pass:   " + pass);
                                Log.w("USERID", "StPass: " + StPass);
                                Log.w("USERID", "DbPass: " + DbPass);*/
                                try {
                                    /*desencriptar(StPass, password);
                                    desencriptar(DbPass, password);*/
                                    if (pass.equals(desencriptar(StPass, password)) && pass.equals(desencriptar(DbPass, password))){
                                        OpenOptionsDg(filesModel, filesModel.getFileName(), 1);
                                        counterPassError = 0;
                                        ValidationDialog.hide();
                                    }else {
                                        Toast.makeText(mContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                        counterPassError++;
                                        if (counterPassError >= 3){
                                            txtforgot.setVisibility(View.VISIBLE);
                                            txtforgot.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        EditTextDefaul(etPass);
                                                        ValidationDialog.hide();
                                                        IniciarUser(filesModel);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }else {
                                            txtforgot.setVisibility(View.GONE);
                                        }
                                        EditTextError(etPass);
                                        etPass.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                                            @Override
                                            public void afterTextChanged(Editable s) { EditTextDefaul(etPass); }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
                });
            }
        });
    }
    private void IniciarUser(FilesModel filesModel) throws Exception {
        forgothPassDialog.setContentView(R.layout.dialog_forgotpass);
        forgothPassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forgothPassDialog.setCanceledOnTouchOutside(false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        //TextView mail = forgothPassDialog.findViewById(R.id.ltxtmail);
        EditText etPass = forgothPassDialog.findViewById(R.id.etPass2);
        TextView btnAceptar = forgothPassDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = forgothPassDialog.findViewById(R.id.btnCancelar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        forgothPassDialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etPass.getText().toString().isEmpty()){
                    reference.child(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            //Log.d("USERID", task.getResult().getChildren().toString());

                            for (DataSnapshot ds : task.getResult().getChildren()){

                                if (ds.getKey().equals("Pass")){
                                    try {
                                        if (etPass.getText().toString().equals(desencriptar(ds.getValue().toString(), password))){
                                            DelPassItem("La contraseña de este archivo se borrara", filesModel);
                                            counterPassError = 0;
                                            forgothPassDialog.hide();
                                            //Log.d("USERID", "Olvido contra: " + desencriptar(txtPass, password));
                                        }else {
                                            Toast.makeText(mContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                            EditTextError(etPass);
                                            etPass.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                                                @Override
                                                public void afterTextChanged(Editable s) { EditTextDefaul(etPass); }
                                            });
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    });


                }else{
                    Toast.makeText(mContext, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                    EditTextError(etPass);
                    etPass.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) { }
                        @Override
                        public void afterTextChanged(Editable s) { EditTextDefaul(etPass); }
                    });
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDefaul(etPass);
                counterPassError = 0;
                forgothPassDialog.hide();
            }
        });

    }
    private String encriptar(String msg, String password) throws Exception{
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(msg.getBytes());
        String datosEncriptadosString = android.util.Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        Log.d("TAG", datosEncriptadosString);
        return datosEncriptadosString;
    }
    public String desencriptar (String mensaje, String password) throws Exception{
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDeco = Base64.decode(mensaje, Base64.DEFAULT);
        byte[] datosDesencriptByte = cipher.doFinal(datosDeco);
        return new String(datosDesencriptByte);
    }
    private SecretKey generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView FileName;
        private ImageView FileType, FileLock;
        private CardView ItemBack;
        public View view;

        public ViewHolder(View view){
            super(view);
            this.view = view;
            this.FileName = view.findViewById(R.id.txtFileName);
            this.FileType = view.findViewById(R.id.imgfileType);
            this.ItemBack = view.findViewById(R.id.card_item);
            this.FileLock = view.findViewById(R.id.LockItem);
        }
    }


}
