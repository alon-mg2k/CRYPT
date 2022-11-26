package com.example.crypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crypt.Adapter.MessageAdapter;
import com.example.crypt.Fragments.Model.Chat;
import com.example.crypt.Fragments.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference reference;

    ImageButton btn_send, btn_multi;
    EditText txt_send;
    LinearLayout btnDoc, btnImg;
    CardView cardView;

    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;

    String password="g4sr7t";

    public String userid;
    public int msgType;

    StorageReference storageReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private static final int IMAGE_REQUEST = 1;
    private Uri media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //CardFile = new Dialog(MessageActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(MessageActivity.this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(MessageActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        MessageActivity.this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }});


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //CardFile.setContentView(R.layout.selector_typefile);
        //CardFile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cardView = findViewById(R.id.multiCard);
        btnDoc = findViewById(R.id.item_file);
        btnImg = findViewById(R.id.item_img);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_multi = findViewById(R.id.btn_sendmulti);
        txt_send = findViewById(R.id.txt_send);

        storageReference = FirebaseStorage.getInstance().getReference("media");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        userid = getIntent().getStringExtra("userid");
        btn_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CardFile.show();
                openimg();
                //cardView.setVisibility(View.VISIBLE);

                btnImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Upload Image
                        openimg();
                        //CardFile.hide();
                        cardView.setVisibility(View.GONE);
                    }
                });
                btnDoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Upload File
                        openfiles();
                        //CardFile.hide();
                        cardView.setVisibility(View.GONE);
                    }
                });
                //openfiles();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txt_send.getText().toString();
                if(!msg.equals("")){
                    try{
                        msg = encriptar(msg, password);
                        msgType = 1;
                        sendMessage(fUser.getUid(), userid, msg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                txt_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getName());
                if(user.getImgURL().isEmpty()){
                    profile_image.setImageResource(R.drawable.ic_launcher_foreground);
                }else{
                    Glide.with(MessageActivity.this).load(user.getImgURL()).into(profile_image);
                }
                readMessage(fUser.getUid(), userid, user.getImgURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private SecretKey generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

    private void openimg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
        msgType = 2;
    }
    private void openfiles() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CONTEXT_INCLUDE_CODE);
        msgType = 3;
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("typeMss", msgType);
        data.child("Chats").push().setValue(hashMap);
    }
    private void readMessage(String myid, String userid, String imageurl){
        mchat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }



    /** Subir archivo media **/
    private String geyFileExtension(Uri uri){
        ContentResolver contentResolver = MessageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){

        final ProgressDialog pd = new ProgressDialog(MessageActivity.this);
        pd.setMessage("Subiendo");
        pd.show();
        if (media != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+geyFileExtension(media));

            uploadTask = fileReference.putFile(media);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        try{
                            sendMessage(fUser.getUid(), userid, encriptar(mUri, password));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{
            Toast.makeText(MessageActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            media = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(MessageActivity.this, "En proceso de subida", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }
}