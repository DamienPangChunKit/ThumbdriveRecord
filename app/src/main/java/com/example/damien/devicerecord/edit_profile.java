package com.example.damien.devicerecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class edit_profile extends AppCompatActivity {
    EditText mETUsername;
    EditText mETPassword;
    TextInputLayout layoutUsername;
    TextInputLayout layoutPassword;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-zA-Z])" +
                    "(.{8,})" +
                    "$");

    private int id;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        layoutUsername = findViewById(R.id.textInputUsername);
        layoutPassword = findViewById(R.id.textInputPassword);
        mETUsername = findViewById(R.id.etUsername);
        mETPassword = findViewById(R.id.etPassword);

        Intent i = getIntent();
        id = i.getIntExtra(login.EXTRA_ID, -1);
        username = i.getStringExtra(profile.USERNAME);
        password = i.getStringExtra(profile.PASSWORD);

        mETUsername.setText(username);
        mETPassword.setText(password);
    }

    private boolean validateUsername() {
        String usernameInput = layoutUsername.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            layoutUsername.setError("This field cannot be empty!");
            return false;
        } else if (usernameInput.length() > 15) {
            layoutUsername.setError("Username was too long!");
            return false;
        } else {
            layoutUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = layoutPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            layoutPassword.setError("This field cannot be empty!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            layoutPassword.setError("Password must contain at least 8 character, letter and number!");
            return false;
        } else {
            layoutPassword.setError(null);
            return true;
        }
    }

    public void btnSave_onClicked(View view) {
        if (!validateUsername() | !validatePassword()){
            return;
        } else {
            String usernameInput = layoutUsername.getEditText().getText().toString().trim();
            String passwordInput = layoutPassword.getEditText().getText().toString().trim();

            Background bg = new Background();
            bg.execute(usernameInput, passwordInput);
        }
    }

    public class Background extends AsyncTask<String, Void, String> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;
        private String usernameEdit;
        private String passwordEdit;

        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            closeConn();

            try {
                if (result.isEmpty()) {
                    String passwordInput = layoutPassword.getEditText().getText().toString();

                    Intent i = new Intent();
                    i.putExtra("USERNAME_EDIT", usernameEdit);
                    i.putExtra("PASSWORD_EDIT", passwordInput);
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(edit_profile.this, result, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(edit_profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(edit_profile.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            conn = connectDB();
            usernameEdit = strings[0];
            passwordEdit = strings[1];

            if (conn == null) {
                return null;
            }
            try {
                String query2 = "SELECT username FROM account WHERE (username LIKE ?) AND id <> ?";
                stmt = conn.prepareStatement(query2);
                stmt.setString(1, usernameEdit);
                stmt.setInt(2, id);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    return getString(R.string.username_exists);

                } else {
                    String query = "UPDATE account SET username=?, password=? WHERE id=?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, usernameEdit);
                    stmt.setString(2, passwordEdit);
                    stmt.setInt(3, id);
                    stmt.executeUpdate();
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return "";
        }

        private Connection connectDB() {
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            }
            catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }

        public void closeConn () {
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
    }
}
