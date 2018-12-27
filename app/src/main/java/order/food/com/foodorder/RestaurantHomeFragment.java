package order.food.com.foodorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MODIFY_LOCATION_REQUEST = 1;
    public static final int MODIFY_RESTAURANT_REQUEST = 2;
    RestaurantAdapter restaurantAdapter;
    RestaurantItemsDatabaseAdapter restaurantItemsDatabaseAdapter;

    Toolbar toolbar;
    RecyclerView restaurantRecyclerView;
    RelativeLayout searchViewLayout;
    TextView searchResultView, location, searchTxt, ratingTxtView;
    boolean sortRating = false, isOnlyVeg = false, offersOnly = false;
    Button modifyLocationBtn, cancelSearchTxtBtn;
    Context context;
    String loc, area, city, restaurantName;
    SharedPreferences contextSharedPreferences;
    boolean isLandscape;
    int position;

    public static final String RESTAURANTS = "Restaurants";


    public static RestaurantHomeFragment newInstance(Context c, int position) {
        RestaurantHomeFragment restaurantHomeFragment = new RestaurantHomeFragment();
        restaurantHomeFragment.context = c;
        restaurantHomeFragment.position = position;
        return restaurantHomeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LRUCacheLoader.initializeLRUCache();

        restaurantItemsDatabaseAdapter = new RestaurantItemsDatabaseAdapter(context);
        View view = inflater.inflate(R.layout.fragment_restaurent_home, container, false);


        fetchLocationIfNotAvailable();
        initializeToolBar(view);
        initializeViewElements(view);
        setValuesForViews();

        initializeViewListeners();

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    private void fetchLocationIfNotAvailable() {
        contextSharedPreferences = context.getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        if (contextSharedPreferences.getString(getString(R.string.location), "").isEmpty()) {
            Toast.makeText(context, getString(R.string.select_location), Toast.LENGTH_LONG).show();
            startModifyLocationActivity(location);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setValuesForViews();
        getLoaderManager().restartLoader(0, null, this);
    }

    private void initializeViewListeners() {
        modifyLocationBtn.setOnClickListener(v -> startModifyLocationActivity(v));
        ratingTxtView.setOnClickListener(v -> onRatingSortedClick(v));
    }

    private void initializeToolBar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.restaurant_menu);

        MenuItem menuItem = toolbar.getMenu().findItem(R.id.app_filter);

        if ((AddFilterFragment.savedCuisineId != null && AddFilterFragment.rapidFilterValues != null) &&
                (AddFilterFragment.savedCuisineId.indexOfValue(true) != -1 || AddFilterFragment.savedRapidId.indexOfValue(true) != -1)) {
            setCount(getActivity().getApplicationContext(), menuItem, "!");
        } else {
            setCount(getActivity().getApplicationContext(), menuItem, "0");
        }

        toolbar.setOnMenuItemClickListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.app_filter:
                    AddFilterFragment addFilterFragment =
                            AddFilterFragment.newInstance(toolbar);
                    addFilterFragment.show(this.getFragmentManager(),
                            getString(R.string.add_filter_fragment));
                    return true;
                case R.id.app_search:
                    Intent startModifyRestaurantIntent = new Intent(context, ModifyRestaurantActivity.class);
                    startModifyRestaurantIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(startModifyRestaurantIntent, MODIFY_RESTAURANT_REQUEST);
                    return true;
            }
            return false;
        });
    }

    public static void setCount(Context context, MenuItem menuItem, String count) {
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        menuItem.setIcon(icon);
    }


    private void setValuesForViews() {
        if (contextSharedPreferences.contains("location")) {
            location.setText(contextSharedPreferences.getString(getString(R.string.location), getString(R.string.location_txt)));
        }
        if (contextSharedPreferences.contains(getString(R.string.restaurant))) {
            searchTxt.setText(contextSharedPreferences.getString(getString(R.string.restaurant), ""));
            searchTxt.setVisibility(View.VISIBLE);
            searchViewLayout.requestLayout();
            searchViewLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            cancelSearchTxtBtn.setVisibility(View.VISIBLE);
        }
        setIsLandscapeValueBasedOnOrientation();
    }

    private void setIsLandscapeValueBasedOnOrientation() {
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }
    }

    private void initializeViewElements(View view) {
        restaurantRecyclerView = (RecyclerView) view.findViewById(R.id.restaurant_recycler_view);
        searchResultView = (TextView) view.findViewById(R.id.searchResult);
        ratingTxtView = (TextView) view.findViewById(R.id.sort_rating_txt);
        modifyLocationBtn = (Button) view.findViewById(R.id.modifyLocationBtn);
        location = (TextView) view.findViewById(R.id.location);
        searchTxt = (TextView) view.findViewById(R.id.searchTxt);
        cancelSearchTxtBtn = (Button) view.findViewById(R.id.btn_cancel_searchTxt);
        searchViewLayout = (RelativeLayout) view.findViewById(R.id.searchView);
        cancelSearchTxtBtn.setOnClickListener((v) -> {
            searchTxt.setVisibility(View.GONE);
            cancelSearchTxtBtn.setVisibility(View.GONE);
            contextSharedPreferences.edit().remove(getString(R.string.restaurant)).commit();
            searchViewLayout.requestLayout();
            searchViewLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            getLoaderManager().restartLoader(0, null, this);
        });
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String restaurantId = restaurantItemsDatabaseAdapter.getRestaurantIdByItemType(AddFilterFragment.getCuisineValues());
        initializeRestaurantParams();

        return new CursorLoader(context, DatabaseConstant.RESTAURANT_CONTENT_ID_URI_BASE, null, getSelection(isOnlyVeg, offersOnly, area, city, restaurantName, restaurantId), null, getOrderBy(sortRating));
    }

    private void initializeRestaurantParams() {
        SharedPreferences sharedPreference = contextSharedPreferences;
        loc = sharedPreference.getString(getString(R.string.location), "");
        area = sharedPreference.getString(RestaurantDatabaseAdapter.AREA_FIELD, loc);
        city = sharedPreference.getString(RestaurantDatabaseAdapter.CITY_FIELD, loc);
        restaurantName = sharedPreference.getString(getString(R.string.restaurant), "");
        if (AddFilterFragment.savedRapidId != null) {
            offersOnly = AddFilterFragment.savedRapidId.get(0);
            isOnlyVeg = AddFilterFragment.savedRapidId.get(1);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        restaurantAdapter = new RestaurantAdapter(cursor, context, isLandscape);
        searchResultView.setText(cursor.getCount() + " " + RESTAURANTS);
        restaurantRecyclerView.setHasFixedSize(true);
        if (isLandscape) {
            restaurantRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        if (position != -1) {
            restaurantRecyclerView.scrollToPosition(position);
        }
        restaurantAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        restaurantAdapter.swapCursor(null);
    }


    private String getOrderBy(boolean sortRatingAccending) {
        return (sortRatingAccending ? RestaurantDatabaseAdapter.OVERALL_RATING_FIELD + " ASC" : RestaurantDatabaseAdapter.OVERALL_RATING_FIELD + " DESC");
    }

    public static String getSelection(boolean isVeg, boolean offersOnly, String area, String city, String restaurantName, String restaurantId) {
        StringBuilder result = new StringBuilder();
        String operator = "AND";
        if (area.equals(city)) {
            operator = "OR";
        }
        result.append(RestaurantDatabaseAdapter.AREA_FIELD + " GLOB '*" + area + "*' " + operator + " " + RestaurantDatabaseAdapter.CITY_FIELD + " GLOB '*" + city + "*' AND " + RestaurantDatabaseAdapter.NAME_FIELD + " GLOB '*" + restaurantName + "*' ");
        if (isVeg) {
            result.append("AND " + RestaurantDatabaseAdapter.IS_VEG_FIELD + " = 0 ");
        }
        if (offersOnly) {
            result.append("AND " + RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD + " > 0 ");
        }
        if (restaurantId != null) {
            result.append("AND " + DatabaseConstant.ID + " IN " + restaurantId);
        }
        return result.toString();
    }

    public void onRatingSortedClick(View v) {
        if (sortRating == false) {
            sortRating = true;
            ratingTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
        } else {
            sortRating = false;
            ratingTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
        }
        getLoaderManager().restartLoader(0, null, this);
        restaurantAdapter.notifyDataSetChanged();
        restaurantRecyclerView.smoothScrollToPosition(0);
    }

    public void startModifyLocationActivity(View v) {
        Intent startModifyLocationIntent = new Intent(context, ModifyLocationActivity.class);
        startModifyLocationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(startModifyLocationIntent, MODIFY_LOCATION_REQUEST);
    }

}