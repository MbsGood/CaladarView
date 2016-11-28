package com.plug.caladarview.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import com.plug.caladarview.R;
import com.plug.caladarview.data.CalendarDay;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/*
* User: ChenCHaoXue
* Create date: 2016-10-25
* Time: 11:43
* From VCard
*
*/
public class CalendarMonthView extends View {
    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;//圆点
    protected static int DAY_SEPARATOR_WIDTH = 1;//天数分离宽度
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;//日期字体大小
    protected static int MIN_HEIGHT = 10;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;//日期大小
    protected static int MONTH_HEADER_SIZE;//月份高度
    protected static int MONTH_LABEL_TEXT_SIZE;//月份字体大小
    protected static int SELECTED_BEGIN_END_TEXT_SIZE;//初始与结束文本大小
    protected static int SELECTED_SMALL_CIRCLE_SIZE;
    protected static int SELECTED_DAY_HEIGHT;//初始  结束 今天 的高度
    protected static int SELECTED_DAY_LINE_TOP_HEIGHT;//选中时顶部到线的高度
    protected static int SELECTED_DAY_LINE_BOTTOM_HEIGHT;//底部到线的高度
    protected static int SELECTED_DAY_LINE_LEFT;//线的左边距离
    protected static int SELECTED_CIRCLE_TOP;//圆点移到线的距离
    protected static int SELECTED_BEGIN_LAST_SAME_OFFSET;//相同一天时设置圆点与线偏移量
    protected static int MONTH_TITLE_LEFT;//月份标题左边的距离
    protected int mPadding = 0;
    protected Paint mMonthNumPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mSelectedCirclePaint;
    protected Paint mSelectedDayTextPaint;
    protected Paint mSelectedShowLinePaint;

    protected int mCurrentDayTextColor;
    protected int mMonthTextColor;
    protected int mDayNumColor;
    protected int mMonthTitleBGColor;
    protected int mPreviousDayColor;//之前的日期
    protected int mSelectedDaysColor;
    protected int mSelectedDayTextColor;
    protected int mSelectedStartAndEndBGColor;
    protected int mSelectedLineColor;

    private StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;
    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;
    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight = DEFAULT_HEIGHT;//日历的高度
    protected int mWidth;
    protected int mYear;
    final Time today;
    private final Calendar mCalendar;
    private Boolean isPrevDayEnabled;
    private Boolean isLastDayEnabled;
    private int mNumRows = DEFAULT_NUM_ROWS;
    private OnDayClickListener mOnDayClickListener;

    /**
     * true 代表 可以  false 不可以  当天为间隔
     * isPrevEnabled  之前日期置灰且不可点击
     * isLastEnabled  之后日期置灰且不可点击
     * */
    public CalendarMonthView(Context context, TypedArray typedArray, boolean isPrevEnabled, boolean isLastEnabled) {
        super(context);
        Resources resources = context.getResources();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        initHeightAndColor(typedArray, isPrevEnabled, isLastEnabled, resources);
        initView();
    }

    private void initHeightAndColor(TypedArray typedArray, boolean isPrevEnabled, boolean isLastEnabled, Resources resources) {
        mCurrentDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDay, resources.getColor(R.color.select_black));
        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName, resources.getColor(R.color.select_black));
        mDayNumColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay, resources.getColor(R.color.select_black));
        mPreviousDayColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDay, resources.getColor(R.color.previous_gray));
        mSelectedDaysColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background));
        mMonthTitleBGColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayText, resources.getColor(R.color.selected_day_text));
        mSelectedStartAndEndBGColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedStartAndEndText, resources.getColor(R.color.selected_start_end_color));
        mSelectedLineColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedLine, resources.getColor(R.color.selected_line));
        mSelectedDayTextColor = typedArray.getColor(R.styleable.DayPickerView_selectedDayText, resources.getColor(android.R.color.white));
        mDrawRect = typedArray.getBoolean(R.styleable.DayPickerView_drawRoundRect, false);
        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, resources.getDimensionPixelSize(R.dimen.text_size_month));
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, resources.getDimensionPixelSize(R.dimen.text_size_day_name));
        SELECTED_BEGIN_END_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectBeginEndText, resources.getDimensionPixelSize(R.dimen.begin_end_text));
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, resources.getDimensionPixelOffset(R.dimen.selected_day_radius));
        SELECTED_SMALL_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedLineRadius, resources.getDimensionPixelOffset(R.dimen.line_circle));
        SELECTED_DAY_HEIGHT = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayHeight, resources.getDimensionPixelOffset(R.dimen.selected_day_text_height));
        SELECTED_DAY_LINE_TOP_HEIGHT = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayLineTopHeight, resources.getDimensionPixelOffset(R.dimen.selected_day_line_top_height));
        SELECTED_DAY_LINE_BOTTOM_HEIGHT = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayLineBottomHeight, resources.getDimensionPixelOffset(R.dimen.selected_day_line_bottom_height));
        SELECTED_DAY_LINE_LEFT = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayLineBottomHeight, resources.getDimensionPixelOffset(R.dimen.selected_day_line_left));
        SELECTED_CIRCLE_TOP = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayCircleTop, resources.getDimensionPixelOffset(R.dimen.selected_day_circle_top));
        SELECTED_BEGIN_LAST_SAME_OFFSET = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayCircleTop, resources.getDimensionPixelOffset(R.dimen.selected_begin_last_same_offset));
        MONTH_TITLE_LEFT = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_month_title_left, resources.getDimensionPixelOffset(R.dimen.month_title_left));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6);
        //之前日期可以设置为灰色，由于之前日期大多无用，只显示当前月份，暂不修改上个月的置灰
        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, isPrevEnabled);
        //之后日期设置为灰色，也显示当月  不处理下月或下一年的置灰
        isLastDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enableLastDay, isLastEnabled);
    }

    protected void initView() {
        //绘制当前月份的标题
        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);//设置文本仿粗体
        mMonthTitlePaint.setAntiAlias(true);//抗锯齿
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Align.LEFT);
        mMonthTitlePaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        mMonthTitlePaint.setStyle(Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);

        //add new 绘画当前文本-- 选中当天与结束当天
        mSelectedDayTextPaint = new Paint();
        mSelectedDayTextPaint.setAntiAlias(true);
        mSelectedDayTextPaint.setTextSize(SELECTED_BEGIN_END_TEXT_SIZE);
        mSelectedDayTextPaint.setColor(mSelectedDayTextColor);
        mSelectedDayTextPaint.setStyle(Style.FILL);
        mSelectedDayTextPaint.setTextAlign(Align.CENTER);

        //add  中间线 或 圆点
        mSelectedShowLinePaint = new Paint();
        mSelectedShowLinePaint.setAntiAlias(true);
        mSelectedShowLinePaint.setTextAlign(Align.CENTER);
        mSelectedShowLinePaint.setStyle(Style.FILL);
    }

    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthNums(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private void drawMonthTitle(Canvas canvas) {
        int x = MONTH_TITLE_LEFT;
//                (mWidth + 2 * mPadding) / 2;//取一半 居中显示
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));

        canvas.drawText(stringBuilder.toString(), x, (y + MONTH_LABEL_TEXT_SIZE / 2), mMonthTitlePaint);
    }

    protected void drawMonthNums(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);//值会丢失
        int dayOffset = findDayOffset();
        int day = 1;
        while (day <= mNumCells) {
            //值丢失有点严重
            int x = paddingDay * (1 + dayOffset * 2);
            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) ||
                    (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                setRectF(canvas, y, x, mSelectedCirclePaint);
            }
            //点击时更换字体颜色
            String startTag = showTimeText(day);
             //判断是否当前天数
            if (mHasToday && (mToday == day)) {
                mMonthNumPaint.setColor(mCurrentDayTextColor);
                //选中当天时处理
                if (((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear))
                        || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                    mMonthNumPaint.setColor(mMonthTitleBGColor);
                    canvas.drawText(startTag, x, y - SELECTED_DAY_HEIGHT, mSelectedDayTextPaint);
                } else {
                    mSelectedCirclePaint.setTextSize(SELECTED_BEGIN_END_TEXT_SIZE);
                    canvas.drawText("今天", x, y - SELECTED_DAY_HEIGHT, mSelectedCirclePaint);
                }
            } else {
                mMonthNumPaint.setColor(mDayNumColor);
            }
            //选中初始与结束时间
            if (((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear))) {
                //设置字体背景颜色
                setRectF(canvas, y, x, mSelectedCirclePaint);
                //判断是否同天 true 同天  flase 不同天
                if (!BeginDayEqualLastDay(day)) {
                    //设置一条线
                    paintLine(canvas, getLeft(paddingDay, x, startTag),
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                            getRight(paddingDay, x, startTag),
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT);
                    //设置圆点
                    mSelectedShowLinePaint.setColor(getResources().getColor(android.R.color.white));
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3 + SELECTED_CIRCLE_TOP, SELECTED_SMALL_CIRCLE_SIZE, mSelectedShowLinePaint);
                } else {
                    //设置一条线
                    paintLine(canvas, x - SELECTED_BEGIN_LAST_SAME_OFFSET,
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                            x + SELECTED_BEGIN_LAST_SAME_OFFSET,
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT);
                    //设置圆点
                    mSelectedShowLinePaint.setColor(getResources().getColor(android.R.color.white));
                    canvas.drawCircle(x - SELECTED_BEGIN_LAST_SAME_OFFSET, y - MINI_DAY_NUMBER_TEXT_SIZE / 3 + SELECTED_CIRCLE_TOP, SELECTED_SMALL_CIRCLE_SIZE, mSelectedShowLinePaint);
                    canvas.drawCircle(x + SELECTED_BEGIN_LAST_SAME_OFFSET, y - MINI_DAY_NUMBER_TEXT_SIZE / 3 + SELECTED_CIRCLE_TOP, SELECTED_SMALL_CIRCLE_SIZE, mSelectedShowLinePaint);
                }
                //设置字体颜色
                mMonthNumPaint.setColor(mMonthTitleBGColor);
                //文本显示
                canvas.drawText(startTag, x, y - SELECTED_DAY_HEIGHT, mSelectedDayTextPaint);
            }
            //开始日期与结束日期相同时
            if ((BeginDayEqualLastDay(day))) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
            }
            /**
             *结束日期大于开始日期
             * 1.同年
             * 2.同年同月 且开始<当前<结束  或 开始大于结束 结束<当前<开始
             *  结束月份大于开始月份 当前月份等于开始月份 当前日期大于开始
             *  开始月份小于结束月份  当前月份等于结束月份 当前日期小于结束
             * */
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear)
                    && (((mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth) && ((mSelectedBeginDay < mSelectedLastDay
                    && day > mSelectedBeginDay && day < mSelectedLastDay) || (mSelectedBeginDay > mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay)))
                    || ((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay))
                    || ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)))) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
                setRectF(canvas, y, x, mSelectedCirclePaint);

                mSelectedShowLinePaint.setColor(mSelectedLineColor);
                paintMiddleLine(canvas, x,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT,
                        mSelectedShowLinePaint);
            }
            //隔一个年份时点击时中间的颜色
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1
                    && mSelectedBeginYear != mSelectedLastYear
                    && ((mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth))
                    && (((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth
                    && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)) || ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay))
            ))) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
                setRectF(canvas, y, x, mSelectedCirclePaint);
                mSelectedShowLinePaint.setColor(mSelectedLineColor);
                paintMiddleLine(canvas, x, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT, mSelectedShowLinePaint);
            }
            /**
             * 同一年
             * 开始月份<当前月份<结束月份  且 开始月份>当前月份>结束月份  且开始月份>结束月份
             * */
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1
                    && mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear) &&
                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) ||
                            (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth))) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
                setRectF(canvas, y, x, mSelectedCirclePaint);
                mSelectedShowLinePaint.setColor(mSelectedLineColor);
                paintMiddleLine(canvas, x,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT,
                        mSelectedShowLinePaint);
            }
            /**
             * 不同年份
             * 相同月份
             * */
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear) &&
                    ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear))) ||
                            (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear))))) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
                setRectF(canvas, y, x, mSelectedCirclePaint);

                mSelectedShowLinePaint.setColor(mSelectedLineColor);
                paintMiddleLine(canvas, x, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE + SELECTED_DAY_LINE_TOP_HEIGHT,
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE - SELECTED_DAY_LINE_BOTTOM_HEIGHT, mSelectedShowLinePaint);
            }

            //小于当前时期时改颜色
            if (!isPrevDayEnabled && prevDay(day, today) && today.month == mMonth && today.year == mYear) {
                mMonthNumPaint.setColor(mPreviousDayColor);
//                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }
            //今天以后的日期置灰
            if (!isLastDayEnabled && lastDay(day, today) && today.month == mMonth && today.year == mYear) {
                mMonthNumPaint.setColor(mPreviousDayColor);
//                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }

            canvas.drawText(String.format(Locale.CHINA, "%d", day), x, y, mMonthNumPaint);
            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
    }

    private int getRight(int paddingDay, int x, String startTag) {
        return TextUtils.equals(startTag, getResources().getString(R.string.start_time_tag)) ? x + DAY_SELECTED_CIRCLE_SIZE+SELECTED_SMALL_CIRCLE_SIZE : x - paddingDay / 2 + SELECTED_DAY_LINE_LEFT;
    }

    /**
     * 待优化
     * */
    private int getLeft(int paddingDay, int x, String startTag) {
        return TextUtils.equals(startTag, getResources().getString(R.string.start_time_tag)) ? x - paddingDay / 2 + SELECTED_DAY_LINE_LEFT : x - paddingDay;
    }

    private boolean BeginDayEqualLastDay(int day) {
        return mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mSelectedBeginMonth == mSelectedLastMonth
                && mSelectedBeginDay == mSelectedLastDay && day == mSelectedBeginDay && mMonth == mSelectedBeginMonth && mYear == mSelectedBeginYear;
    }

    private void paintLine(Canvas canvas, int left, int top, int right, int bottom) {
        RectF rectF = new RectF(left, top, right, bottom);
        mSelectedShowLinePaint.setColor(mSelectedLineColor);
        canvas.drawRoundRect(rectF, 0.0f, 0.0f, mSelectedShowLinePaint);
    }

    private void paintMiddleLine(Canvas canvas, int x, int top, int bottom, Paint paint) {
        RectF rectF = new RectF(x - (mWidth - 2 * mPadding) / (2 * mNumDays), top, x + (mWidth - 2 * mPadding) / (2 * mNumDays), bottom);
        canvas.drawRoundRect(rectF, 0.0f, 0.0f, paint);
    }

    public CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }
        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;
        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;
        return new CalendarDay(mYear, mMonth, day);
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);
        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    /**
     * 返回星期几
     */
    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart) - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

    private void onDayClick(CalendarDay calendarDay) {
        if (mOnDayClickListener != null && isPreDayBoolean(calendarDay) && isLastBoolean(calendarDay)) {
            mOnDayClickListener.onDayClick(this, calendarDay);
        }
    }

    private boolean isPreDayBoolean(CalendarDay calendarDay) {
        return isPrevDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day < today.monthDay);
    }

    private boolean isLastBoolean(CalendarDay calendarDay) {
        return isLastDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day > today.monthDay);
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth == time.month && monthDay < time.monthDay);
    }

    private boolean lastDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth > time.month) || (mMonth == time.month && monthDay > time.monthDay);
    }

    @NonNull
    private String showTimeText(int day) {
        String startTag = "";
        if (mSelectedBeginYear != -1 && mSelectedLastYear == -1) {
            return startTag = getResources().getString(R.string.start_time_tag);
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, mSelectedBeginYear);
        startCalendar.set(Calendar.MONTH, mSelectedBeginMonth);
        startCalendar.set(Calendar.DAY_OF_MONTH, mSelectedBeginDay);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.YEAR, mSelectedLastYear);
        endCalendar.set(Calendar.MONTH, mSelectedLastMonth);
        endCalendar.set(Calendar.DAY_OF_MONTH, mSelectedLastDay);
        int value = startCalendar.compareTo(endCalendar);
        /**
         * start比end 早 返回 -1
         * start比end 相同 返回 0
         * start比end 晚 返回 1
         */
        switch (value) {
            case -1:
                if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear)) {
                    startTag = getResources().getString(R.string.start_time_tag);
                }
                if (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear) {
                    startTag = getResources().getString(R.string.end_time_tag);
                }
                break;
            case 0:
                startTag = getResources().getString(R.string.same_tag);
                break;
            case 1:
                if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear)) {
                    startTag = getResources().getString(R.string.end_time_tag);
                }
                if (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear) {
                    startTag = getResources().getString(R.string.start_time_tag);
                }
                break;
            default:
                break;
        }
        return startTag;
    }

    private void setRectF(Canvas canvas, int y, int x, Paint paint) {
        if (mDrawRect) {
            paintMiddleLine(canvas, x, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE, paint);
        } else {
            canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, paint);//圆形
        }
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public interface OnDayClickListener {
         void onDayClick(CalendarMonthView simpleMonthView, CalendarDay calendarDay);
    }
}