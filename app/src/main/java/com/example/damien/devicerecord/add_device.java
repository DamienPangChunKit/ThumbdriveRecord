package com.example.damien.devicerecord;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class add_device extends AppCompatActivity {
    TextInputLayout layout_device;
    TextInputLayout layout_deviceName;
    TextInputLayout layout_deviceRemark;
    ImageView backButton;
    Button btnAddDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        layout_device = findViewById(R.id.textInputDevice);
        layout_deviceName = findViewById(R.id.textInputDeviceName);
        layout_deviceRemark = findViewById(R.id.textInputDeviceRemark);
        backButton = findViewById(R.id.btnBack);
        btnAddDevice = findViewById(R.id.btnAddDevice);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(add_device.this);
        builder.setTitle("Are you sure want to add this device in the list?");
        builder.setMessage("Device            : " + deviceInput + "\n"
                        +  "Name/Model : " + deviceNameInput + "\n"
                        +  "Remark           : " + deviceRemarkInput);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Background bg = new Background();
                bg.execute(deviceInput, deviceNameInput, deviceRemarkInput);
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
                String query = "INSERT into device (name) values (?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, strings[0]);

                String query2 = "INSERT into device_type (device, name, remark) values (?, ?, ?)";
                stmt2 = conn.prepareStatement(query2);
                stmt2.setString(1, strings[0]);
                stmt2.setString(2, strings[1]);
                stmt2.setString(3, strings[2]);

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
