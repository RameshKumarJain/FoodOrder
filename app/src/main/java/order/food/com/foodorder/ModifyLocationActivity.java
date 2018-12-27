package order.food.com.foodorder;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ModifyLocationActivity extends AppCompatActivity {

    private static int count = 0;

    private boolean cancelFlag = false;

    ListView frequentResultList;

    EditText searchLocationTxt;

    ImageView searchIcon;

    FrequentLocationDatabaseAdapter frequentLocationDatabaseAdapter;
    List<String> searchedLocationValues = new ArrayList<>();
    List<String> frequentLocationValues = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_location);

        initializeToolBar();

        initializeLocationValues();

        initializeViews();
        initializeSearchIconListener();

        initializeOnFocusListenerForSearchLocationTxt();
        initializeOnTextChangeListenerForSearchLocationTxt();


        frequentLocationDatabaseAdapter = new FrequentLocationDatabaseAdapter(this);
        Cursor c = frequentLocationDatabaseAdapter.getFrequentSearchedLocation(HomeActivity.userId);

        initializeLocationValuesFromCursor(c, FrequentLocationDatabaseAdapter.SEARCH_LOCATION);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, frequentLocationValues);
        frequentResultList.setAdapter(adapter);

        frequentResultList.setOnItemClickListener(getOnItemClickListener());

    }

    private void initializeSearchIconListener() {
        searchIcon.setOnClickListener(v -> {
            if (cancelFlag) {
                searchLocationTxt.setText("");
                searchLocationTxt.clearFocus();
                frequentResultList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeViews() {
        searchLocationTxt = (EditText) findViewById(R.id.searchLocation);
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        frequentResultList = (ListView) findViewById(R.id.frequent_search_result);
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> {
            preventBackPressedIfLocationNotSelected();
        });
    }

    private void initializeOnFocusListenerForSearchLocationTxt() {
        searchLocationTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedLocationValues);
                adapter.getFilter().filter(searchLocationTxt.getText());
                frequentResultList.setAdapter(adapter);
                frequentResultList.setVisibility(View.GONE);
                searchIcon.setImageResource(R.drawable.ic_cancel_black_24dp);
                cancelFlag = true;
            } else {
                searchIcon.setImageResource(R.drawable.ic_search_black_24dp);
                cancelFlag = false;
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, frequentLocationValues);
                frequentResultList.setAdapter(adapter);
                frequentResultList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeOnTextChangeListenerForSearchLocationTxt() {
        searchLocationTxt.addTextChangedListener(new TextWatcher() {

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
                    frequentResultList.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                } else {
                    frequentResultList.setVisibility(View.GONE);
                }
            }
        });
    }

    @NonNull
    private AdapterView.OnItemClickListener getOnItemClickListener() {
        return (parent, view, position, d) -> {
            String loc = adapter.getItem(position);
            initializeSharedPreferences(loc);
            addToFrequentLocation(loc);
            onBackPressed();
        };
    }

    private void initializeSharedPreferences(String location) {
        String[] value = location.split(", ");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.location), location);
        if (value.length > 1) {
            editor.putString(RestaurantDatabaseAdapter.AREA_FIELD, value[0]);
            editor.putString(RestaurantDatabaseAdapter.CITY_FIELD, value[1]);
        } else {
            editor.remove(RestaurantDatabaseAdapter.AREA_FIELD);
            editor.remove(RestaurantDatabaseAdapter.CITY_FIELD);
        }
        editor.commit();
    }

    public void addToFrequentLocation(String location) {
        ContentValues values = new ContentValues();
        if (frequentLocationValues.contains(location)) {
            StringBuilder updateStatement = new StringBuilder("update " + DatabaseConstant.FREQUENT_LOCATION_TABLE);
            updateStatement.append(" SET ");
            updateStatement.append(FrequentLocationDatabaseAdapter.SEARCH_COUNT + " = " + FrequentLocationDatabaseAdapter.SEARCH_COUNT + " + 1");
            updateStatement.append(" WHERE " + FrequentLocationDatabaseAdapter.SEARCH_LOCATION + " = \"" + location + "\";");
            frequentLocationDatabaseAdapter.update(updateStatement.toString());
        } else {
            if (frequentLocationValues.size() < 10) {
                values.put(FrequentLocationDatabaseAdapter.SEARCH_LOCATION, location);
                values.put(FrequentLocationDatabaseAdapter.USERID, HomeActivity.userId);
                values.put(FrequentLocationDatabaseAdapter.SEARCH_COUNT, 1);
                frequentLocationDatabaseAdapter.insert(values);
            } else {
                values.put(FrequentLocationDatabaseAdapter.SEARCH_COUNT, 1);
                values.put(FrequentLocationDatabaseAdapter.SEARCH_LOCATION, location);
                frequentLocationDatabaseAdapter.update(values, HomeActivity.userId, frequentLocationValues.get(9));
            }
        }
    }

    public void initializeLocationValues() {
        String area, city;
        Cursor c = getContentResolver().query(DatabaseConstant.RESTAURANT_CONTENT_ID_URI_BASE, new String[]{RestaurantDatabaseAdapter.AREA_FIELD, RestaurantDatabaseAdapter.CITY_FIELD}, null, null, null);
        if (c != null && c.getCount() != 0) {
            searchedLocationValues = null;
            searchedLocationValues = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                area = c.getString(c.getColumnIndexOrThrow(RestaurantDatabaseAdapter.AREA_FIELD));
                city = c.getString(c.getColumnIndexOrThrow(RestaurantDatabaseAdapter.CITY_FIELD));
                String value = area + ", " + city;
                if (!searchedLocationValues.contains(value)) {
                    searchedLocationValues.add(value);
                }
                if (!searchedLocationValues.contains(city)) {
                    searchedLocationValues.add(city);
                }
                c.moveToNext();
            }
        }
    }


    public void initializeLocationValuesFromCursor(Cursor c, String field) {
        if (c != null && c.getCount() != 0) {
            frequentLocationValues = null;
            frequentLocationValues = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                frequentLocationValues.add(c.getString(c.getColumnIndexOrThrow(field)));
                c.moveToNext();
            }
        }

    }


    public void fetchCurrentLocation(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            MyLocation myLocation = new MyLocation();
            Toast.makeText(getApplicationContext(), "Fetching nearest location", Toast.LENGTH_SHORT).show();
            myLocation.getLocation(this, locationResult);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLocation myLocation = new MyLocation();
                    myLocation.getLocation(this, locationResult);

                }
                return;
            }
        }
    }

    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getApplicationContext(), new GeocoderHandler());
                return;
            }
        }
    };

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(getApplicationContext(), "Completed Fetching nearest location", Toast.LENGTH_SHORT).show();
            if (locationAddress.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Unable to fetch nearest location", Toast.LENGTH_SHORT).show();
            }
            searchLocationTxt.setText(locationAddress);
            searchIcon.setImageResource(R.drawable.ic_cancel_black_24dp);
            cancelFlag = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            preventBackPressedIfLocationNotSelected();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void preventBackPressedIfLocationNotSelected() {
        String loc = getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getString(getString(R.string.location), "");
        if (loc.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_location), Toast.LENGTH_LONG).show();
            count++;
        } else {
            count = 0;
            onBackPressed();
        }
        if (count > 2) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Please select location, or do you want to exit?");
            dialog.setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finishAffinity();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    count = 0;
                    paramDialogInterface.dismiss();
                }

            });
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }
}
