package karolinakaminska.github.com;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;


public class GetReadableDbTask extends AsyncTask<SQLiteOpenHelper, Void, SQLiteDatabase>{

    @Override
    protected SQLiteDatabase doInBackground(SQLiteOpenHelper... sqLiteOpenHelpers) { //i tak będzie tylko 1 XD
        for(SQLiteOpenHelper i : sqLiteOpenHelpers) {
            return i.getReadableDatabase();
        }
        return null;
    }
}
