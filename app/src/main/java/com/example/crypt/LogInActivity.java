package com.example.crypt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    private Button button;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener lAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        txtbtn();
        button = findViewById(R.id.lbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IniciarUser();
            }
        });
        lAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    if(!user.isEmailVerified()){
                        Toast.makeText(LogInActivity.this, "Email no verificado: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }else{ startActivity(new Intent(LogInActivity.this, MainActivity.class)); }
                }else{ mAuth.signOut(); }
            }
        };
    }

    private void IniciarUser() {
        TextView mail = findViewById(R.id.ltxtmail);
        TextView pass = findViewById(R.id.ltxtpass);
        if (!mail.getText().toString().isEmpty() || !pass.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(mail.getText().toString(), pass.getText().toString()).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){ Toast.makeText(LogInActivity.this, "Las credenciales no coinciden", Toast.LENGTH_SHORT).show(); }
                }
            });
        }else{ Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show(); }
    }
    public void txtbtn(){
        TextView ltxtbtn = findViewById(R.id.ltxtbtn);
        ltxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, RegistroActivity.class));
                finish();
            }
        });
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
}