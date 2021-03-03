package com.example.damien.devicerecord;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

public class add_list_next extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    TextInputLayout layout_remark;
    TextInputLayout layout_type;
    ImageView backButton;
    Spinner mSpinner;

    private String employeeID;
    private String employeeName;
    private String borrowDate;
    private String returnDate;
    private String device;
    private int[] deviceID;
    private String[] deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_next);

        layout_remark = findViewById(R.id.textInputRemark);
        layout_type = findViewById(R.id.textInputDeviceType);
        backButton = findViewById(R.id.btnBack);
        mSpinner = findViewById(R.id.spinnerDeviceType);

        Intent i = getIntent();
        employeeID = i.getStringExtra("empIDInput");
        employeeName = i.getStringExtra("empNameInput");
        borrowDate = i.getStringExtra("borrowDateInput");
        returnDate = i.getStringExtra("returnDateInput");
        device = i.getStringExtra("deviceInput");

        mSpinner.setOnItemSelectedListener(this);
        new Background(Background.FETCH_DEVICE).execute();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClick(View v){
        if(v.getId()==R.id.btnScan){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, deviceName[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
    }

    public void btnAddList_onClicked(View view) {
        String remarkInput = layout_remark.getEditText().getText().toString().trim();
        String deviceTypeInput = mSpinner.getSelectedItem().toString().trim();

        if (remarkInput.isEmpty()){
            remarkInput = null;
        }

        Background bg = new Background(Background.INSERT_LIST);
        bg.execute(employeeID, employeeName, borrowDate, returnDate, remarkInput, deviceTypeInput, device);
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        public static final int FETCH_DEVICE = 30;
        public static final int INSERT_LIST = 31;

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

                    case INSERT_LIST:
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(add_list_next.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(add_list_next.this);
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
                        query = "SELECT id, name FROM device_type WHERE device = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, device);
                        return stmt.executeQuery();

                    case INSERT_LIST:
                        query = "insert into borrow_list (employee_id, employee_name, borrow_date, return_date, remark, device_type, device) values (?, ?, ?, ?, ?, ?, ?)";
                        stmt = conn.prepareStatement(query);
                        stmt.setInt(1, Integer.parseInt(strings[0]));
                        stmt.setString(2, strings[1]);
                        stmt.setString(3, strings[2]);
                        stmt.setString(4, strings[3]);
                        stmt.setString(5, strings[4]);
                        stmt.setString(6, strings[5]);
                        stmt.setString(7, strings[6]);
                        stmt.executeUpdate();

//                        String[] splitDate = strings[2].split("-");
//                        Calendar c = Calendar.getInstance();
//                        c.set(Calendar.YEAR, Integer.parseInt(splitDate[0]));
//                        c.set(Calendar.MONTH, Integer.parseInt(splitDate[1]));
//                        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitDate[2]));
//
//                        startAlarm(c);
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

    // no need care because of failed
    private void startAlarm(Calendar c) {
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlertReceiver.class);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 1, i, 0);

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), mPendingIntent);
    }
}
