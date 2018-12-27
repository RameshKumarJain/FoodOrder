package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrderItemDatabaseAdapter extends SQLiteOpenHelper {

    public static final String ORDER_ID = "orderId";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_PRICE = "itemPrice";
    public static final String ITEM_QUANTITY = "itemQuantity";
    public static final String ITEM_IS_VEG = "itemIsVeg";
    public static final String ITEM_OFFER_PRICE = "itemOfferPrice";

    public static final String CREATE_ORDER_ITEM_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.ORDER_ITEMS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(ORDER_ID)
            .append(" INTEGER NOT NULL, ")
            .append(ITEM_NAME)
            .append(" TEXT NOT NULL, ")
            .append(ITEM_PRICE)
            .append(" REAL NOT NULL, ")
            .append(ITEM_QUANTITY)
            .append(" INTEGER NOT NULL, ")
            .append(ITEM_IS_VEG)
            .append(" INTEGER NOT NULL, ")
            .append(ITEM_OFFER_PRICE)
            .append(" REAL NOT NULL, ")
            .append("FOREIGN KEY(" + ORDER_ID + ") REFERENCES orders(_id))").toString();


    public OrderItemDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertOrderItems(ContentValues contentValues) {
        getWritableDatabase().insert(DatabaseConstant.ORDER_ITEMS_TABLE, null, contentValues);
    }

    public Cursor getOrderItemsByOrderId(int orderId) {
        return getReadableDatabase().query(DatabaseConstant.ORDER_ITEMS_TABLE, null, ORDER_ID + "=? ", new String[]{String.valueOf(orderId)}, null, null, null);
    }
}
