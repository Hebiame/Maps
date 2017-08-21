package karolinakaminska.github.com;

import android.provider.BaseColumns;

public final class PathReaderContract {
    private PathReaderContract() {}

    public static class PathEntry implements BaseColumns {
        public static final String TABLE_NAME = "paths";
        public static final String COLUMN_NAME_START_DATE = "start_date";
        public static final String COLUMN_NAME_END_DATE = "end_date";
        public static final String COLUMN_NAME_LOCATIONS = "locations";
        public static final String COLUMN_NAME_STEPS = "steps";
    }

}
