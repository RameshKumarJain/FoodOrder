package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CouponHistoryDatabaseAdapter extends SQLiteOpenHelper {

    public static final String USERID = "userId";
    public static final String OFFERID = "offerId";
    public static final String COUNT = "count";

    public static final String CREATE_COUPON_HISTORY_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.COUPON_HISTORY_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(USERID)
            .append(" INTEGER NOT NULL, ")
            .append(OFFERID)
            .append(" INTEGER NOT NULL, ")
            .append(COUNT)
            .append(" INTEGER DEFAULT 1, ")
            .append("FOREIGN KEY(" + OFFERID + ") REFERENCES offers(_id), ")
            .append("FOREIGN KEY(" + USERID + ") REFERENCES users(_id))").toString();

    public CouponHistoryDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getCouponHistoryByUserId(int userId) {
        return getReadableDatabase().query(DatabaseConstant.COUPON_HISTORY_TABLE, null, USERID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Cursor getCouponHistoryByUserIdAndOfferId(int userId, int offerId) {
        return getReadableDatabase().query(DatabaseConstant.COUPON_HISTORY_TABLE, null, USERID + "=? AND " + OFFERID + "=?", new String[]{String.valueOf(userId), String.valueOf(offerId)}, null, null, null);
    }

    public void insert(ContentValues contentValues) {
        getWritableDatabase().insert(DatabaseConstant.COUPON_HISTORY_TABLE, null, contentValues);
    }

    public void update(ContentValues contentValues, int userId, int offerId) {
        getWritableDatabase().update(DatabaseConstant.COUPON_HISTORY_TABLE, contentValues, USERID + "=? AND " + OFFERID + "=?", new String[]{String.valueOf(userId), String.valueOf(offerId)});
    }
}
