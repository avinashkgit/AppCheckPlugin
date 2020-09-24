package cordova.plugin.appcheck;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppsCodesDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private ContentValues contentValues = new ContentValues();

    //Student Database
    private static final String TABLE_NAME = "apps_codes_details";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "appCode";
    private static final String COL_3 = "appName";

    AppsCodesDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    //Creating the Database...
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT," + COL_3 + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE EXISTS" + TABLE_NAME);
        onCreate(db);

    }

    //Saving short name and Urls into the Database...
    boolean saveAppsCodes(String appCode, String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues.put(COL_2, appCode);
        contentValues.put(COL_3, appName);

        long resultSN = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //if data is inserted incorrectly  it will return -1
        return resultSN != -1;
    }


    //2- AppCode
    public String getAppCode(long id) {
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



    String getAppCodeByNAme(String app_name){
        String app_code="Not Found";
        String POSTS_SELECT_QUERY ="SELECT * FROM apps_codes_details";

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        try {

            if(cursor!=null) {
                cursor.moveToFirst();
                app_code = cursor.getString(cursor.getColumnIndex(COL_2));

            /*if (cursor.moveToFirst()) {
                do {
                    app_code = cursor.getString(cursor.getColumnIndex(COL_2));
                } while(cursor.moveToNext());
            }*/
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        }
        /*finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }*//*
        }*/
        return app_code;
    }

    //appExists
    String appExistsReturnCode(String appName) {
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String whereclause = COL_3+"=?";
        String[] whereargs = new String[]{appName};

        String[] columns = new String[]{ COL_2 };
        Cursor c = db.query(TABLE_NAME, columns, whereclause, whereargs, null, null, null);
        rv =  c.getString(c.getColumnIndex(COL_2));
        /*try {
            try (Cursor csr = db.query(TABLE_NAME, null, whereclause, whereargs, null, null, null)) {
                if (csr != null) {
                    if (csr.moveToFirst()) {
                        rv = csr.getString(csr.getColumnIndex(COL_2));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }*/
        return rv;//appcode
    }

    //3-AppName
    public String getAppName(long id) {
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String whereclause = "APPNAME=?";

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

    //3-AppName
    public String getAppExistCode(String appName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT" + COL_2 + "FROM" + TABLE_NAME + "WHERE" + COL_3 + "=" + "\"" +appName+ "\"";
        db.execSQL(Query);
        return Query;
    }


    public void clearData() {
        String clear = "DELETE FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(clear);
    }

    /*public boolean codeExists(String searchItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { COL_2 };
        String selection = COL_2 + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;

//        String query = "SELECT " + COL_2 + "FROM" + TABLE_NAME + "WHERE" + COL_3 + "=" +searchItem;
        String query = "SELECT  FROM" + TABLE_NAME + "WHERE" + COL_3 + "=" +searchItem;
        db.execSQL(query);
        return true;



        String Query = "Select * from " + TABLE_NAME + " where " + COL_3 + " = " + searchItem;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
        }

        cursor.close();

        return true;
    }*/

    /*public boolean checkIfMyCodeExists(String appName) {

        SQLiteDatabase db = this.getWritableDatabase();

//        String Query = "SELECT" +appcode+ "FROM" +apps_codes_details "WHERE" appname = "\"" +appName + "\"";
//        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }*/

    @SuppressLint("Recycle")
/*
    public boolean dbHasData(String searchKey) {
        String query = "Select * from " + TABLE_NAME + " where " + COL_2 + " = ?";
        return getReadableDatabase().rawQuery(query, new String[]{searchKey}).moveToFirst();
    }
*/


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + " TABLE_NAME";
        Cursor cursor = db.rawQuery(Query, null);
        cursor.close();
        return cursor;
    }

    public String getuserIDChart(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String appCOde = "";
        try {
            String countQuery = "select " + COL_2 + " FROM " + TABLE_NAME +
                    " where " + COL_3 + "='" + userID + "'";
            Cursor cursor = db.rawQuery(countQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                {
                    appCOde=cursor.getString(cursor.getColumnIndex(COL_2));
                }


            }


        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return appCOde;

    }


}


