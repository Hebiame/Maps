package karolinakaminska.github.com;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PathReaderDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PathReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PathReaderContract.PathEntry.TABLE_NAME + " (" +
                    PathReaderContract.PathEntry._ID + " INTEGER PRIMARY KEY," +
                    PathReaderContract.PathEntry.COLUMN_NAME_START_DATE + " TIMESTAMP," +
                    PathReaderContract.PathEntry.COLUMN_NAME_END_DATE + " TIMESTAMP," +
                    PathReaderContract.PathEntry.COLUMN_NAME_LOCATIONS + " LONGTEXT," +
                    PathReaderContract.PathEntry.COLUMN_NAME_STEPS + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PathReaderContract.PathEntry.TABLE_NAME;

    public PathReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //...
    }
}
