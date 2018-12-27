package order.food.com.foodorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MyOrdersActivity extends AppCompatActivity {

    OrdersDatabaseAdapter ordersDatabaseAdapter;
    OrderItemDatabaseAdapter orderItemDatabaseAdapter;
    RestaurantDatabaseAdapter restaurantDatabaseAdapter;

    ListView myOrderList;
    MyOrderAdapter myOrderAdapter;
    ScrollView myOrderScrollView;

    int restaurantId;
    int review;
    int orderId;

    TextView hotelName, deliveryAddressLocation, subTotal, offerPercentage, packingCharges, tax, total, couponOffer, grandTotal;
    TextView offerPercentageLabel, totalLabel, couponOfferLabel, reorderView, submitRatingTxt, messageTxt;
    ListView orderItemList;
    ImageView img_star1, img_star2, img_star3, img_star4, img_star5;

    MyOrderItemAdapter myOrderItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        initializeToolBar();

        ordersDatabaseAdapter = new OrdersDatabaseAdapter(this);
        orderItemDatabaseAdapter = new OrderItemDatabaseAdapter(this);
        restaurantDatabaseAdapter = new RestaurantDatabaseAdapter(this);

        initializeOrderElements();

        Cursor c = ordersDatabaseAdapter.getOrdersByUserId(HomeActivity.userId);
        if (c.getCount() == 0) {
            messageTxt.setVisibility(View.VISIBLE);
        } else {
            messageTxt.setVisibility(View.GONE);
        }
        myOrderAdapter = new MyOrderAdapter(c);
        myOrderList.setAdapter(myOrderAdapter);

    }


    @NonNull
    private View.OnClickListener getOnClickListener(int no) {
        return v -> {
            review = no;
            submitRatingTxt.setVisibility(View.VISIBLE);
            setRatingBasedOnNumber(no);
        };
    }

    private void setRatingBasedOnNumber(int no) {
        switch (no) {
            case 5:
                img_star5.setImageResource(R.drawable.ic_star_black_24dp);
            case 4:
                img_star4.setImageResource(R.drawable.ic_star_black_24dp);
            case 3:
                img_star3.setImageResource(R.drawable.ic_star_black_24dp);
            case 2:
                img_star2.setImageResource(R.drawable.ic_star_black_24dp);
            case 1:
                img_star1.setImageResource(R.drawable.ic_star_black_24dp);
                break;
        }

        switch (no + 1) {
            case 2:
                img_star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            case 3:
                img_star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            case 4:
                img_star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            case 5:
                img_star5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
        }
    }

    private void initializeOrderElements() {
        hotelName = findViewById(R.id.hotelName);
        reorderView = findViewById(R.id.reorderItems);
        deliveryAddressLocation = findViewById(R.id.deliveryAddressLocation);
        subTotal = findViewById(R.id.subTotal);
        offerPercentage = findViewById(R.id.offerPercentage);
        packingCharges = findViewById(R.id.packingCharges);
        tax = findViewById(R.id.tax);
        total = findViewById(R.id.total);
        couponOffer = findViewById(R.id.couponOffer);
        grandTotal = findViewById(R.id.grandTotal);
        orderItemList = findViewById(R.id.order_item_content);
        offerPercentageLabel = findViewById(R.id.offerPercentageLabel);
        couponOfferLabel = findViewById(R.id.couponOfferLabel);
        totalLabel = findViewById(R.id.totalLabel);
        img_star1 = findViewById(R.id.rating_start1);
        img_star2 = findViewById(R.id.rating_start2);
        img_star3 = findViewById(R.id.rating_start3);
        img_star4 = findViewById(R.id.rating_start4);
        img_star5 = findViewById(R.id.rating_start5);
        submitRatingTxt = findViewById(R.id.submit_rating);
        messageTxt = findViewById(R.id.my_order_message_txt);

        myOrderScrollView = findViewById(R.id.my_order_scroll);

        myOrderList = findViewById(R.id.my_order_list);
    }


    public void setValuesToOrderElement(Cursor cursor) {
        orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.ID));
        hotelName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_NAME)));
        deliveryAddressLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.DELIVERY_ADDRESS)));
        subTotal.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.SUB_TOTAL_PRICE)));
        double offerPercentageVal = cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_OFFER_PRICE));
        if (offerPercentageVal > 0.0) {
            offerPercentage.setVisibility(View.VISIBLE);
            offerPercentageLabel.setVisibility(View.VISIBLE);
            offerPercentage.setText("₹ " + offerPercentageVal);
            offerPercentageLabel.setText("Hotel offer " + cursor.getInt(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_OFFER_PERCENTAGE)) + "%");
        }
        packingCharges.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.PACKING_CHARGES)));
        tax.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.GST_CHARGES)));
        double couponOfferPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.COUPON_DISCOUNT_PRICE));
        if (couponOfferPrice > 0.0) {
            total.setVisibility(View.VISIBLE);
            totalLabel.setVisibility(View.VISIBLE);
            couponOffer.setVisibility(View.VISIBLE);
            couponOfferLabel.setVisibility(View.VISIBLE);
            total.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.TOTAL_PRICE)));
            couponOffer.setText("₹ " + couponOfferPrice);
            couponOfferLabel.setText("Coupon offer " + cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.COUPON_CODE)));
        }
        grandTotal.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.GRAND_TOTAL_PRICE)));

        review = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.REVIEW));
        if (review > 0) {
            setRatingBasedOnNumber(review);
            submitRatingTxt.setVisibility(View.VISIBLE);
            submitRatingTxt.setText("Submitted");
            submitRatingTxt.setClickable(false);
            img_star1.setClickable(false);
            img_star2.setClickable(false);
            img_star3.setClickable(false);
            img_star4.setClickable(false);
            img_star5.setClickable(false);
        } else {
            initializeOrderElementsListener();
        }

        Cursor c = orderItemDatabaseAdapter.getOrderItemsByOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.ID)));
        myOrderItemAdapter = new MyOrderItemAdapter(c);
        orderItemList.setAdapter(myOrderItemAdapter);
        BasketFragment.setListViewHeightBasedOnChildren(myOrderItemAdapter, orderItemList);

        reorderView.setOnClickListener(v -> {
            if (!RestaurantBasketDetails.isBasketEmpty) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Basket already contains items from other restaurants. Do you want to replace it?");
                dialog.setPositiveButton("Replace it.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        addItemsToTheBasket(c);
                    }
                });
                dialog.setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
                    paramDialogInterface.dismiss();
                });
                dialog.show();
            } else {
                addItemsToTheBasket(c);
            }

        });
    }

    private void initializeOrderElementsListener() {
        submitRatingTxt.setVisibility(View.GONE);
        img_star1.setImageResource(R.drawable.ic_star_border_black_24dp);
        img_star2.setImageResource(R.drawable.ic_star_border_black_24dp);
        img_star3.setImageResource(R.drawable.ic_star_border_black_24dp);
        img_star4.setImageResource(R.drawable.ic_star_border_black_24dp);
        img_star5.setImageResource(R.drawable.ic_star_border_black_24dp);
        submitRatingTxt.setText("Submit");
        img_star1.setOnClickListener(getOnClickListener(1));
        img_star2.setOnClickListener(getOnClickListener(2));
        img_star3.setOnClickListener(getOnClickListener(3));
        img_star4.setOnClickListener(getOnClickListener(4));
        img_star5.setOnClickListener(getOnClickListener(5));

        submitRatingTxt.setOnClickListener(v -> {
            ordersDatabaseAdapter.updateOrderReviewForOrderId(review, orderId);
            Cursor restaurantCursor = restaurantDatabaseAdapter.getRestaurantsById(restaurantId);
            restaurantCursor.moveToFirst();
            int ratingCount = restaurantCursor.getInt(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.RATING_COUNT_FIELD));
            double currentRating = restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OVERALL_RATING_FIELD));
            double newRating = Math.round(((currentRating * ratingCount + review) / (ratingCount + 1)) * 10.0) / 10.0;
            restaurantDatabaseAdapter.updateRestaurantReviewByRestaurantId(restaurantId, ratingCount + 1, newRating);
            Toast.makeText(this, "Review submitted successfully!!", Toast.LENGTH_SHORT).show();
            submitRatingTxt.setClickable(false);
            img_star1.setClickable(false);
            img_star2.setClickable(false);
            img_star3.setClickable(false);
            img_star4.setClickable(false);
            img_star5.setClickable(false);
            submitRatingTxt.setText("Submitted");
            myOrderAdapter.cursor = ordersDatabaseAdapter.getOrdersByUserId(HomeActivity.userId);
            myOrderAdapter.notifyDataSetChanged();
        });
    }

    private void addItemsToTheBasket(Cursor c) {
        RestaurantBasketDetails.clearBasket(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        RestaurantBasketDetails.isBasketEmpty = false;
        RestaurantBasketDetails.addRestaurantId(restaurantId);
        Cursor restaurantCursor = restaurantDatabaseAdapter.getRestaurantsById(restaurantId);
        restaurantCursor.moveToFirst();
        RestaurantBasketDetails.addRestaurantName(restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.NAME_FIELD)));
        String area = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.AREA_FIELD));
        String city = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.CITY_FIELD));
        RestaurantBasketDetails.addRestaurantLocation(city);
        RestaurantBasketDetails.addRestaurantOfferPercentage(restaurantCursor.getInt(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD)));
        RestaurantBasketDetails.addRestaurantMinOfferPrice(restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD)));
        double packingCharges = restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.PACKING_CHARGES_FIELD));
        RestaurantBasketDetails.addRestaurantPackingCharges(packingCharges);

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            String itemName = c.getString(c.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_NAME));
            boolean itemIsVeg = c.getInt(c.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_IS_VEG)) == 0;
            double itemPrice = c.getDouble(c.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_PRICE));
            double offerPrice = c.getDouble(c.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_OFFER_PRICE));
            int quantity = c.getInt(c.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_QUANTITY));
            RestaurantBasketDetails.addNewItemToBasketWithQuantity(itemName, itemIsVeg, itemPrice, offerPrice, quantity);
        }

        Intent homeActivityIntent = new Intent(this.getApplicationContext(), HomeActivity.class);
        homeActivityIntent.putExtra("moveToBasket", true);
        startActivity(homeActivityIntent);
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_orders_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> {
            handleOnBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        handleOnBackPressed();
    }

    private void handleOnBackPressed() {
        if (myOrderList.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            myOrderList.setVisibility(View.VISIBLE);
            myOrderScrollView.setVisibility(View.GONE);
        }
    }

    class MyOrderAdapter extends BaseAdapter {
        Cursor cursor;

        MyOrderAdapter(Cursor c) {
            cursor = c;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            cursor.moveToPosition(position);
            return cursor;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView restaurantName, grandTotal, dateView, paidVia, orderStatus, trackOrder;
            int index;
            convertView = getLayoutInflater().inflate(R.layout.my_order_view, null);

            restaurantName = convertView.findViewById(R.id.restaurant_name_txt);
            grandTotal = convertView.findViewById(R.id.grandTotal);
            dateView = convertView.findViewById(R.id.dateTxt);
            orderStatus = convertView.findViewById(R.id.orderStatusTxt);
            trackOrder = convertView.findViewById(R.id.track_order);
            paidVia = convertView.findViewById(R.id.paidViaTxt);
            cursor.moveToPosition(position);
            index = position;
            restaurantName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_NAME)));
            grandTotal.setText("₹ " + cursor.getDouble(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.GRAND_TOTAL_PRICE)));
            dateView.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.DATE)));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.ORDER_STATUS));
            orderStatus.setText(status);
            if (!status.equals(OrderTrackActivity.ORDER_RECEIVED)) {
                trackOrder.setVisibility(View.VISIBLE);
            } else {
                trackOrder.setVisibility(View.GONE);
            }
            paidVia.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.PAID_VIA)));

            trackOrder.setOnClickListener(v -> {
                cursor.moveToPosition(index);
                Intent i = new Intent(getApplicationContext(), OrderTrackActivity.class);
                i.putExtra(OrderItemDatabaseAdapter.ORDER_ID, cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstant.ID)));
                i.putExtra("isFromMyOrderPage", true);
                startActivity(i);
            });

            convertView.setOnClickListener(v -> {
                if (myOrderList.getVisibility() == View.VISIBLE) {
                    myOrderScrollView.setVisibility(View.VISIBLE);
                    myOrderList.setVisibility(View.GONE);
                    cursor.moveToPosition(index);
                    setValuesToOrderElement(cursor);
                    restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersDatabaseAdapter.RESTAURANT_ID));
                }
            });
            return convertView;
        }
    }

    class MyOrderItemAdapter extends BaseAdapter {
        Cursor cursor;
        ImageView vegStatusImage;
        TextView orderItemName, orderItemPrice, orderItemDiscountPrice, orderItemQuantity, orderItemQuantitySum;

        MyOrderItemAdapter(Cursor c) {
            cursor = c;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.my_order_item_view, null);
            initializeViews(convertView);
            cursor.moveToPosition(position);
            addValuesToViews(cursor);
            return convertView;
        }

        private void addValuesToViews(Cursor cursor) {
            if (cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_IS_VEG)) == 0) {
                vegStatusImage.setImageResource(R.drawable.veg_logo);
            }
            orderItemName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_NAME)));
            orderItemName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_NAME)));
            double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_PRICE));
            orderItemPrice.setText("₹ " + itemPrice);
            double discountPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_OFFER_PRICE));
            if (discountPrice > 0.0) {
                orderItemDiscountPrice.setVisibility(View.VISIBLE);
                orderItemPrice.setPaintFlags(orderItemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                orderItemDiscountPrice.setText("₹ " + discountPrice);
            } else {
                discountPrice = itemPrice;
            }
            int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemDatabaseAdapter.ITEM_QUANTITY));
            orderItemQuantity.setText("" + itemQuantity);
            orderItemQuantitySum.setText("" + Math.round(itemQuantity * discountPrice * 100.0) / 100.0);
        }

        private void initializeViews(View convertView) {
            vegStatusImage = convertView.findViewById(R.id.order_item_veg_status);
            orderItemName = convertView.findViewById(R.id.order_item_name);
            orderItemPrice = convertView.findViewById(R.id.order_item_price);
            orderItemDiscountPrice = convertView.findViewById(R.id.order_item_discount_price);
            orderItemQuantity = convertView.findViewById(R.id.order_item_quantity);
            orderItemQuantitySum = convertView.findViewById(R.id.order_item_quantity_sum);
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        restaurantDatabaseAdapter.close();
        ordersDatabaseAdapter.close();
        orderItemDatabaseAdapter.close();
        super.onDestroy();
    }

}
