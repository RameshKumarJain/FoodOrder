package order.food.com.foodorder;

import android.content.UriMatcher;
import android.net.Uri;

public class DatabaseConstant {

    public static final String DATABASE_NAME = "food_db";
    public static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "order.food.com.foodorder";
    private static final String SCHEME = "content://";

    private static final String PATH_RESTAURANT_ID = "/restaurant/";
    private static final String PATH_RESTAURANT_ITEM_ID = "/restaurantitem/";

    public static final int RESTAURANT_CODE = 1;

    public static final int RESTAURANT_ITEMS_CODE = 2;


    public static final Uri RESTAURANT_CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_RESTAURANT_ID);
    public static final Uri RESTAURANT_ITEM_CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_RESTAURANT_ITEM_ID);

    public static final String USERS_TABLE = "users";
    public static final String RESTAURANT_TABLE = "restaurant";
    public static final String RESTAURANT_ITEMS_TABLE = "restaurant_items";
    public static final String OFFERS_TABLE = "offers";
    public static final String COUPON_HISTORY_TABLE = "coupon_history";
    public static final String FREQUENT_LOCATION_TABLE = "frequent_location";
    public static final String FREQUENT_SEARCHES_TABLE = "frequent_searches";
    public static final String ORDERS_TABLE = "orders";
    public static final String ORDER_ITEMS_TABLE = "order_items";
    public static final String CARDS_TABLE = "cards";

    public static final String ID = "_id";

    public static UriMatcher uriMathcer = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMathcer.addURI(AUTHORITY, PATH_RESTAURANT_ID, RESTAURANT_CODE);
        uriMathcer.addURI(AUTHORITY, PATH_RESTAURANT_ITEM_ID, RESTAURANT_ITEMS_CODE);
    }

}


