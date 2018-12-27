package order.food.com.foodorder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ModifyRestaurantActivity extends AppCompatActivity {

    private boolean cancelFlag = false;

    ListView frequentResultList;
    EditText searchRestaurantTxt;
    ImageView searchIcon;

    FrequentRestaurantDatabaseAdapter frequentRestaurantDatabaseAdapter;
    List<String> searchedRestaurantValues = new ArrayList<>();
    List<String> frequentRestaurantValues = new ArrayList<>();

    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_restaurant);

        initializeToolBar();

        initializeRestaurantValues();

        initializeViews();

        frequentRestaurantDatabaseAdapter = new FrequentRestaurantDatabaseAdapter(this);

        initializeSearchIconListener();

        setListenerForSearchRestaurantEditTxt();

        addValuesToFrequentResultList();

        frequentResultList.setOnItemClickListener(getOnItemClickListener());
    }

    private void addValuesToFrequentResultList() {
        Cursor c = frequentRestaurantDatabaseAdapter.getFrequentSearchedText(HomeActivity.userId);
        initializeLocationValuesFromCursor(c, FrequentRestaurantDatabaseAdapter.SEARCH_TEXT);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, frequentRestaurantValues);
        frequentResultList.setAdapter(adapter);
    }

    private void initializeSearchIconListener() {
        searchIcon.setOnClickListener(v -> {
            if (cancelFlag) {
                searchRestaurantTxt.setText("");
                searchRestaurantTxt.clearFocus();
                frequentResultList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeViews() {
        searchRestaurantTxt = (EditText) findViewById(R.id.search_restaurant);
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        frequentResultList = (ListView) findViewById(R.id.frequent_search_result);
    }

    private void setListenerForSearchRestaurantEditTxt() {

        searchRestaurantTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedRestaurantValues);
                adapter.getFilter().filter(searchRestaurantTxt.getText());
                frequentResultList.setAdapter(adapter);
                searchIcon.setImageResource(R.drawable.ic_cancel_black_24dp);
                frequentResultList.setVisibility(View.GONE);
                cancelFlag = true;
            } else {
                searchIcon.setImageResource(R.drawable.ic_search_black_24dp);
                cancelFlag = false;
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, frequentRestaurantValues);
                frequentResultList.setAdapter(adapter);
                frequentResultList.setVisibility(View.VISIBLE);
            }
        });

        searchRestaurantTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s != null && s.length() > 0) {
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                    frequentResultList.setVisibility(View.VISIBLE);
                } else {
                    frequentResultList.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    @NonNull
    private AdapterView.OnItemClickListener getOnItemClickListener() {
        return (parent, view, position, d) -> {
            String restaurant = adapter.getItem(position);
            initializeSharedPreferences(restaurant);
            addToFrequentLocation(restaurant);
            onBackPressed();
        };
    }

    private void initializeSharedPreferences(String restaurant) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.restaurant), restaurant);
        editor.commit();
    }

    public void addToFrequentLocation(String restaurant) {
        ContentValues values = new ContentValues();
        if (frequentRestaurantValues.contains(restaurant)) {
            StringBuilder updateStatement = new StringBuilder("update " + DatabaseConstant.FREQUENT_SEARCHES_TABLE);
            updateStatement.append(" SET ");
            updateStatement.append(FrequentRestaurantDatabaseAdapter.SEARCH_COUNT + " = " + FrequentRestaurantDatabaseAdapter.SEARCH_COUNT + " + 1");
            updateStatement.append(" WHERE " + FrequentRestaurantDatabaseAdapter.SEARCH_TEXT + " = \"" + restaurant + "\";");
            frequentRestaurantDatabaseAdapter.update(updateStatement.toString());
        } else {
            if (frequentRestaurantValues.size() < 10) {
                values.put(FrequentRestaurantDatabaseAdapter.SEARCH_TEXT, restaurant);
                values.put(FrequentRestaurantDatabaseAdapter.USERID, HomeActivity.userId);
                values.put(FrequentRestaurantDatabaseAdapter.SEARCH_COUNT, 1);
                frequentRestaurantDatabaseAdapter.insert(values);
            } else {
                values.put(FrequentRestaurantDatabaseAdapter.SEARCH_COUNT, 1);
                values.put(FrequentRestaurantDatabaseAdapter.SEARCH_TEXT, restaurant);
                frequentRestaurantDatabaseAdapter.update(values, HomeActivity.userId, frequentRestaurantValues.get(9));
            }
        }
    }

    private void initializeRestaurantValues() {
        String loc, area, city, restaurantName;
        SharedPreferences sharedPreference = getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        loc = sharedPreference.getString("location", "");
        area = sharedPreference.getString(RestaurantDatabaseAdapter.AREA_FIELD, loc);
        city = sharedPreference.getString(RestaurantDatabaseAdapter.CITY_FIELD, loc);

        Cursor c = getContentResolver().query(DatabaseConstant.RESTAURANT_CONTENT_ID_URI_BASE, new String[]{RestaurantDatabaseAdapter.NAME_FIELD},
                RestaurantHomeFragment.getSelection(false, false, area, city, "", null), null, null);
        if (c != null && c.getCount() != 0) {
            searchedRestaurantValues = null;
            searchedRestaurantValues = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String value = c.getString(c.getColumnIndexOrThrow(RestaurantDatabaseAdapter.NAME_FIELD));
                searchedRestaurantValues.add(value);
                c.moveToNext();
            }
        }
    }

    public void initializeLocationValuesFromCursor(Cursor c, String field) {
        if (c != null && c.getCount() != 0) {
            frequentRestaurantValues = null;
            frequentRestaurantValues = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                frequentRestaurantValues.add(c.getString(c.getColumnIndexOrThrow(field)));
                c.moveToNext();
            }
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }
}
