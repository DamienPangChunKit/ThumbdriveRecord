package com.example.damien.devicerecord;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class device_details extends AppCompatActivity {
    RecyclerView deviceDetailsRV;
    deviceDetailsAdapter mAdapter;

    TextView tvDeviceType;
    ImageView backButton;
    TableLayout mTableLayout;

    private int deviceID;
    private String device;
    private String deviceName;

    public static final int REQUEST_CODE3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        backButton = findViewById(R.id.btnBack);
        tvDeviceType = findViewById(R.id.tvDeviceType);
        mTableLayout = findViewById(R.id.layout_table);

        Intent i = getIntent();
        deviceID = i.getIntExtra("deviceID", -1);
        device = i.getStringExtra("device");
        deviceName = i.getStringExtra("deviceName");

        tvDeviceType.setText(deviceName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Background bg = new Background(Background.FETCH_DETAILS);
        deviceDetailsRV = (RecyclerView) findViewById(R.id.deviceDetailsRecycler);
        mAdapter = new deviceDetailsAdapter(bg);
        deviceDetailsRV.setAdapter(mAdapter);
        deviceDetailsRV.setLayoutManager(new LinearLayoutManager(device_details.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE3){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private final String LIBRARY = getString(R.string.db_library);
        private final String USERNAME = getString(R.string.db_username);
        private final String DB_NAME = getString(R.string.db_name);
        private final String PASSWORD = getString(R.string.db_password);
        private final String SERVER = getString(R.string.db_server);

        private Connection conn;
        private PreparedStatement stmt;
        private int method;

        public static final int FETCH_DETAILS = 1;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;

            if (conn == null) {
                return null;
            }
            try {
                String query;
                switch(method){
                    case FETCH_DETAILS:
                        query = "SELECT id, employee_id, employee_name, borrow_date, return_date, remark, issues FROM borrow_list WHERE (device_type = ? AND device = ?)";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, deviceName);
                        stmt.setString(2, device);
                        result = stmt.executeQuery();
                        return result;
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return null;
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

    private class deviceDetailsAdapter extends RecyclerView.Adapter<deviceDetailsAdapter.deviceDetailsHolder>{
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;


        public deviceDetailsAdapter(Background bg){
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(device_details.this);
        }

        class deviceDetailsHolder extends RecyclerView.ViewHolder{
            TextView tvDeviceDetailsID;
            TextView tvDeviceDetailsName;
            TextView tvDeviceDetailsReturnDate;
            TableLayout mTableLayout;

            final deviceDetailsAdapter mAdapter;

            public deviceDetailsHolder(@NonNull View itemView, deviceDetailsAdapter adapter){
                super(itemView);
                tvDeviceDetailsID = (TextView) itemView.findViewById(R.id.tvDeviceDetailsID);
                tvDeviceDetailsName = (TextView) itemView.findViewById(R.id.tvDeviceDetailsEmployeeName);
                tvDeviceDetailsReturnDate = (TextView) itemView.findViewById(R.id.tvDeviceDetailsReturnDate);
                mTableLayout = (TableLayout) itemView.findViewById(R.id.device_details_layout_table);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public deviceDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(R.layout.device_details_row, viewGroup, false);
            return new deviceDetailsHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull deviceDetailsHolder deviceDetailsHolder, int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                final String detailsID = result.getString(1);
                final String detailsEmployeeID = result.getString(2);
                final String detailsEmployeeName = result.getString(3);
                final String detailsBorrowDate = result.getString(4);
                final String detailsReturnDate = result.getString(5);
                final String detailsRemark = result.getString(6);
                final String detailsIssues = result.getString(7);

                deviceDetailsHolder.tvDeviceDetailsID.setText(detailsID);
                deviceDetailsHolder.tvDeviceDetailsName.setText(detailsEmployeeName);
                deviceDetailsHolder.tvDeviceDetailsReturnDate.setText(detailsReturnDate);
                checkReturnDate(deviceDetailsHolder, detailsReturnDate);

                deviceDetailsHolder.mTableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(device_details.this, device_final_details.class);
                        i.putExtra("detailsID", detailsID);
                        i.putExtra("detailsEmployeeID", detailsEmployeeID);
                        i.putExtra("detailsEmployeeName", detailsEmployeeName);
                        i.putExtra("detailsBorrowDate", detailsBorrowDate);
                        i.putExtra("detailsReturnDate", detailsReturnDate);
                        i.putExtra("detailsRemark", detailsRemark);
                        i.putExtra("deviceName", deviceName);
                        i.putExtra("detailsIssues", detailsIssues);
                        startActivityForResult(i, REQUEST_CODE3);
                    }
                });
            }
            catch (SQLException e) {
                Log.d("ERROR BIND VIEW", e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        private int getResultCount() {
            try {
                result.next();
                result.last();
                int count = result.getRow();
                result.first();
                return count;
            } catch (SQLException e) {

            }
            return 0;
        }

        public void updateResultSet() {
            try {
                bg.closeConn();
                bg = new Background(Background.FETCH_DETAILS);
                this.result = this.bg.execute().get();
                itemCount = getResultCount();
            } catch (ExecutionException e) {
                Log.e("ERROR EXECUTION", e.getMessage());
            } catch (InterruptedException e) {
                Log.e("ERROR INTERRUPTED", e.getMessage());
            }
        }
    }

    private void checkReturnDate(@NonNull deviceDetailsAdapter.deviceDetailsHolder deviceDetailsHolder, String detailsReturnDate) {
        ColorStateList oldColors =  deviceDetailsHolder.tvDeviceDetailsName.getTextColors(); //save original colors

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date today = calendar.getTime();

        if (detailsReturnDate != null){
            String[] splitDate = detailsReturnDate.split("-");
            int year = Integer.parseInt(splitDate[0]);
            int month = Integer.parseInt(splitDate[1]);
            int day = Integer.parseInt(splitDate[2]);
            // very weird bug on month (maybe it is my own problem)
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MILLISECOND, 0);

            Date checkReturnDate = calendar.getTime();

            if (checkReturnDate.before(today) ){
                deviceDetailsHolder.tvDeviceDetailsReturnDate.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                deviceDetailsHolder.tvDeviceDetailsReturnDate.setTextColor(oldColors);
            }
        }
    }
}
