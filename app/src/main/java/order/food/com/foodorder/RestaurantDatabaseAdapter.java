package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantDatabaseAdapter extends SQLiteOpenHelper {

    public static final String NAME_FIELD = "name";
    public static final String AREA_FIELD = "area";
    public static final String CITY_FIELD = "city";
    public static final String IMAGE_FIELD = "image";
    public static final String OVERALL_RATING_FIELD = "overallRating";
    public static final String RATING_COUNT_FIELD = "ratingCount";
    public static final String OFFER_PERCENTAGE_FIELD = "offerPercentage";
    public static final String MIN_OFFER_PRICE_FIELD = "minOfferPrice";
    public static final String IS_VEG_FIELD = "isVeg";
    public static final String OPENS_AT_FIELD = "opensAt";
    public static final String CLOSES_FIELD = "closesAt";
    public static final String GST_PERCENTAGE_FIELD = "gstPercentage";
    public static final String PACKING_CHARGES_FIELD = "packingCharges";

    public static final String CREATE_RESTAURANT_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.RESTAURANT_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(NAME_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(AREA_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(CITY_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(IMAGE_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(OVERALL_RATING_FIELD)
            .append(" REAL DEFAULT 0, ")
            .append(RATING_COUNT_FIELD)
            .append(" INTEGER DEFAULT 0, ")
            .append(OFFER_PERCENTAGE_FIELD)
            .append(" INTEGER DEFAULT 0, ")
            .append(MIN_OFFER_PRICE_FIELD)
            .append(" REAL DEFAULT 0, ")
            .append(IS_VEG_FIELD)
            .append(" INTEGER DEFAULT 0, ")
            .append(OPENS_AT_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(CLOSES_FIELD)
            .append(" TEXT NOT NULL, ")
            .append(GST_PERCENTAGE_FIELD)
            .append(" REAL DEFAULT 0, ")
            .append(PACKING_CHARGES_FIELD)
            .append(" REAL DEFAULT 0)").toString();

    public RestaurantDatabaseAdapter(Context ctx) {
        super(ctx, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    public void updateRestaurantReviewByRestaurantId(int restaurantId, int reviewCount, double overAllRating) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RATING_COUNT_FIELD, reviewCount);
        contentValues.put(OVERALL_RATING_FIELD, overAllRating);
        getWritableDatabase().update(DatabaseConstant.RESTAURANT_TABLE, contentValues, DatabaseConstant.ID + " = ?",
                new String[]{String.valueOf(restaurantId)});
    }

    public Cursor getRestaurants(String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return getReadableDatabase().query(DatabaseConstant.RESTAURANT_TABLE, columns, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getRestaurantsById(int restaurantId) {
        return getReadableDatabase().query(DatabaseConstant.RESTAURANT_TABLE, null, DatabaseConstant.ID + " = ? ",
                new String[]{String.valueOf(restaurantId)}, null, null, null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
