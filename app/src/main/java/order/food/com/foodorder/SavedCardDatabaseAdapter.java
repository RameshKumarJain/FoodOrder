package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedCardDatabaseAdapter extends SQLiteOpenHelper {

    public static final String USERID = "userId";
    public static final String CARDNUMBER = "cardNumber";
    public static final String CARDHOLDERNAME = "cardHolderName";
    public static final String EXPIRYMONTH = "expiryMonth";
    public static final String EXPIRYYEAR = "expiryYear";

    public static final String CREATE_SAVED_CARDS_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.CARDS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(USERID)
            .append(" INTEGER NOT NULL, ")
            .append(CARDNUMBER)
            .append(" TEXT NOT NULL, ")
            .append(CARDHOLDERNAME)
            .append(" TEXT NOT NULL, ")
            .append(EXPIRYMONTH)
            .append(" INTEGER NOT NULL, ")
            .append(EXPIRYYEAR)
            .append(" INTEGER NOT NULL, ")
            .append("FOREIGN KEY(" + USERID + ") REFERENCES users(_id))").toString();

    public SavedCardDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getSavedCards(int userId) {
        return getReadableDatabase().query(DatabaseConstant.CARDS_TABLE, null, USERID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }


    public void insert(ContentValues contentValues) {
        if (getReadableDatabase().query(DatabaseConstant.CARDS_TABLE, null, USERID + "=? AND " + CARDNUMBER + " =? ",
                new String[]{String.valueOf(HomeActivity.userId), contentValues.getAsString(CARDNUMBER)}, null, null, null)
                .getCount() == 0) {
            getWritableDatabase().insert(DatabaseConstant.CARDS_TABLE, null, contentValues);
        }
    }

    public void delete(int id) {
        getWritableDatabase().delete(DatabaseConstant.CARDS_TABLE, DatabaseConstant.ID + "=?", new String[]{String.valueOf(id)});
    }
}
