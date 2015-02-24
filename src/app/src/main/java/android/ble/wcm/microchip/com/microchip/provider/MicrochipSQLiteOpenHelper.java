package android.ble.wcm.microchip.com.microchip.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import android.ble.wcm.microchip.com.microchip.BuildConfig;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseColumns;

public class MicrochipSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = MicrochipSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "microchip.db";
    private static final int DATABASE_VERSION = 1;

    // @formatter:off
    private static final String SQL_CREATE_TABLE_RESPONSE = "CREATE TABLE IF NOT EXISTS "
            + ResponseColumns.TABLE_NAME + " ( "
            + ResponseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ResponseColumns.SHA1 + " INTEGER, "
            + ResponseColumns.STATUS_CODE + " INTEGER, "
            + ResponseColumns.URL + " TEXT, "
            + ResponseColumns.METHOD_TYPE + " TEXT, "
            + ResponseColumns.RESPONSE_STATUS + " TEXT, "
            + ResponseColumns.RESPONSE_ERROR + " TEXT "
            + " );";

    // @formatter:on

    public static MicrochipSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */

    private static MicrochipSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new MicrochipSQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private MicrochipSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    /*
     * Post Honeycomb.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static MicrochipSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new MicrochipSQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private MicrochipSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        db.execSQL(SQL_CREATE_TABLE_RESPONSE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
    }
}
