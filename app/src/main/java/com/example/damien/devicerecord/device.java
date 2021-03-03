package com.example.damien.devicerecord;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class device extends AppCompatActivity {
    RecyclerView deviceRecyclerView;
    DeviceListAdapter mDeviceListAdapter;

    ImageView backButton;
    ImageView btnAdd;

    public static final int REQUEST_CODE5 = 5;
    public static final int REQUEST_CODE10 = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        deviceRecyclerView = findViewById(R.id.deviceRecycler);
        backButton = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);

        Background bg = new Background(Background.FETCH_DEVICE_LIST);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        deviceRecyclerView.setLayoutManager(layoutManager);
        mDeviceListAdapter = new DeviceListAdapter(bg);
        deviceRecyclerView.setAdapter(mDeviceListAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(device.this, add_device.class);
                startActivityForResult(i, REQUEST_CODE10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE5){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Edit successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE10){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12387699";
        private static final String DB_NAME = "sql12387699";
        private static final String PASSWORD = "UMmjeekHxr";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;
        private int method;

        public static final int FETCH_DEVICE_LIST = 1;
        public static final int DELETE_DEVICE = 2;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {
                // nothing to do
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(device.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(device.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
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

                switch(this.method){
                    case FETCH_DEVICE_LIST:
                        query = "SELECT id, device, name, remark FROM device_type";
                        stmt = conn.prepareStatement(query);
                        result = stmt.executeQuery();
                        return result;

                    case DELETE_DEVICE:
                        query = "DELETE FROM device_type WHERE id = ?";
                        stmt = conn.prepareStatement(query);
                        stmt.setInt(1, Integer.parseInt(strings[0]));
                        stmt.executeUpdate();
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

    private class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;

        public DeviceListAdapter(Background bg) {
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(device.this);
        }

        class DeviceViewHolder extends RecyclerView.ViewHolder{
            TextView tvDeviceName;
            TextView tvRemark;
            ImageView imgDevice;
            ImageView imgViewDevice;
            ImageView imgDeleteDevice;

            final DeviceListAdapter mAdapter;

            public DeviceViewHolder(@NonNull View itemView, DeviceListAdapter adapter){
                super(itemView);
                tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
                tvRemark = itemView.findViewById(R.id.tvDeviceRemark);
                imgDevice = itemView.findViewById(R.id.imgDeviceItems);
                imgViewDevice = itemView.findViewById(R.id.imgViewDevice);
                imgDeleteDevice = itemView.findViewById(R.id.imgDeleteDevice);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = mInflater.inflate(R.layout.device_list, parent, false);
            return new DeviceViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder deviceViewHolder, final int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                final int deviceID = result.getInt(1);
                final String device = result.getString(2);
                final String deviceName = result.getString(3);
                final String deviceRemark = result.getString(4);

                deviceViewHolder.tvDeviceName.setText(deviceName);
                deviceViewHolder.tvRemark.setText(deviceRemark);
                deviceViewHolder.imgViewDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(device.this, device_details.class);
                        i.putExtra("deviceID", deviceID);
                        i.putExtra("device", device);
                        i.putExtra("deviceName", deviceName);
                        startActivityForResult(i, REQUEST_CODE5);
                    }
                });

                final DeviceViewHolder delete = deviceViewHolder;

                deviceViewHolder.imgDeleteDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(device.this);
                        builder.setTitle("Are you sure want to delete " + device + "(" + deviceName + ")" + " ?");
                        builder.setMessage("It cannot be undo once this device deleted!");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Background bg = new Background(Background.DELETE_DEVICE);
                                try {
                                    bg.execute(String.valueOf(deviceID)).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                updateResultSet();
                                notifyItemRemoved(delete.getAdapterPosition());
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
                });

                switch (deviceID){
                    case 1: deviceViewHolder.imgDevice.setImageResource(R.drawable.blackdrive);
                        break;
                    case 2: deviceViewHolder.imgDevice.setImageResource(R.drawable.reddrive);
                        break;
                    case 3: deviceViewHolder.imgDevice.setImageResource(R.drawable.whitedrive);
                        break;
                    case 4: deviceViewHolder.imgDevice.setImageResource(R.drawable.yellowdrive);
                        break;
                    case 5: deviceViewHolder.imgDevice.setImageResource(R.drawable.greendrive);
                        break;
                    case 6: deviceViewHolder.imgDevice.setImageResource(R.drawable.laptop1);
                        break;
                    case 7: deviceViewHolder.imgDevice.setImageResource(R.drawable.laptop2);
                        break;
                    case 8: deviceViewHolder.imgDevice.setImageResource(R.drawable.laptop3);
                        break;
                    case 9: deviceViewHolder.imgDevice.setImageResource(R.drawable.speaker1);
                        break;
                    case 10: deviceViewHolder.imgDevice.setImageResource(R.drawable.speaker2);
                        break;
                    case 11: deviceViewHolder.imgDevice.setImageResource(R.drawable.extension1);
                        break;
                    case 12: deviceViewHolder.imgDevice.setImageResource(R.drawable.extension2);
                        break;
                    case 13: deviceViewHolder.imgDevice.setImageResource(R.drawable.keyboard1);
                        break;
                    case 14: deviceViewHolder.imgDevice.setImageResource(R.drawable.keyboard2);
                        break;
                    case 15: deviceViewHolder.imgDevice.setImageResource(R.drawable.mouse1);
                        break;
                    case 16: deviceViewHolder.imgDevice.setImageResource(R.drawable.mouse2);
                        break;
                    case 17: deviceViewHolder.imgDevice.setImageResource(R.drawable.hdmi1);
                        break;
                    case 18: deviceViewHolder.imgDevice.setImageResource(R.drawable.hdmi2);
                        break;
                    case 19: deviceViewHolder.imgDevice.setImageResource(R.drawable.vga1);
                        break;
                    case 20: deviceViewHolder.imgDevice.setImageResource(R.drawable.vga2);
                        break;
                    case 21: deviceViewHolder.imgDevice.setImageResource(R.drawable.harddisk1);
                        break;
                    case 22: deviceViewHolder.imgDevice.setImageResource(R.drawable.harddisk2);
                        break;
                    case 23: deviceViewHolder.imgDevice.setImageResource(R.drawable.motherboard1);
                        break;
                }

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
                bg = new Background(Background.FETCH_DEVICE_LIST);
                this.result = this.bg.execute().get();
                itemCount = getResultCount();
            } catch (ExecutionException e) {
                Log.e("ERROR EXECUTION", e.getMessage());
            } catch (InterruptedException e) {
                Log.e("ERROR INTERRUPTED", e.getMessage());
            }
        }
    }
}
