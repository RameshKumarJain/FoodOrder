package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrdersDatabaseAdapter extends SQLiteOpenHelper {

    public static final String USERID = "userId";
    public static final String RESTAURANT_ID = "restaurantId";
    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String DATE = "date";
    public static final String PAID_VIA = "paidVia";
    public static final String DELIVERY_ADDRESS = "deliveryAddress";
    public static final String GST_CHARGES = "gstCharges";
    public static final String PACKING_CHARGES = "packingCharges";
    public static final String TOTAL_PRICE = "totalPrice";
    public static final String SUB_TOTAL_PRICE = "subTotalPrice";
    public static final String GRAND_TOTAL_PRICE = "grandTotalPrice";
    public static final String COUPON_CODE = "couponCode";
    public static final String COUPON_DISCOUNT_PRICE = "couponDiscountPrice";
    public static final String RESTAURANT_OFFER_PERCENTAGE = "restaurantOfferPercentage";
    public static final String RESTAURANT_OFFER_PRICE = "restaurantDiscountPrice";
    public static final String ORDER_STATUS = "orderStatus";
    public static final String ORDER_MESSAGE = "orderMessage";
    public static final String REVIEW = "review";

    public static final String CREATE_ORDERS_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.ORDERS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(USERID)
            .append(" INTEGER NOT NULL, ")
            .append(RESTAURANT_ID)
            .append(" INTEGER NOT NULL, ")
            .append(RESTAURANT_NAME)
            .append(" TEXT NOT NULL, ")
            .append(DATE)
            .append(" TEXT NOT NULL, ")
            .append(PAID_VIA)
            .append(" TEXT NOT NULL, ")
            .append(DELIVERY_ADDRESS)
            .append(" TEXT NOT NULL, ")
            .append(GST_CHARGES)
            .append(" REAL DEFAULT 0, ")
            .append(PACKING_CHARGES)
            .append(" REAL DEFAULT 0, ")
            .append(SUB_TOTAL_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(TOTAL_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(GRAND_TOTAL_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(COUPON_CODE)
            .append(" TEXT NOT NULL, ")
            .append(COUPON_DISCOUNT_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(RESTAURANT_OFFER_PERCENTAGE)
            .append(" INTEGER DEFAULT 0, ")
            .append(RESTAURANT_OFFER_PRICE)
            .append(" REAL DEFAULT 0, ")
            .append(ORDER_STATUS)
            .append(" TEXT NOT NULL, ")
            .append(ORDER_MESSAGE)
            .append(" TEXT NOT NULL, ")
            .append(REVIEW)
            .append(" INTEGER DEFAULT 0)").toString();

    public OrdersDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insert(ContentValues contentValues) {
        return getWritableDatabase().insert(DatabaseConstant.ORDERS_TABLE, null, contentValues);
    }

    public Cursor getOrdersByUserId(int userId) {
        return getReadableDatabase().query(DatabaseConstant.ORDERS_TABLE, null, USERID + "=?", new String[]{String.valueOf(userId)}, null, null, DatabaseConstant.ID + " DESC");
    }

    public Cursor getOrderByOrderIdAndUserId(int userId, long orderId) {
        return getReadableDatabase().query(DatabaseConstant.ORDERS_TABLE, null, USERID + " = ? AND " + DatabaseConstant.ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(orderId)}, null, null, null);
    }

    public void updateOrderStatusByOrderId(String status, int userId, long orderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_STATUS, status);
        getWritableDatabase().update(DatabaseConstant.ORDERS_TABLE, contentValues, USERID + " = ? AND " + DatabaseConstant.ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(orderId)});
    }

    public void updateOrderReviewForOrderId(int review, long orderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(REVIEW, review);
        getWritableDatabase().update(DatabaseConstant.ORDERS_TABLE, contentValues, DatabaseConstant.ID + " = ?",
                new String[]{String.valueOf(orderId)});
    }
}
