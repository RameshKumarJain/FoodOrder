package order.food.com.foodorder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantItemsDatabaseAdapter extends SQLiteOpenHelper {

    public static final String RESTAURANT_ID = "restaurantId";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_TYPE = "itemType";
    public static final String ITEM_MENU = "itemMenu";
    public static final String ITEM_PRICE = "itemPrice";
    public static final String IS_VEG = "isVeg";

    public static final String CREATE_RESTAURANT_ITEMS_TABLE = new StringBuilder()
            .append("CREATE TABLE " + DatabaseConstant.RESTAURANT_ITEMS_TABLE + " (")
            .append(DatabaseConstant.ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(RESTAURANT_ID)
            .append(" INTEGER NOT NULL, ")
            .append(ITEM_NAME)
            .append(" TEXT NOT NULL, ")
            .append(ITEM_TYPE)
            .append(" TEXT NOT NULL, ")
            .append(ITEM_MENU)
            .append(" TEXT NOT NULL, ")
            .append(ITEM_PRICE)
            .append(" REAL NOT NULL, ")
            .append(IS_VEG)
            .append(" INTEGER DEFAULT 0, ")
            .append("FOREIGN KEY(" + RESTAURANT_ID + ") REFERENCES restaurant(_id))").toString();


    public RestaurantItemsDatabaseAdapter(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getRestaurantItemsByRestaurantId(int restaurantId) {
        return getWritableDatabase().query(DatabaseConstant.RESTAURANT_ITEMS_TABLE, null, null, null, null, null, null);

    }

    public Cursor getRestaurantItems(String selection,
                                     String[] selectionArgs, String groupBy) {
        return getWritableDatabase().query(DatabaseConstant.RESTAURANT_ITEMS_TABLE, null, selection, selectionArgs, groupBy, null, null);

    }

    public Cursor getRestaurantItemsByRestaurantIdAndMenuType(int restaurantId, String menuType) {
        return getWritableDatabase().query(DatabaseConstant.RESTAURANT_ITEMS_TABLE, null,
                RESTAURANT_ID + " =? AND " + ITEM_MENU + " =?", new String[]{String.valueOf(restaurantId), menuType}, null, null, null);
    }

    public String getRestaurantIdByItemType(String itemTypes) {
        String query = "select distinct(restaurantId) from restaurant_items";
        if (itemTypes != null) {
            query = query + " where itemType IN " + itemTypes;
        }
        Cursor c = getReadableDatabase().rawQuery(query, null, null);

        StringBuilder result = new StringBuilder("( ");
        int i;
        for (i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            result.append("'" + c.getString(0) + "', ");
        }
        result.append(")");
        String finalResult = result.toString();
        if (finalResult.contains(", )")) {
            finalResult = finalResult.replace(", )", " )");
        }
        return finalResult;


    }

}
