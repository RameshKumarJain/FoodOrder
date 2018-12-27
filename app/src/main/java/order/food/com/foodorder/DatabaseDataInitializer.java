package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;

public class DatabaseDataInitializer implements Runnable {

    private static final String RESTAURANT_DATA_TXT = "restaurant_data.txt";
    private static final String OFFERS_DATA_TXT = "offers_data.txt";
    private Context ctnx;
    private Boolean flag = false;


    public DatabaseDataInitializer(Context context) {
        this.ctnx = context;
    }

    @Override
    public void run() {
        DatabaseInstaller dbInstaller = new DatabaseInstaller(ctnx);
        dbInstaller.getWritableDatabase();
        dbInstaller.close();
        if (flag) {
            dbInstaller.initializeDataFromFile(RESTAURANT_DATA_TXT, DatabaseConstant.RESTAURANT_TABLE);
            dbInstaller.initializeDataFromFile(OFFERS_DATA_TXT, DatabaseConstant.OFFERS_TABLE);
            dbInstaller.initializeRestaurantItems();
        }
    }

    protected class DatabaseInstaller extends SQLiteOpenHelper {

        public DatabaseInstaller(Context context) {
            super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(UsersDatabaseAdapter.CREATE_USER_TABLE);
            db.execSQL(RestaurantDatabaseAdapter.CREATE_RESTAURANT_TABLE);
            db.execSQL(FrequentLocationDatabaseAdapter.CREATE_FREQUENT_LOCATION_TABLE);
            db.execSQL(FrequentRestaurantDatabaseAdapter.CREATE_FREQUENT_RESTAURANT_TABLE);
            db.execSQL(SavedCardDatabaseAdapter.CREATE_SAVED_CARDS_TABLE);
            db.execSQL(OfferDatabaseAdapter.CREATE_OFFER_TABLE);
            db.execSQL(RestaurantItemsDatabaseAdapter.CREATE_RESTAURANT_ITEMS_TABLE);
            db.execSQL(CouponHistoryDatabaseAdapter.CREATE_COUPON_HISTORY_TABLE);
            db.execSQL(OrdersDatabaseAdapter.CREATE_ORDERS_TABLE);
            db.execSQL(OrderItemDatabaseAdapter.CREATE_ORDER_ITEM_TABLE);
            db.execSQL("insert into cards values(1,1,\"5242540300646082\", \"Ramesh Kumar M\",06,2020 );");
            flag = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void initializeDataFromFile(String fileName, String tableName) {
            JSONArray jsonArray = JsonHelper.readJsonFromFile(ctnx.getAssets(), fileName);
            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues value = null;
                value = JsonHelper.convertJsonObjectToContentValues(jsonArray, i);
                getWritableDatabase().insert(tableName, null, value);
            }
            close();
        }

        public void initializeRestaurantItems() {
            SQLiteDatabase db = getWritableDatabase();
            int count = 1;
            for (int i = 1; i < 69; i++) {
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Idly\", \"South Indian\",\"Breakfast\",12,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Dosa\", \"South Indian\",\"Breakfast\",30,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Puri\", \"South Indian\",\"Breakfast\",40,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Utthapam\", \"South Indian\",\"Breakfast\",45,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Ghee Roast\", \"South Indian\",\"Breakfast\",55,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Masala Roast\", \"South Indian\",\"Breakfast\",65,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Plain Roast\", \"South Indian\",\"Breakfast\",50,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Meals\", \"South Indian\",\"Lunch\",90,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Veg Briyani\", \"Briyani\",\"Main Course\",85,0 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Chicken Briyani\", \"Briyani\",\"Main Course\",120,1 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Chicken Fried Rice\", \"Chinise\",\"Main Course\",105,1 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Mutton pepper fry\", \"Chinese\",\"Starter\",140,1 );");
                db.execSQL("insert into restaurant_items values(" + count++ + "," + i + ",\"Chapathi\", \"North Indian\",\"Dinner\",20,0 );");
            }
        }
    }
}
