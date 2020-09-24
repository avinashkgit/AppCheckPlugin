package cordova.plugin.appcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppsDetailsDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private ContentValues contentValues = new ContentValues();

    //Student Database
    private static final String TABLE_NAME = "apps_usages";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "appName";
    private static final String COL_3 = "lasttimeused";
    private static final String COL_4 = "usagetime";

    AppsDetailsDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    //Creating the Database...
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT," + COL_3 + " TEXT," + COL_4 + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE EXISTS" + TABLE_NAME);
        onCreate(db);

    }

    //Saving short name and Urls into the Database...
    public boolean saveAppsDetails(String appName, String last_time_used, String usage_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues.put(COL_2, appName);
        contentValues.put(COL_3, last_time_used);
        contentValues.put(COL_4, usage_time);

        long resultSN = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //if data is inserted incorrectly  it will return -1
        return resultSN != -1;
    }


    //2-Retrieving appName from the Database...
    public String getAppName(long id) {
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String whereclause = "ID=?";
        String[] whereargs = new String[]{String.valueOf(id)};
        try {
            try (Cursor csr = db.query(TABLE_NAME, null, whereclause, whereargs, null, null, null)) {
                if (csr != null) {
                    if (csr.moveToFirst()) {
                        rv = csr.getString(csr.getColumnIndex(COL_2));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return rv;
    }


    //3-Retrieving last time used from the Database...
    public String getLastTimeUsed(long id) {
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String whereclause = "ID=?";
        String[] whereargs = new String[]{String.valueOf(id)};
        try {
            try (Cursor csr = db.query(TABLE_NAME, null, whereclause, whereargs, null, null, null)) {
                if (csr != null) {
                    if (csr.moveToFirst()) {
                        rv = csr.getString(csr.getColumnIndex(COL_3));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return rv;
    }


    //4-Retrieving UsageTime from the Database...
    public String getUsageTime(long id) {
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String whereclause = "ID=?";
        String[] whereargs = new String[]{String.valueOf(id)};
        Cursor csr = db.query(TABLE_NAME, null, whereclause, whereargs, null, null, null);
        try {
            if (csr != null) {
                if (csr.moveToFirst()) {
                    rv = csr.getString(csr.getColumnIndex(COL_4));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csr != null) {
                csr.close();
            }
        }
        return rv;
    }


    void clearData() {
        String clear = "DELETE FROM " + TABLE_NAME;
//        String clear = "DROP TABLE " + TABLE_NAME;
//        String clear = "DROP TABLE IF EXISTS " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(clear);

    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
