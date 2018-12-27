package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FrequentLocationDatabaseAdapter extends SQLiteOpenHelper {


    public static final String SEARCH_LOCATION = "searchLocation";
    public static final String USERID = "userId";
    public static final String SEARCH_COUNT = "searchCount";

    public static final String CREATE_FREQUENT_LOCATION_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.FREQUENT_LOCATION_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(USERID)
            .append(" INTEGER NOT NULL, ")
            .append(SEARCH_LOCATION)
            .append(" TEXT NOT NULL, ")
            .append(SEARCH_COUNT)
            .append(" INTEGER DEFAULT 1,")
            .append("FOREIGN KEY(" + USERID + ") REFERENCES users(_id))").toString();

    public FrequentLocationDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getFrequentSearchedLocation(int userId) {
        return getReadableDatabase().query(DatabaseConstant.FREQUENT_LOCATION_TABLE, null, USERID + "=?", new String[]{String.valueOf(userId)}, null, null, SEARCH_COUNT + " DESC");
    }


    public void update(ContentValues contentValues, int userID, String oldSearchLocation) {
        getWritableDatabase().update(DatabaseConstant.FREQUENT_LOCATION_TABLE, contentValues, USERID + "=? AND " + SEARCH_LOCATION + "=?", new String[]{String.valueOf(userID), oldSearchLocation});
    }

    public void update(String statement) {
        getWritableDatabase().execSQL(statement);
    }

    public void insert(ContentValues contentValues) {
        getWritableDatabase().insert(DatabaseConstant.FREQUENT_LOCATION_TABLE, null, contentValues);
    }
}
