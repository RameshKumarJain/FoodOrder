package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UsersDatabaseAdapter extends SQLiteOpenHelper {

    public static final String NAME_FIELD = "name";
    public static final String MOB_FIELD = "mob";
    public static final String MAIL_FIELD = "mail";
    public static final String PASSWORD_FIELD = "password";

    public static final String CREATE_USER_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.USERS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(NAME_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(MOB_FIELD)
            .append(" NUMBER NOT NULL, ")
            .append(MAIL_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(PASSWORD_FIELD)
            .append(" TEXT NOT NULL)").toString();

    public UsersDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }


    public long createUser(ContentValues contentValues) {
        return getWritableDatabase().insert(DatabaseConstant.USERS_TABLE, null, contentValues);
    }

    public void updateUser(ContentValues contentValues, String whereClause, String[] whereArgs) {
        getWritableDatabase().update(DatabaseConstant.USERS_TABLE, contentValues, whereClause, whereArgs);
    }

    public Cursor getUser(String mob, String password) {
        return getReadableDatabase().query(DatabaseConstant.USERS_TABLE, null, MOB_FIELD + "=? and " + PASSWORD_FIELD + "=?", new String[]{
                mob, password}, null, null, null);
    }

    public Cursor getUserByMobNo(String mob) {
        return getReadableDatabase().query(DatabaseConstant.USERS_TABLE, null, MOB_FIELD + "=?", new String[]{
                mob}, null, null, null);
    }

    public Cursor getUserByUserId(int id) {
        return getReadableDatabase().query(DatabaseConstant.USERS_TABLE, null, DatabaseConstant.ID + "=?", new String[]{
                String.valueOf(id)}, null, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
