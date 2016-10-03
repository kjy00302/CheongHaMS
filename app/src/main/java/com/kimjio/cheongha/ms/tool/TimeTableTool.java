package com.kimjio.cheongha.ms.tool;

import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import com.kimjio.cheongha.ms.R;


/**
 * Created by whdghks913 on 2015-02-17.
 */
public class TimeTableTool {
    public static final String TimeTableDBName = "CheongHaMiddleSchoolTimeTable.db";
    public static final String tableName = "CheongHaTimeTable";

    public final static String mFilePath = "/data/data/com.kimjio.cheongha.ms/databases/";
    public final static String mGoogleSpreadSheetUrl = "https://docs.google.com/spreadsheets/d/1BZR8g4ZFPwCTi4P7DwIP0SSXVHG_2LqjyNuKmdzdXIw/pubhtml?gid=0&single=true";

    public final static String[] mDisplayName = {"월요일 😪", "화요일", "수요일", "목요일", "금요일 😆"};

    public static boolean fileExists() {
        return new File(TimeTableTool.mFilePath + TimeTableTool.TimeTableDBName).exists();
    }

    public static timeTableData getTimeTableData(int mGrade, int mClass, int DayOfWeek ) {
        if (mGrade == -1 || mClass == -1) {
            return null;
        }

        timeTableData mData = new timeTableData();
        String[] subject = new String[9];
        String[] room = new String[9];

        Database mDatabase = new Database();
        mDatabase.openDatabase(TimeTableTool.mFilePath, TimeTableTool.TimeTableDBName);

        Cursor mCursor;


        mCursor = mDatabase.getData(TimeTableTool.tableName, "G" + mGrade + mClass +", R" + mGrade + mClass);

        /**
         * Move to Row
         * ---- moveToFirst
         * ---- moveToNext
         * ---- moveToPosition
         * ---- moveToLast
         *
         * Mon : DayOfWeek : 0
         * Tus : DayOfWeek : 1
         * ...
         * Fri : DayOfWeek : 4
         */

        // TODO 시간표 1일당 교시 설정 & 처음 시작 위치
        mCursor.moveToPosition((DayOfWeek * 9) /* + 1 */);

        for (int period = 0; period < 9; period++) {
            String mSubject, mRoom;

            /**
             * | | | |
             * 0 1 2 3
             */

            mSubject = mCursor.getString(0);

            mRoom = mCursor.getString(1);

            if (mSubject != null && !mSubject.isEmpty()
                    && mSubject.contains("\n"))
                mSubject = mSubject.replace("\n ", " (") + ")";

            subject[period] = mSubject;
            room[period] = mRoom;

            mCursor.moveToNext();
        }

        mData.subject = subject;
        mData.room = room;

        return mData;
    }

    public static class timeTableData {
        public String[] subject, room;
    }

    public static todayTimeTableData getTodayTimeTable(Context mContext) {
        Preference mPref = new Preference(mContext);
        todayTimeTableData mData = new todayTimeTableData();

        Calendar mCalendar = Calendar.getInstance();

        int DayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

        int mGrade = mPref.getInt("myGrade", -1);
        int mClass = mPref.getInt("myClass", -1);

        if (DayOfWeek > 1 && DayOfWeek < 7) {
            DayOfWeek -= 2;
        } else {
            mData.title = mContext.getString(R.string.not_go_to_school_title);
            mData.info = mContext.getString(R.string.not_go_to_school_message);
            return mData;
        }

        mData.title = String.format(mContext.getString(R.string.today_timetable), mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN));

        if (mGrade == -1 || mClass == -1) {
            mData.info = mContext.getString(R.string.no_setting_my_grade);
            return mData;
        }

        boolean mFileExists = new File(TimeTableTool.mFilePath + TimeTableTool.TimeTableDBName).exists();
        if (!mFileExists) {
            mData.info = mContext.getString(R.string.not_exists_data);
            return mData;
        }

        String mTimeTable = "";

        Database mDatabase = new Database();
        mDatabase.openDatabase(TimeTableTool.mFilePath, TimeTableTool.TimeTableDBName);


        Cursor mCursor;

        mCursor = mDatabase.getData(TimeTableTool.tableName, "G" + mGrade + mClass + ", R" + mGrade + mClass);


        /**
         * Move to Row
         * ---- moveToFirst
         * ---- moveToNext
         * ---- moveToPosition
         * ---- moveToLast
         *
         * Mon : DayOfWeek : 0
         * Tus : DayOfWeek : 1
         * ...
         * Fri : DayOfWeek : 4
         */

        // TODO 시간표 1일당 교시 설정 & 처음 시작 위치
        mCursor.moveToPosition((DayOfWeek * 9)  /*+ 1*/ );

        for (int period = 0; period < 9; period++) {
            String mSubject;

            /**
             * | | | |
             * 0 1 2 3
             */

            mSubject = mCursor.getString(0);

            if (mSubject != null && !mSubject.isEmpty()
                    && mSubject.contains("\n"))
                mSubject = mSubject.replace("\n", " (") + ")";

            mTimeTable += Integer.toString(period + 1) + ". " + mSubject +"\n";

            if (mCursor.moveToNext()) {
                mTimeTable += " ";
            }
        }

        mData.info = mTimeTable;

        return mData;
    }

    public static class todayTimeTableData {
        public String title;
        public String info;
    }
}

