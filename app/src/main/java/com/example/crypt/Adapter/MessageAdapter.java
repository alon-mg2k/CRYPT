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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crypt.Fragments.Model.Chat;
import com.example.crypt.Fragments.Model.FilesModel;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int IMG_TYPE_LEFT = 2;
    public static final int IMG_TYPE_RIGHT = 3;
    public static final int FILE_TYPE_LEFT = 4;
    public static final int FILE_TYPE_RIGHT = 5;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private Dialog ValidationDialog;
    private String DbPass = "";
    private String StPass = "";
    private String password="g4sr7t";

    FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ValidationDialog = new Dialog(mContext);

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == IMG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_img_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == FILE_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_file_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == FILE_TYPE_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_file_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == IMG_TYPE_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_img_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);


        if (chat.getTypeMss() == 2){
            Glide.with(mContext).load(chat.getMessage()).into(holder.img_msg);
        }else if (chat.getTypeMss() == 3){
            putFile(holder, chat);
        }else if (chat.getTypeMss() == 1){
            holder.show_message.setText(chat.getMessage());
        }

        if(imageurl.isEmpty()){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
    }

    private void putFile(ViewHolder holder, Chat chat) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FilesManager").child(chat.getSender()).child(chat.getMessage());
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                FilesModel file = task.getResult().getValue(FilesModel.class);
                Log.d("USERID", file.getFileName());
                holder.fileName.setText(file.getFileName());


                if (file.getFilePass().equals("nullPass")){ holder.FileLock.setVisibility(View.GONE); }
                else { holder.FileLock.setVisibility(View.VISIBLE); }

                if (file.getFileType() != null){
                    switch (file.getFileType()) {
                        case "pdf":
                            holder.FileType.setImageResource(R.drawable.pdf);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "doc":
                            holder.FileType.setImageResource(R.drawable.doc);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "ppt":
                            holder.FileType.setImageResource(R.drawable.ppt);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "jpg":
                        case "jpeg":
                            holder.FileType.setImageResource(R.drawable.jpg);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "png":
                            holder.FileType.setImageResource(R.drawable.png);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "zip":
                        case "rar":
                            holder.FileType.setImageResource(R.drawable.zip);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "mp3":
                            holder.FileType.setImageResource(R.drawable.mp3);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "mp4":
                            holder.FileType.setImageResource(R.drawable.mp4);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "avi":
                            holder.FileType.setImageResource(R.drawable.avi);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "raw":
                            holder.FileType.setImageResource(R.drawable.raw);
                            holder.fileName.setText(file.getFileName());
                            break;
                        case "wmv":
                            holder.FileType.setImageResource(R.drawable.wmv);
                            holder.fileName.setText(file.getFileName());
                            break;
                        default:
                            holder.FileType.setImageResource(R.drawable.unknown);
                            holder.fileName.setText(file.getFileName() + "." + file.getFileType());
                            break;
                    }
                }else {
                    holder.FileType.setImageResource(R.drawable.txt);
                }
                holder.fileitemchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, file.getFileName(), Toast.LENGTH_SHORT).show();
                        if (file.getFilePass().equals("nullPass")){
                            //Toast.makeText(mContext, "Podras establecer una contraseña a este archivo", Toast.LENGTH_SHORT).show();
                            try {
                                DownloadFile(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            holder.FileLock.setVisibility(View.GONE);

                        }else {
                            openValidPass(file);
                        }
                    }
                });
            }
        });

    }
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
    public void openValidPass(FilesModel filesModel){

        ValidationDialog = ValidDgInit();

        TextView btnAceptar = ValidationDialog.findViewById(R.id.btnAceptar);
        TextView btnCancelar = ValidationDialog.findViewById(R.id.btnCancelar);
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
                    SearhData(filesModel, etPass.getText().toString(), etPass);
                }


            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDefaul(etPass);
                ValidationDialog.hide();
            }

        });

    }
    // Verifica si la contraseña es correcta cuando un Item estan protegido
    public void SearhData(FilesModel filesModel, String pass, EditText etPass){
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
                                Log.w("USERID", "Pass:   " + pass);
                                Log.w("USERID", "StPass: " + StPass);
                                Log.w("USERID", "DbPass: " + DbPass);
                                try {
                                    /*desencriptar(StPass, password);
                                    desencriptar(DbPass, password);*/

                                    if (pass.equals(desencriptar(StPass, password)) && pass.equals(desencriptar(DbPass, password))){
                                        DownloadFile(filesModel);
                                        ValidationDialog.hide();
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
            }
        });
    }
    public String desencriptar (String mensaje, String password) throws Exception{
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDeco = Base64.decode(mensaje, Base64.DEFAULT);
        byte[] datosDesencriptByte = cipher.doFinal(datosDeco);
        String datosDesencriptString = new String(datosDesencriptByte);
        return datosDesencriptString;
    }
    private SecretKey generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
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

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message, fileName;
        public ImageView profile_image, img_msg, FileLock, FileType;
        public CardView fileitemchat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_msg = itemView.findViewById(R.id.mess_img);
            fileName = itemView.findViewById(R.id.txtFileName);
            FileLock = itemView.findViewById(R.id.LockItem);
            FileType = itemView.findViewById(R.id.imgfileType);
            fileitemchat = itemView.findViewById(R.id.card_item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            if (mChat.get(position).getTypeMss() == 2){
                return IMG_TYPE_RIGHT;
            }else if (mChat.get(position).getTypeMss() == 3){
                return FILE_TYPE_RIGHT;
            }else {
                return MSG_TYPE_RIGHT;
            }
        }else {
            if (mChat.get(position).getTypeMss() == 2){
                return IMG_TYPE_LEFT;
            }else if (mChat.get(position).getTypeMss() == 3){
                return FILE_TYPE_LEFT;
            }else {
                return MSG_TYPE_LEFT;
            }
        }
    }
    public void typeMsg(int typeMss){
        if (typeMss == 1){
            //Texto
        }else if (typeMss == 2){
            //Multimedia
        }else  if (typeMss == 3){
            //Archivo
        }else {
            //Texto
        }
    }
}
