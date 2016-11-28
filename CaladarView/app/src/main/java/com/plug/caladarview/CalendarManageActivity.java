package com.plug.caladarview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.plug.caladarview.data.CalendarDay;
import com.plug.caladarview.data.SelectedDays;
import com.plug.caladarview.view.DatePickerController;
import com.plug.caladarview.view.DayPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
* User: ChenCHaoXue
* Create date: 2016-10-25 
* Time: 17:10 
* From VCard
* 日历时间选择
*/
public class CalendarManageActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerController {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    public static final String EXTRA_BEGIN_DAY = "extraBeginDay";
    public static final String EXTRA_END_DAY = "extraEndDay";

    public static final String EXTRA_PRE_ENABLED = "isPrevEnabled";//之前日期开关
    public static final String EXTRA_LAST_ENABLED = "isLastEnabled";//之后日期开关
    public static final String FROM_SCENE = "from_scene";//场景,专用来控制开始月份数据  1、统计   2.商品劵
    public static final String DOUBLE_EXTRA = "isDouble";//其它界面场景需要一点变双选中

    private DayPickerView dayPickerView;

    private Date dayStart;
    private Date dayEnd;
    private boolean selectFinish = true;

    private boolean isPrevEnabled;
    private boolean isLastEnabled;
    private int scene;
    private boolean isDouble;
    TextView tvBack;
    TextView tvSave;

    /**
     * mController 监听点击事件
     * isHasValue 判断是否有值 false 不显示选中日期  true 要传开始与结束日期
     * beginCalendarDay  开始日期
     * endCalendarDay   结束日期
     * yearMonthDay  设置日历开始时间
     * isPrevEnabled  当天之前日期置灰开关
     * isLastEnabled 当天之后日期置灰开关
     * isDouble true 双击  false 点击两次
     * 项目中使用场景过多，有需要可以继续更改
     */
    public static void startForResult(Activity context, int requestCode, Date dayStart, Date dayEnd, boolean isPreEnable,
                                      boolean isLastEnable, int scene, boolean isDouble) {
        Intent intent = new Intent(context, CalendarManageActivity.class);
        intent.putExtra(EXTRA_BEGIN_DAY, dayStart);
        intent.putExtra(EXTRA_END_DAY, dayEnd);
        intent.putExtra(EXTRA_PRE_ENABLED, isPreEnable);
        intent.putExtra(EXTRA_LAST_ENABLED, isLastEnable);
        intent.putExtra(FROM_SCENE, scene);
        intent.putExtra(DOUBLE_EXTRA, isDouble);
        context.startActivityForResult(intent, requestCode);
    }

    //true 大于100天  false 小于
    public boolean isMaxDay(Date dayEnd, Date dayStart) {
        if (((dayEnd.getTime() - dayStart.getTime()) / 86400000 + 1) > 100) return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initView();
        initDayPicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.tv_save:
                if (selectFinish && !isMaxDay(dayEnd, dayStart)) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_BEGIN_DAY, dayStart);
                    data.putExtra(EXTRA_END_DAY, dayEnd);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        dayPickerView = (DayPickerView) findViewById(R.id.pickerView);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvBack.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    private void initDayPicker() {
        dayStart = (Date) getIntent().getSerializableExtra(EXTRA_BEGIN_DAY);
        dayEnd = (Date) getIntent().getSerializableExtra(EXTRA_END_DAY);
        isPrevEnabled = getIntent().getBooleanExtra(EXTRA_PRE_ENABLED, false);
        isLastEnabled = getIntent().getBooleanExtra(EXTRA_LAST_ENABLED, false);
        isDouble = getIntent().getBooleanExtra(DOUBLE_EXTRA, false);
        scene = getIntent().getIntExtra(FROM_SCENE, 0);

        Calendar calendar = Calendar.getInstance();
        if (scene == 1) {
            calendar.set(2015, 5, 1);//开始时间
        } else if (scene == 2) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));//开始时间
        }
        dayPickerView.setController(this, true, new CalendarDay(dayStart), new CalendarDay(dayEnd), calendar, isPrevEnabled, isLastEnabled, scene == 1, isDouble);
        dayPickerView.scrollToPosition(scrollToPosition(dayStart));
    }

    public int scrollToPosition(Date startTime) {
        Calendar localCalender = Calendar.getInstance();
        CalendarDay startCalender = new CalendarDay(startTime);
        int intervalsYear;
        if (scene == 1) {
            intervalsYear = localCalender.get(Calendar.YEAR) - startCalender.getYear();
            return dayPickerView.getAdapter().getItemCount() - (intervalsYear * 12 + localCalender.get(Calendar.MONTH) - startCalender.getMonth()) - 1;
        } else {
            intervalsYear = startCalender.getYear() - localCalender.get(Calendar.YEAR);
            return intervalsYear * 12 + startCalender.getMonth() - localCalender.get(Calendar.MONTH);
        }
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        selectFinish = false;
    }

    @Override
    public void onDateRangeSelected(SelectedDays selectedDays) {
        selectFinish = true;
        dayStart = selectedDays.getFirst().getDate();
        dayEnd = selectedDays.getLast().getDate();
    }
}
