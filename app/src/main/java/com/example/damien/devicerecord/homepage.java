package com.example.damien.devicerecord;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class homepage extends AppCompatActivity {
    ImageView imgDrive;
    ImageView imgAddList;
    ImageView imgExpenses;
    TextView tvTotalBalance;

    private int id;
    private String username;
    private String password;

    public static final int REQUEST_CODE1 = 1;
    public static final int REQUEST_CODE6 = 6;
    public static final int REQUEST_CODE7 = 7;
    public static final int REQUEST_CODE11 = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Intent a = getIntent();
        id = a.getIntExtra(login.EXTRA_ID, -1);
        username = a.getStringExtra(login.EXTRA_USERNAME);
        password = a.getStringExtra(login.EXTRA_PASSWORD);

        imgDrive = findViewById(R.id.imgDrive);
        imgAddList = findViewById(R.id.imgEmployee);
        imgExpenses = findViewById(R.id.imgPayment);

        imgDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(homepage.this, device.class);
                startActivityForResult(i, REQUEST_CODE11);
            }
        });

        imgAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(homepage.this, add_list.class);
                startActivityForResult(i, REQUEST_CODE6);
            }
        });

        imgExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(homepage.this, expenses.class);
                i.putExtra(login.EXTRA_ID, id);
                startActivityForResult(i, REQUEST_CODE7);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE1){
            if (resultCode == RESULT_OK){
                username = data.getStringExtra("USERNAME_EDIT");
                password = data.getStringExtra("PASSWORD_EDIT");
                Toast.makeText(this, "Profile save successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE6){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Add list successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE11){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Add device successfully!", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == 9999){
                Toast.makeText(this, "Delete device successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void btnLogout_onClicked(View view) {
        Intent i = new Intent(homepage.this, login.class);
        startActivity(i);
    }

    public void btnProfile_onClicked(View view) {
        Intent b = new Intent(homepage.this, profile.class);
        b.putExtra(login.EXTRA_ID, id);
        b.putExtra(login.EXTRA_USERNAME, username);
        b.putExtra(login.EXTRA_PASSWORD, password);
        startActivityForResult(b, REQUEST_CODE1);
    }

}
