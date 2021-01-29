package com.example.damien.thumbdriverecord;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class thumbdrive extends AppCompatActivity {
    RecyclerView thumbdriveRecyclerView;
    ThumbdriveListAdapter mThumbdriveListAdapter;

    ImageView backButton;

    public static final int REQUEST_CODE5 = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbdrive);

        thumbdriveRecyclerView = findViewById(R.id.thumbdriveRecycler);
        backButton = findViewById(R.id.btnBack);

        Background bg = new Background(Background.FETCH_THUMBDRIVE_LIST);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        thumbdriveRecyclerView.setLayoutManager(layoutManager);
        mThumbdriveListAdapter = new ThumbdriveListAdapter(bg);
        thumbdriveRecyclerView.setAdapter(mThumbdriveListAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        public static final int FETCH_THUMBDRIVE_LIST = 1;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);

            try {

            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(thumbdrive.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(thumbdrive.this);
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
                    case FETCH_THUMBDRIVE_LIST:
                        query = "SELECT id, name, remark FROM thumbdrive_type";
                        stmt = conn.prepareStatement(query);
                        result = stmt.executeQuery();
                        return result;
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return result;
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

    private class ThumbdriveListAdapter extends RecyclerView.Adapter<ThumbdriveListAdapter.ThumbdriveViewHolder> {
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;

        public ThumbdriveListAdapter(Background bg) {
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(thumbdrive.this);
        }

        class ThumbdriveViewHolder extends RecyclerView.ViewHolder{
            TextView tvThumbdriveName;
            TextView tvRemark;
            ImageView imgThumbdrive;
            ImageView imgViewThumbdrive;

            final ThumbdriveListAdapter mAdapter;

            public ThumbdriveViewHolder(@NonNull View itemView, ThumbdriveListAdapter adapter){
                super(itemView);
                tvThumbdriveName = itemView.findViewById(R.id.tvThumbdriveName);
                tvRemark = itemView.findViewById(R.id.tvThumbdriveRemark);
                imgThumbdrive = itemView.findViewById(R.id.imgThumbdriveItems);
                imgViewThumbdrive = itemView.findViewById(R.id.imgViewThumbdrive);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public ThumbdriveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = mInflater.inflate(R.layout.thumbdrive_list, parent, false);
            return new ThumbdriveViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull ThumbdriveViewHolder thumbdriveViewHolder, final int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                final int thumbdriveID = result.getInt(1);
                final String thumbdriveName = result.getString(2);
                final String thumbdriveRemark = result.getString(3);

                thumbdriveViewHolder.tvThumbdriveName.setText(thumbdriveName);
                thumbdriveViewHolder.tvRemark.setText(thumbdriveRemark);
                thumbdriveViewHolder.imgViewThumbdrive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(thumbdrive.this, thumbdrive_details.class);
                        i.putExtra("thumbdriveID", thumbdriveID);
                        i.putExtra("thumbdriveName", thumbdriveName);
                        startActivityForResult(i, REQUEST_CODE5);
                    }
                });

                switch (thumbdriveID){
                    case 1: thumbdriveViewHolder.imgThumbdrive.setImageResource(R.drawable.blackdrive);
                        break;
                    case 2: thumbdriveViewHolder.imgThumbdrive.setImageResource(R.drawable.reddrive);
                        break;
                    case 3: thumbdriveViewHolder.imgThumbdrive.setImageResource(R.drawable.whitedrive);
                        break;
                    case 4: thumbdriveViewHolder.imgThumbdrive.setImageResource(R.drawable.yellowdrive);
                        break;
                    case 5: thumbdriveViewHolder.imgThumbdrive.setImageResource(R.drawable.greendrive);
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
                bg = new Background(Background.FETCH_THUMBDRIVE_LIST);
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
