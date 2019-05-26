package com.example.makeup;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button button_register;
    TextView text_login;

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_register = findViewById(R.id.register);
        text_login = findViewById(R.id.text_login);

        firebaseAuth = FirebaseAuth.getInstance();

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("подождите пожалуйста...");
                progressDialog.show();

                String string_username = username.getText().toString();
                String string_fullname = fullname.getText().toString();
                String string_email = email.getText().toString();
                String string_password = password.getText().toString();

                if (TextUtils.isEmpty(string_username)
                        ||TextUtils.isEmpty(string_fullname)
                        ||TextUtils.isEmpty(string_email)
                        ||TextUtils.isEmpty(string_password)){
                    Toast.makeText(RegisterActivity.this,"Все поля обязательны для заполнения",Toast.LENGTH_SHORT).show();
                }else if (string_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Пароль должен состоять из 6 символов",Toast.LENGTH_SHORT).show();
                }else{
                    register(string_username,string_fullname,string_email,string_password);
                }
            }
        });
    }

    private void register (final String username, final String fullname, String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/makeup-dec13.appspot.com/o/user.png?alt=media&token=27c18ca5-a980-47e1-806c-848ffe1b8567");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Вы не можете зарегистрироваться " +
                                    "с этим адресом электронной почты или паролем",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
