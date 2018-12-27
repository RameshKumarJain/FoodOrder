package order.food.com.foodorder;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class RestaurantItemsActivity extends AppCompatActivity {
    public static int restaurantId;
    RestaurantDatabaseAdapter restaurantDatabaseAdapter;
    RestaurantItemsDatabaseAdapter restaurantItemsDatabaseAdapter;
    public static Cursor restaurantCursor;

    Toolbar toolbar;
    TextView restaurant_rating;
    TextView restaurant_offer_txt;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView restaurantItemViewImg;
    RecyclerView restaurantItemRecyclerView;
    RestaurantItemAdapter restaurantItemAdapter;
    BottomAppBar bottomAppBar;
    TextView checkoutTxtView;

    boolean isFromBasketPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_items);
        restaurantDatabaseAdapter = new RestaurantDatabaseAdapter(this);
        restaurantItemsDatabaseAdapter = new RestaurantItemsDatabaseAdapter(this);

        isFromBasketPage = getIntent().getBooleanExtra("fromBasketPage", false);

        initializeRestaurantCursorDetails();

        initializeToolBar();
        initializeViews();
        initializeCollapseToolbar();

        setValuesToRestaurantItemRecylerView();

        setBottomAppBarIfItemsAddedInBasket();
    }

    private void setBottomAppBarIfItemsAddedInBasket() {
        if (RestaurantBasketDetails.count > 0) {
            bottomAppBar.setVisibility(View.VISIBLE);
            checkoutTxtView.setText("Checkout ₹ " + RestaurantBasketDetails.total);
        }
    }

    private void setValuesToRestaurantItemRecylerView() {
        Cursor itemCursor = restaurantItemsDatabaseAdapter.getRestaurantItems(RestaurantItemsDatabaseAdapter.RESTAURANT_ID + " =? ", new String[]{String.valueOf(restaurantId)}, RestaurantItemsDatabaseAdapter.ITEM_MENU);
        restaurantItemAdapter = new RestaurantItemAdapter(this, itemCursor, bottomAppBar, checkoutTxtView);
        restaurantItemRecyclerView.setHasFixedSize(true);
        restaurantItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantItemRecyclerView.setAdapter(restaurantItemAdapter);

        if (restaurantItemAdapter.position != -1) {
            restaurantItemRecyclerView.scrollToPosition(restaurantItemAdapter.position);
        }
    }

    private void initializeCollapseToolbar() {
        String restaurantTitle = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.NAME_FIELD));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(restaurantTitle);
        collapsingToolbarLayout.setContentScrimColor(getColor(R.color.colorAccent));
    }

    private void initializeViews() {
        String restaurantImgName = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.IMAGE_FIELD));
        String restaurantRating = String.valueOf(restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OVERALL_RATING_FIELD)));

        restaurantItemViewImg = (ImageView) findViewById(R.id.restaurant_item_view_img);
        restaurant_rating = (TextView) findViewById(R.id.res_rating_txt);
        restaurant_offer_txt = (TextView) findViewById(R.id.item_offer_txt);

        restaurant_rating.setText(restaurantRating + " ★");
        RestaurantAdapter.setOfferString(restaurant_offer_txt, restaurantCursor);

        restaurantItemRecyclerView = findViewById(R.id.item_recycler_view);

        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        checkoutTxtView = findViewById(R.id.checkout_txt);
        bottomAppBar.setOnClickListener(v -> {
            moveToBasketFragment();
        });

        try {
            restaurantItemViewImg.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(restaurantImgName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveToBasketFragment() {
        Intent homeActivityIntent = new Intent(this.getApplicationContext(), HomeActivity.class);
        homeActivityIntent.putExtra("moveToBasket", true);
        homeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeActivityIntent);
        finish();
    }

    private void initializeToolBar() {
        toolbar = (Toolbar) findViewById(R.id.res_item_toolbar);
        toolbar.inflateMenu(R.menu.restaurant_menu);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> {
            if (isFromBasketPage) {
                moveToBasketFragment();
            }
            onBackPressed();
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));
    }

    private void initializeRestaurantCursorDetails() {
        restaurantId = getIntent().getIntExtra(RestaurantItemsDatabaseAdapter.RESTAURANT_ID, 0);
        restaurantCursor = restaurantDatabaseAdapter.getRestaurantsById(restaurantId);
        if (restaurantCursor != null) {
            restaurantCursor.moveToFirst();
        }
        restaurantDatabaseAdapter.close();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
