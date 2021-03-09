package com.example.damien.devicerecord;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class add_device extends AppCompatActivity {
    TextInputLayout layout_device;
    TextInputLayout layout_deviceName;
    TextInputLayout layout_deviceRemark;
    TextView tvImageURI;
    ImageView imgFromGallery;
    ImageView backButton;
    Button btnChooseImage;
    Button btnAddDevice;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        layout_device = findViewById(R.id.textInputDevice);
        layout_deviceName = findViewById(R.id.textInputDeviceName);
        layout_deviceRemark = findViewById(R.id.textInputDeviceRemark);
        tvImageURI = findViewById(R.id.tvImageURI);
        imgFromGallery = findViewById(R.id.imageFromGallery);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        backButton = findViewById(R.id.btnBack);
        btnAddDevice = findViewById(R.id.btnAddDevice);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateDevice() | !validateDeviceName() | !validateDeviceRemark()) {
                    return;
                } else {
                    openConfirmationDialog();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imgFromGallery.setImageURI(data.getData());
            tvImageURI.setText(data.getData().toString());
        }
    }

    private boolean validateDevice() {
        String deviceInput = layout_device.getEditText().getText().toString().trim();

        if (deviceInput.isEmpty()){
            layout_device.setError("This field cannot be empty!");
            return false;
        } else {
            layout_device.setError(null);
            return true;
        }
    }

    private boolean validateDeviceName() {
        String deviceNameInput = layout_deviceName.getEditText().getText().toString().trim();

        if (deviceNameInput.isEmpty()){
            layout_deviceName.setError("This field cannot be empty!");
            return false;
        } else {
            layout_deviceName.setError(null);
            return true;
        }
    }

    private boolean validateDeviceRemark() {
        String deviceRemarkInput = layout_deviceRemark.getEditText().getText().toString().trim();

        if (deviceRemarkInput.isEmpty()){
            layout_deviceRemark.setError("This field cannot be empty!");
            return false;
        } else {
            layout_deviceRemark.setError(null);
            return true;
        }
    }

    private void openConfirmationDialog() {
        final String deviceInput = layout_device.getEditText().getText().toString().trim();
        final String deviceNameInput = layout_deviceName.getEditText().getText().toString().trim();
        final String deviceRemarkInput = layout_deviceRemark.getEditText().getText().toString().trim();
        final String deviceImageURI = tvImageURI.getText().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(add_device.this);
        builder.setTitle("Are you sure want to add this device in the list?");
        builder.setMessage("Device            : " + deviceInput + "\n"
                        +  "Name/Model : " + deviceNameInput + "\n"
                        +  "Remark           : " + deviceRemarkInput);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Background bg = new Background();
                bg.execute(deviceInput, deviceNameInput, deviceRemarkInput, deviceImageURI);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do here
            }
        });

        builder.setIcon(R.drawable.ic_add_black);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt, stmt2;
        private ProgressDialog progressDialog;

        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(add_device.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            finally {
//                progressDialog.hide();
                try { result.close(); } catch (Exception e) { /* ignored */ }
                closeConn();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(add_device.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
//            progressDialog.show();
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;

            if (conn == null) {
                return null;
            }
            try {
                String query = "INSERT into device (name) " +
                               "SELECT ? " +
                               "FROM DUAL " +
                               "WHERE NOT EXISTS( " +
                                   "SELECT 1 " +
                                   "FROM device " +
                                   "WHERE name = ? " +
                               ") " +
                               "LIMIT 1";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, strings[0]);
                stmt.setString(2, strings[0]);

//                String query2 = "INSERT into device_type (device, name, remark, image_uri) values (?, ?, ?, ?)";
                String query2 = "INSERT into device_type (device, name, remark, image_uri) " +
                                "SELECT ?, ?, ?, ? " +
                                "FROM DUAL " +
                                "WHERE NOT EXISTS( " +
                                    "SELECT 1 " +
                                    "FROM device_type " +
                                    "WHERE device = ? AND name = ?" +
                                ") " +
                                "LIMIT 1";
                stmt2 = conn.prepareStatement(query2);
                stmt2.setString(1, strings[0]);
                stmt2.setString(2, strings[1]);
                stmt2.setString(3, strings[2]);
                stmt2.setString(4, strings[3]);
                stmt2.setString(5, strings[0]);
                stmt2.setString(6, strings[1]);

                stmt.executeUpdate();
                stmt2.executeUpdate();
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return result;
        }

        private Connection connectDB(){
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            } catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }

        public void closeConn() {
            try {
                stmt.close();
            } catch (Exception e) {
                /* ignored */
            }
            try {
                conn.close();
            } catch (Exception e) { /* ignored */ }
        }
    }
}
