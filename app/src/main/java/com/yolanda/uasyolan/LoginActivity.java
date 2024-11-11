package com.yolanda.uasyolan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText etusername, etpassword;
    String name;
    Button btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etusername=findViewById(R.id.etUsername);
        etpassword=findViewById(R.id.etpassword);
        btnlogin=findViewById(R.id.btnLogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etusername.getText().toString();
                String pass = etpassword.getText().toString();
                if(nama.trim().equals("")){
                    etusername.setError("Nama Harus DiIsi");
                }
                else if(pass.trim().equals("")){
                    etpassword.setError("Password Harus Di Isi");
                }else {
                    Intent pindah = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(pindah);
                    name = "Selamat Datang\t"+ etusername.getText().toString();
                    showToast(name);
                }
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(LoginActivity.this,text,Toast.LENGTH_SHORT).show();
    }
}