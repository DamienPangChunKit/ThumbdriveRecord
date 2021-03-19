package com.example.damien.devicerecord;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class delete_device extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView backButton;
    Spinner mSpinner;

    private int[] deviceID;
    private String[] deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);

        backButton = findViewById(R.id.btnBack);
        mSpinner = findViewById(R.id.spinnerDeleteDevice);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSpinner.setOnItemSelectedListener(this);
        new Background(Background.FETCH_DEVICE).execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, deviceName[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
    }

    public void btnDeleteDevices_onClicked(View view) {
        final String deviceDeleteInput = mSpinner.getSelectedItem().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(delete_device.this);
        builder.setTitle("Are you sure want to delete " + deviceDeleteInput + " ?");
        builder.setMessage("Once delete it will not exists anymore!");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Background bg = new Background(Background.DELETE_DEVICE); // delete from the device table
                bg.execute(deviceDeleteInput);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do here
            }
        });

        builder.setIcon(R.drawable.ic_delete);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        public static final int FETCH_DEVICE = 300;
        public static final int DELETE_DEVICE = 400;

        private int method;
        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }
        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                switch (this.method) {
                    case FETCH_DEVICE:
                        result.last();
                        int totalRow = result.getRow();
                        result.first();
                        deviceID = new int[totalRow];
                        deviceName = new String[totalRow];
                        for (int i = 0; i < totalRow; i++) {
                            deviceID[i] = result.getInt(1);
                            deviceName[i] = result.getString(2);
                            result.next();
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner, deviceName);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinner.setAdapter(arrayAdapter);
                        break;

                    case DELETE_DEVICE:
                        Intent i = new Intent();
                        setResult(99999, i);
                        finish();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(delete_device.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(delete_device.this);
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
                String query = "";
                switch (this.method) {
                    case FETCH_DEVICE:
                        query = "SELECT id, name FROM device";
                        stmt = conn.prepareStatement(query);
                        return stmt.executeQuery();

                    case DELETE_DEVICE:
                        query = "DELETE FROM device WHERE name = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, strings[0]);
                        stmt.executeUpdate();

                        query = "DELETE FROM device_type WHERE device = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, strings[0]);
                        stmt.executeUpdate();

                        query = "DELETE FROM borrow_list WHERE device = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, strings[0]);
                        stmt.executeUpdate();
                }
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
