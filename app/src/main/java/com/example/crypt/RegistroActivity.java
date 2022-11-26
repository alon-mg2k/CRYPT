package com.example.crypt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RegistroActivity extends AppCompatActivity {

    private EditText username, email, pass, vpass;
    private String Name, Email, Pass, vPass;
    private Button rBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener lAuth;

    String password="g4sr7t";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth = FirebaseAuth.getInstance();
        /** Iniciar boton para Iniciar sesión **/
        txtbtn();
        /** Iniciar Registro de usuario **/
        username = findViewById(R.id.rtxtname);
        email = findViewById(R.id.rtxtmail);
        pass = findViewById(R.id.rtxtpass);
        vpass = findViewById(R.id.rtxtpassval);
        rBtn = findViewById(R.id.rbtn);

        mAuth = FirebaseAuth.getInstance();
        //String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = username.getText().toString();
                Email = email.getText().toString();
                Pass = pass.getText().toString();
                vPass = vpass.getText().toString();
                if(Validador(Pass, vPass, Name, Email)){
                    CrearUsuario(Email, Pass);
                }
            }
        });
        lAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    if(!user.isEmailVerified()){
                        Toast.makeText(RegistroActivity.this, "Verifica tu Email", Toast.LENGTH_SHORT).show();
                        user.sendEmailVerification();
                        String idUser = user.getUid();
                        try {
                            RegisterDataUser(idUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAuth.signOut();
                    }else{
                        Toast.makeText(RegistroActivity.this, "Bienvenido!!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                    }
                }
            }
        };
    }
    private void RegisterDataUser(String idUser) throws Exception {
        String Name, Mail, Pass;
        Name = username.getText().toString();
        Mail = email.getText().toString();
        Pass = pass.getText().toString();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> hashmap = new HashMap<>();
        hashmap.put("Name", Name);
        hashmap.put("Email", Mail);
        hashmap.put("Pass", encriptar(Pass, password));
        hashmap.put("imgURL", "");
        hashmap.put("idUser", idUser);
        hashmap.put("status", "");
        data.child("Users").child(idUser).setValue(hashmap);
    }
    public void txtbtn(){
        TextView rtxtbtn = findViewById(R.id.rtxtbtn);
        rtxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, LogInActivity.class));
                finish();
            }
        });
    }
    private void CrearUsuario(String mail, String password) {
        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(this, mail + "\n" + password, Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Toast.makeText(RegistroActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(RegistroActivity.this, "Usuario Creado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistroActivity.this, LogInActivity.class));
                }else{
                    Toast.makeText(RegistroActivity.this, "Hubo un Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean Validador(String password, String passwordval, String username, String email){
        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordval.isEmpty()){
            Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            //Log.d("TAG","\n"+password+"\n"+passwordVal);
            if(password.equals(passwordval)){
                if(password.length() >= 8){
                    return true;
                }else{
                    Toast.makeText(this, "La contraseña debe tener una longitud mayor o igual a 8 caracteres", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                vpass.setText("");
                return false;
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(lAuth);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (lAuth != null)
            mAuth.removeAuthStateListener(lAuth);
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
}