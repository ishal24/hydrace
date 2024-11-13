package com.example.hydracebeta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "Hydrace.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USER_PROFILE = "user_profile";
    private static final String TABLE_DAILY_ACTIVITY = "daily_activity";

    // User Profile Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";

    // Daily Activity Table Columns
    private static final String COLUMN_DATE = "date"; // For tracking activity by day
    private static final String COLUMN_DRINK_COUNT = "drink_count";
    private static final String COLUMN_STEPS = "steps";
    private static final String COLUMN_CALORIES_BURNED = "calories_burned";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Profile Table
        String createUserProfileTable = "CREATE TABLE " + TABLE_USER_PROFILE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_WEIGHT + " REAL);";
        db.execSQL(createUserProfileTable);

        // Create Daily Activity Table
        String createDailyActivityTable = "CREATE TABLE " + TABLE_DAILY_ACTIVITY + " (" +
                COLUMN_DATE + " TEXT PRIMARY KEY, " +
                COLUMN_DRINK_COUNT + " INTEGER, " +
                COLUMN_STEPS + " INTEGER, " +
                COLUMN_CALORIES_BURNED + " INTEGER);";
        db.execSQL(createDailyActivityTable);

        // Initialize default user profile
        initializeUserProfileIfEmpty(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_ACTIVITY);
        onCreate(db);
    }

    // Method to check if profile data exists, and if not, insert default profile data
    private void initializeUserProfileIfEmpty(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_PROFILE, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_GENDER, "Male");
            values.put(COLUMN_AGE, 0);
            values.put(COLUMN_HEIGHT, 0.0);
            values.put(COLUMN_WEIGHT, 0.0);
            db.insert(TABLE_USER_PROFILE, null, values);
        }
        cursor.close();
    }

    // Methods for User Profile Table
    public void insertUserProfile(String gender, int age, double height, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        db.insert(TABLE_USER_PROFILE, null, values);
        db.close();
    }

    public Cursor getUserProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER_PROFILE, null, null, null, null, null, null);
    }

    // Methods for Daily Activity Table
    public void insertDailyActivity(String date, int drinkCount, int steps, int caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DRINK_COUNT, drinkCount);
        values.put(COLUMN_STEPS, steps);
        values.put(COLUMN_CALORIES_BURNED, caloriesBurned);
        db.insert(TABLE_DAILY_ACTIVITY, null, values);
        db.close();
    }

    public Cursor getDailyActivity(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DATE + " = ?";
        String[] selectionArgs = { date };
        return db.query(TABLE_DAILY_ACTIVITY, null, selection, selectionArgs, null, null, null);
    }

    public void updateUserProfile(String gender, int age, double height, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);

        // Assuming a single row for user profile. Replace with your primary key if needed.
        db.update(TABLE_USER_PROFILE, values, COLUMN_ID + " = ?", new String[]{"1"});
        db.close();
    }
}
