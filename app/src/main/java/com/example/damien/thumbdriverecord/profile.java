package com.example.damien.thumbdriverecord;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class profile extends AppCompatActivity {
    public static final String USERNAME = "com.example.damien.thumbdriverecord.USERNAME";
    public static final String PASSWORD = "com.example.damien.thumbdriverecord.PASSWORD";
    public static final int REQUEST_CODE2 = 2;

    private int id;
    private String username;
    private String password;

    TextView tvUsername;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);

        Intent i = getIntent();
        id = i.getIntExtra(login.EXTRA_ID, -1);
        username = i.getStringExtra(login.EXTRA_USERNAME);
        password = i.getStringExtra(login.EXTRA_PASSWORD);

        tvUsername.setText(username);
        tvPassword.setText(password);
    }

    public void btnEditProfile_onClicked(View view) {
        Intent i = new Intent(profile.this, edit_profile.class);
        i.putExtra(login.EXTRA_ID, id);
        i.putExtra(profile.USERNAME, username);
        i.putExtra(profile.PASSWORD, password);
        startActivityForResult(i, REQUEST_CODE2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE2){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
