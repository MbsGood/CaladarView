package com.plug.caladarview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.plug.caladarview.CalendarManageActivity.EXTRA_BEGIN_DAY;
import static com.plug.caladarview.CalendarManageActivity.EXTRA_END_DAY;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE=100;
    Date dayStart;
    Date dayEnd;
    TextView tvDate;
    SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDate=(TextView)findViewById(R.id.tv_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarManageActivity.startForResult(MainActivity.this, REQUEST_CODE, new Date(), new Date(), false, true, 2,false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    dayStart = (Date) data.getSerializableExtra(EXTRA_BEGIN_DAY);
                    dayEnd = (Date) data.getSerializableExtra(EXTRA_END_DAY);
                    tvDate.setText(sdfShort.format(dayStart) + "至" + sdfShort.format(dayEnd));
                    break;
                default:
                    break;
            }
        }

    }
}
