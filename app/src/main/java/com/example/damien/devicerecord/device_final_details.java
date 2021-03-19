package com.example.damien.devicerecord;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class device_final_details extends AppCompatActivity {
    TextView TVFinalID;
    TextView TVFinalEmployeeID;
    TextView TVFinalEmployeeName;
    TextView TVFinalBorrowDate;
    TextView TVFinalReturnDate;
    TextView TVFinalRemark;
    TextView TVDeviceName;
    TextView TVFinalIssues;
    ImageView backButton;

    private String finalID;
    private String finalEmployeeID;
    private String finalEmployeeName;
    private String finalBorrowDate;
    private String finalReturnDate;
    private String finalRemark;
    private String deviceName;
    private String finalIssues;

    public static final int REQUEST_CODE4 = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_final_details);

        TVFinalID = findViewById(R.id.tvFinalID);
        TVFinalEmployeeID = findViewById(R.id.tvFinalEmployeeID);
        TVFinalEmployeeName = findViewById(R.id.tvFinalEmployeeName);
        TVFinalBorrowDate = findViewById(R.id.tvFinalBorrowDate);
        TVFinalReturnDate = findViewById(R.id.tvFinalReturnDate);
        TVFinalRemark = findViewById(R.id.tvFinalRemark);
        TVDeviceName = findViewById(R.id.tvDeviceType);
        TVFinalIssues = findViewById(R.id.tvFinalIssues);
        backButton = findViewById(R.id.btnBack);

        Intent i = getIntent();
        finalID = i.getStringExtra("detailsID");
        finalEmployeeID = i.getStringExtra("detailsEmployeeID");
        finalEmployeeName = i.getStringExtra("detailsEmployeeName");
        finalBorrowDate = i.getStringExtra("detailsBorrowDate");
        finalReturnDate = i.getStringExtra("detailsReturnDate");
        finalRemark = i.getStringExtra("detailsRemark");
        finalIssues = i.getStringExtra("detailsIssues");
        deviceName = i.getStringExtra("deviceName");

        checkReturnDate(finalReturnDate);

        TVFinalID.setText(finalID);
        TVFinalEmployeeID.setText(finalEmployeeID);
        TVFinalEmployeeName.setText(finalEmployeeName);
        TVFinalBorrowDate.setText(finalBorrowDate);
        TVFinalReturnDate.setText(finalReturnDate);
        TVFinalRemark.setText(finalRemark);
        TVFinalIssues.setText(finalIssues);
        TVDeviceName.setText(deviceName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkReturnDate(String finalReturnDate) {
        ColorStateList oldColors =  TVFinalID.getTextColors(); //save original colors

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date today = calendar.getTime();

        if (finalReturnDate != null) {
            String[] splitDate = finalReturnDate.split("-");
            int year = Integer.parseInt(splitDate[0]);
            int month = Integer.parseInt(splitDate[1]);
            int day = Integer.parseInt(splitDate[2]);
            // very weird bug on month (maybe it is my own problem)
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            Date checkReturnDate = calendar.getTime();

            if (checkReturnDate.before(today)) {
                TVFinalReturnDate.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                TVFinalReturnDate.setTextColor(oldColors);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE4){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void btnEditDetails_onClicked(View view) {
        Intent i = new Intent(device_final_details.this, edit_details.class);
        i.putExtra("finalID", finalID);
        i.putExtra("finalEmployeeID", finalEmployeeID);
        i.putExtra("finalEmployeeName", finalEmployeeName);
        i.putExtra("finalBorrowDate", finalBorrowDate);
        i.putExtra("finalReturnDate", finalReturnDate);
        i.putExtra("finalRemark", finalRemark);
        i.putExtra("finalIssues", finalIssues);
        startActivityForResult(i, REQUEST_CODE4);
    }
}
