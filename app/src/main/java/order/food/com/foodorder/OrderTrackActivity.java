package order.food.com.foodorder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderTrackActivity extends AppCompatActivity {

    public static String ORDER_PLACED = "Order Placed";
    public static String ORDER_CONFIRMED = "Order Confirmed";
    public static String ORDER_PROCESSING = "Order Processing";
    public static String ORDER_DISPATCHED = "Order Dispatched";
    public static String ORDER_RECEIVED = "Order Received";

    public static Map<String, String> orderStatusMessageMap = new HashMap<>();

    public static List<String> orderStatusList = new ArrayList<>();

    static {
        orderStatusList.add(ORDER_PLACED);
        orderStatusList.add(ORDER_CONFIRMED);
        orderStatusList.add(ORDER_PROCESSING);
        orderStatusList.add(ORDER_DISPATCHED);
        orderStatusList.add(ORDER_RECEIVED);
        orderStatusMessageMap.put(ORDER_PLACED, "Your order has been placed to the restaurant.");
        orderStatusMessageMap.put(ORDER_CONFIRMED, "Your order is confirmed by restaurant and being prepared.");
        orderStatusMessageMap.put(ORDER_PROCESSING, "Worked has arrived to pick up your order.");
        orderStatusMessageMap.put(ORDER_DISPATCHED, "Worker has picked up your order and food is on the way to you.");
        orderStatusMessageMap.put(ORDER_RECEIVED, "Food received by you. Thank you.");

    }

    Cursor orderCursor;

    OrdersDatabaseAdapter ordersDatabaseAdapter;

    long orderId;

    TextView hotelName, orderTotalPrice, orderStatus, orderStatusMessage, viewOrder;
    ProgressBar orderProgressBar;
    boolean isFromMyOrderPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        orderId = getIntent().getLongExtra(OrderItemDatabaseAdapter.ORDER_ID, 0);
        isFromMyOrderPage = getIntent().getBooleanExtra("isFromMyOrderPage", false);
        ordersDatabaseAdapter = new OrdersDatabaseAdapter(this);
        orderCursor = ordersDatabaseAdapter.getOrderByOrderIdAndUserId(HomeActivity.userId, orderId);
        initializeToolBar();
        initializeViewsAndListener();
        setValuesToViews(orderCursor);

    }

    private void setValuesToViews(Cursor orderCursor) {
        orderCursor.moveToFirst();
        hotelName.setText(orderCursor.getString(orderCursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_NAME)));
        String orderStatusStr = orderCursor.getString(orderCursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.ORDER_STATUS));
        orderStatus.setText(orderStatusStr);
        orderTotalPrice.setText("₹ " + orderCursor.getDouble(orderCursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.GRAND_TOTAL_PRICE)));
        orderStatusMessage.setText(orderCursor.getString(orderCursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.ORDER_MESSAGE)));
        orderProgressBar.setProgress(orderStatusList.indexOf(orderStatusStr) + 1);
        TrackOrderAsync trackOrderAsync = new TrackOrderAsync(orderStatusStr, this);
        trackOrderAsync.start();
    }


    private void initializeViewsAndListener() {
        hotelName = findViewById(R.id.orderHotelName);
        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        orderStatus = findViewById(R.id.orderStatus);
        orderStatusMessage = findViewById(R.id.orderStatusMessage);
        orderProgressBar = findViewById(R.id.orderProgressBar);
        viewOrder = findViewById(R.id.view_order);
        viewOrder.setOnClickListener(v -> {
            goToHomeActivity();
        });
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.order_track_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> {
            handleBackPressed();
        });
    }

    private void handleBackPressed() {
        if (isFromMyOrderPage) {
            Intent i = new Intent(this, MyOrdersActivity.class);
            startActivity(i);
            return;
        }
        goToHomeActivity();
    }

    private void goToHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }

    class TrackOrderAsync extends Thread {
        Context context;
        String orderStatusTxt;

        TrackOrderAsync(String orderStatus, Context c) {
            this.orderStatusTxt = orderStatus;
            context = c;
        }

        @Override
        public void run() {
            try {
                int orderPosition = orderStatusList.indexOf(orderStatusTxt);
                for (int i = orderPosition + 1; i < orderStatusList.size(); i++) {

                    String nxtStatus = orderStatusList.get(i);
                    int finalPosition = i + 1;
                    runOnUiThread(() -> {
                        orderStatus.setText(nxtStatus);
                        orderStatusMessage.setText(orderStatusMessageMap.get(nxtStatus));
                        orderProgressBar.setProgress(finalPosition, true);
                    });
                    ordersDatabaseAdapter.updateOrderStatusByOrderId(nxtStatus, HomeActivity.userId, orderId);
                    this.sleep(10000);
                }
                ordersDatabaseAdapter.close();
                runOnUiThread(() -> {
                    Toast.makeText(context, "Order received.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
