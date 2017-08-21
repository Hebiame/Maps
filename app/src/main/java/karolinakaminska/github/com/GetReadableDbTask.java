package karolinakaminska.github.com;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class GetReadableDbTask extends AsyncTask<SQLiteOpenHelper, Void, Void>{

    @Override
    protected Void doInBackground(SQLiteOpenHelper... sqLiteOpenHelpers) {
        for(SQLiteOpenHelper i : sqLiteOpenHelpers) {
            i.getReadableDatabase();
        }
        return null;
    }
}
