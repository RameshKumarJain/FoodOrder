package order.food.com.foodorder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RestaurantContentProvider extends ContentProvider {

    private static RestaurantDatabaseAdapter restaurantDatabaseAdapter;

    private static RestaurantItemsDatabaseAdapter restaurantItemsDatabaseAdapter;

    @Override
    public boolean onCreate() {
        restaurantDatabaseAdapter = new RestaurantDatabaseAdapter(getContext());
        restaurantItemsDatabaseAdapter = new RestaurantItemsDatabaseAdapter(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int requestCode = DatabaseConstant.uriMathcer.match(uri);
        switch (requestCode) {
            case DatabaseConstant.RESTAURANT_CODE:
                return restaurantDatabaseAdapter.getRestaurants(projection, selection, selectionArgs, sortOrder);
            case DatabaseConstant.RESTAURANT_ITEMS_CODE:
                return restaurantItemsDatabaseAdapter.getRestaurantItems(selection, selectionArgs, null);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
