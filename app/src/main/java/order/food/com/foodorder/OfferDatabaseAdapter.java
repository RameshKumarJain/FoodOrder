package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfferDatabaseAdapter extends SQLiteOpenHelper {

    public static final String OFFER_NAME = "offerName";
    public static final String OFFER_CODE = "offerCode";
    public static final String DESCRIPTION = "description";
    public static final String DISCOUNT_PERCENTAGE = "discountPercentage";
    public static final String MAX_DISCOUNT_PRICE = "maxDiscountPrice";
    public static final String VALID_TILL = "validTill";
    public static final String COUNT_ALLOWED_PER_USER = "countAllowedPerUser";
    public static final String TERMS_AND_CONDITIONS = "termsAndConditions";
    public static final String PAID_VIA = "paidVia";
    public static final String IMAGE_NAME = "imageName";


    public static final String CREATE_OFFER_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.OFFERS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(OFFER_NAME)
            .append(" TEXT NOT NULL, ")
            .append(OFFER_CODE)
            .append(" TEXT NOT NULL, ")
            .append(DESCRIPTION)
            .append(" TEXT NOT NULL, ")
            .append(DISCOUNT_PERCENTAGE)
            .append(" REAL DEFAULT 0, ")
            .append(MAX_DISCOUNT_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(VALID_TILL)
            .append(" TEXT NOT NULL, ")
            .append(COUNT_ALLOWED_PER_USER)
            .append(" INTEGER DEFAULT 1, ")
            .append(TERMS_AND_CONDITIONS)
            .append(" TEXT NOT NULL, ")
            .append(PAID_VIA)
            .append(" TEXT NOT NULL, ")
            .append(IMAGE_NAME)
            .append(" TEXT NOT NULL) ").toString();


    public OfferDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getOffers() {
        return getReadableDatabase().query(DatabaseConstant.OFFERS_TABLE, null, null, null, null, null, null);
    }

    public Cursor getOffersByCode(String code) {
        return getReadableDatabase().query(DatabaseConstant.OFFERS_TABLE, null, OFFER_CODE + " =? ", new String[]{code}, null, null, null);
    }

    public void insert(ContentValues contentValues) {
        getWritableDatabase().insert(DatabaseConstant.OFFERS_TABLE, null, contentValues);
    }

    public Cursor executeQuery(String query, String[] selectionArgs) {
        return getWritableDatabase().rawQuery(query, selectionArgs);
    }

}
