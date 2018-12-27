package order.food.com.foodorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Stack;

public class HomeActivity extends AppCompatActivity implements AddFilterFragment.Dismissed, OnItemModifyListener {

    private int count = 0;
    int currentFragmentId = 0;
    int position = -1;

    BottomNavigationView bottomNavigationView;
    RestaurantHomeFragment restaurantHomeFragment;
    OffersFragment offersFragment;


    public static int userId;
    FragmentTransaction transaction;

    Stack<Integer> navigationHistory = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getInt(FrequentRestaurantDatabaseAdapter.USERID, 0);

        initializeBottomNavigationBarWithListener();

        startFragmentBasedOnIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBasketCount();
    }

    private void updateBasketCount() {
        MenuItem basketMenu = bottomNavigationView.getMenu().findItem(R.id.app_basket);
        int itemCount = RestaurantBasketDetails.getItemCount();
        RestaurantHomeFragment.setCount(getApplicationContext(), basketMenu, String.valueOf(itemCount));
    }

    private void startFragmentBasedOnIntent() {
        if (getIntent().getBooleanExtra("moveToBasket", false)) {
            startBasketFragment();
            navigationHistory.push(R.id.app_basket);
            bottomNavigationView.getMenu().findItem(R.id.app_basket).setChecked(true);
            currentFragmentId = R.id.app_basket;
        } else {
            //Default restaurant home fragment
            startHomeFragment();
            currentFragmentId = R.id.app_home;
            navigationHistory.push(R.id.app_home);
        }
    }

    private void initializeBottomNavigationBarWithListener() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            currentFragmentId = item.getItemId();
            position = -1;
            switch (item.getItemId()) {
                case R.id.app_home:
                    startHomeFragment();
                    addToNavigationHistory(R.id.app_home);
                    return true;
                case R.id.app_basket:
                    startBasketFragment();
                    addToNavigationHistory(R.id.app_basket);
                    return true;
                case R.id.app_offers:
                    startOfferFragment();
                    addToNavigationHistory(R.id.app_offers);
                    return true;
                case R.id.app_account:
                    startAccountFragment();
                    addToNavigationHistory(R.id.app_account);
                    return true;
            }
            return false;
        });
    }

    private void addToNavigationHistory(int fragmentId) {
        if (!navigationHistory.isEmpty() && navigationHistory.peek() != fragmentId)
            navigationHistory.push(fragmentId);
    }

    private void startAccountFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new AccountFragment(), getString(R.string.account));
        transaction.commit();
    }

    private void startBasketFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, BasketFragment.newInstance(this, this), getString(R.string.basket));
        transaction.commit();
    }

    private void startOfferFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        offersFragment = OffersFragment.newInstance(position);
        transaction.replace(R.id.container, offersFragment, getString(R.string.offer));
        transaction.commit();
    }


    private void startHomeFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        restaurantHomeFragment = RestaurantHomeFragment.newInstance(this, position);
        transaction.replace(R.id.container, restaurantHomeFragment, getString(R.string.home));
        transaction.commit();
    }

    @Override
    public void dialogDismissed() {
        startHomeFragment();
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
        if (currentFragmentId == R.id.app_home) {
            count++;
        } else {
            startHomeFragment();
            currentFragmentId = R.id.app_home;
            navigationHistory.pop();
            navigationHistory.push(R.id.app_home);
            if (!navigationHistory.isEmpty()) {
                bottomNavigationView.getMenu().findItem(navigationHistory.get(navigationHistory.size() - 1)).setChecked(true);
            }
            count = 0;
        }

        if (count > 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Do you want to exit?");
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (currentFragmentId == R.id.app_home) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((RelativeLayout) findViewById(R.id.restaurant_relative_view)).removeAllViews();
            } else {
                ((RelativeLayout) findViewById(R.id.restaurant_relative_view_landscape)).removeAllViews();
            }
            position = restaurantHomeFragment.restaurantAdapter.position;
            startHomeFragment();
        } else if (currentFragmentId == R.id.app_offers) {
            position = offersFragment.offerViewList.getLastVisiblePosition();
        }
    }

    @Override
    public void onChange() {
        updateBasketCount();
    }
}
