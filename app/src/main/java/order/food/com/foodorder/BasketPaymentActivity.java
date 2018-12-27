package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BasketPaymentActivity extends AppCompatActivity {
    String paymentShouldBeVia, paidVia, couponCode;
    double grandTotal, couponPrice;
    TextView cardView, walletView, codView, pay_view, proceedView;
    ListView walletList, savedCardList;

    Context context;

    boolean isNotAllowedToMoveBack = false;

    TextInputEditText cardHolderNameTxt, cardNumberTxt, expiryMonthTxt, expiryYearTxt, cvvTxt;

    WalletAdapter walletAdapter;
    SavedCardAdapter savedCardAdapter;

    RelativeLayout cardLayout;

    ContentValues contentValues = new ContentValues();

    SavedCardDatabaseAdapter savedCardDatabaseAdapter;
    CouponHistoryDatabaseAdapter couponHistoryDatabaseAdapter;
    OrdersDatabaseAdapter ordersDatabaseAdapter;
    OrderItemDatabaseAdapter orderItemDatabaseAdapter;


    int[] walletImagesId = new int[]{R.drawable.freecharge_logo, R.drawable.paytm_logo, R.drawable.phonepe_logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_payment);

        context = this;
        savedCardDatabaseAdapter = new SavedCardDatabaseAdapter(this);
        couponHistoryDatabaseAdapter = new CouponHistoryDatabaseAdapter(this);
        ordersDatabaseAdapter = new OrdersDatabaseAdapter(this);
        orderItemDatabaseAdapter = new OrderItemDatabaseAdapter(this);

        initializeValuesFromIntent();
        initializeToolBar();
        initializeView();
        initializeViewListener();
        pay_view.setText("PAY  ₹ " + grandTotal);
    }

    private void initializeValuesFromIntent() {
        paymentShouldBeVia = getIntent().getStringExtra("paidVia");
        couponCode = getIntent().getStringExtra("couponCode");
        grandTotal = getIntent().getDoubleExtra("grandTotal", 0);
        couponPrice = getIntent().getDoubleExtra("couponDiscountPrice", 0);
    }


    private void initializeViewListener() {
        cardView.setOnClickListener(v -> {
            if (cardLayout.getVisibility() == View.GONE) {
                savedCardList.setVisibility(View.VISIBLE);
                cardLayout.setVisibility(View.VISIBLE);
                makeViewExpand(cardView);
                return;
            }
            savedCardList.setVisibility(View.GONE);
            cardLayout.setVisibility(View.GONE);
            makeViewClose(cardView);
        });

        walletView.setOnClickListener(v -> {
            if (walletList.getVisibility() == View.GONE) {
                walletList.setVisibility(View.VISIBLE);
                makeViewExpand(walletView);
                return;
            }
            walletList.setVisibility(View.GONE);
            makeViewClose(walletView);
        });

        codView.setOnClickListener(v -> {
            paidVia = "COD";
            makePayment();
        });

    }

    private void initializeView() {
        pay_view = findViewById(R.id.pay_message);
        cardView = findViewById(R.id.payment_card);
        walletView = findViewById(R.id.payment_wallet);
        codView = findViewById(R.id.payment_cod);
        cardLayout = findViewById(R.id.card_details_layout);
        walletList = findViewById(R.id.payment_wallet_list);
        walletAdapter = new WalletAdapter();
        walletList.setAdapter(walletAdapter);
        walletList.setVisibility(View.VISIBLE);

        BasketFragment.setListViewHeightBasedOnChildren(walletAdapter, walletList);

        savedCardList = findViewById(R.id.payment_saved_card_list);
        Cursor c = savedCardDatabaseAdapter.getSavedCards(HomeActivity.userId);
        savedCardAdapter = new SavedCardAdapter(c);
        savedCardList.setAdapter(savedCardAdapter);

        savedCardList.setVisibility(View.VISIBLE);
        BasketFragment.setListViewHeightBasedOnChildren(savedCardAdapter, savedCardList);

        initializeCardDetailsAndListeners();
        initializeCardValidation();
        if (paymentShouldBeVia == null || paymentShouldBeVia.isEmpty() || paymentShouldBeVia.equals("CARD")) {
            walletList.setVisibility(View.GONE);
            makeViewClose(walletView);
            return;
        } else {
            savedCardList.setVisibility(View.GONE);
            cardLayout.setVisibility(View.GONE);
            makeViewClose(cardView);
            walletList.setVisibility(View.VISIBLE);
            makeViewExpand(walletView);
            return;
        }
    }

    private String getPaymentNameViaId(int id) {
        switch (id) {
            case R.drawable.paytm_logo:
                return "PAYTM";
            case R.drawable.phonepe_logo:
                return "PHONEPE";
            case R.drawable.freecharge_logo:
                return "FREECHARGE";
            default:
                return "";
        }
    }

    private void initializeCardDetailsAndListeners() {

        cardHolderNameTxt = findViewById(R.id.card_holder_name_txt);
        cardNumberTxt = findViewById(R.id.card_number_txt);
        expiryMonthTxt = findViewById(R.id.card_expiry_month_txt);
        expiryYearTxt = findViewById(R.id.card_year_txt);
        cvvTxt = findViewById(R.id.card_cvv_number_txt);

        proceedView = findViewById(R.id.complete_payment);
        proceedView.setOnClickListener(v -> {
            if (validateCardHolderName() && validateCardNumber() && validateExpiryMonth() && validateExpiryYear() && validateCVV()) {
                paidVia = "CARD";
                makePayment();
            }
        });
    }

    private void initializeCardValidation() {
        cardHolderNameTxt.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                validateCardHolderName();
            }
        });
        cardNumberTxt.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                validateCardNumber();
            }
        });
        expiryMonthTxt.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                validateExpiryMonth();
            }
        });
        expiryYearTxt.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                validateExpiryYear();
            }
        });
        cvvTxt.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                validateCVV();
            }
        });
    }

    private boolean validateCVV() {
        if (cvvTxt.getText().toString().isEmpty()) {
            cvvTxt.setError("Card - cvv should not be empty!!");
            return false;
        }
        if (cvvTxt.getText().toString().length() != 3) {
            cvvTxt.setError("Card - cvv should be 3 digits!!");
            return false;
        }
        return true;
    }

    private boolean validateExpiryYear() {
        if (expiryYearTxt.getText().toString().isEmpty()) {
            expiryYearTxt.setError("Card - expiry year should not be empty!!");
            return false;
        }
        if (expiryYearTxt.getText().toString().length() != 4) {
            expiryYearTxt.setError("Card - expiry year should be  4 digits!!");
            return false;
        }
        return true;
    }

    private boolean validateExpiryMonth() {
        if (expiryMonthTxt.getText().toString().isEmpty()) {
            expiryMonthTxt.setError("Card - expiry month should not be empty!!");
            return false;
        }
        if (expiryMonthTxt.getText().toString().length() > 2) {
            expiryMonthTxt.setError("Card - expiry month should be less than 2 digits!!");
            return false;
        }
        return true;
    }

    private boolean validateCardNumber() {
        if (cardNumberTxt.getText().toString().isEmpty()) {
            cardNumberTxt.setError("Card number should not be empty!!");
            return false;
        }
        if (cardNumberTxt.getText().toString().length() != 16) {
            cardNumberTxt.setError("Card number should be of 16 digits!!");
            return false;
        }
        return true;
    }

    private boolean validateCardHolderName() {
        if (cardHolderNameTxt.getText().toString().isEmpty()) {
            cardHolderNameTxt.setError("Card holder name should not be empty!!");
            return false;
        }
        return true;
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.payment_toolbar);
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
        if (!isNotAllowedToMoveBack) {
            super.onBackPressed();
        } else {
            Toast.makeText(context, "Payment is in process. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeViewExpand(TextView v) {
        v.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
    }

    private void makeViewClose(TextView v) {
        v.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
    }

    class WalletAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return walletImagesId.length;
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
            ImageView walletImage;
            convertView = getLayoutInflater().inflate(R.layout.wallet_view, null);
            walletImage = convertView.findViewById(R.id.wallet_logo);
            walletImage.setImageResource(walletImagesId[position]);
            convertView.setOnClickListener(v -> {
                paidVia = getPaymentNameViaId(walletImagesId[position]);
                makePayment();
            });
            return convertView;
        }
    }

    class SavedCardAdapter extends BaseAdapter {

        Cursor cursor;

        ImageView prevApplyCardImg, prevCancelImg;


        public SavedCardAdapter(Cursor c) {
            cursor = c;
        }


        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.ID));
        }

        @Override
        public long getItemId(int position) {
            return R.id.card_no_txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cardNoTextView;
            String cardHolderName, cardNumber, expiryMonth, expiryYear;
            ImageView applyCardImg, cancelImg;

            convertView = getLayoutInflater().inflate(R.layout.saved_card_view, null);
            cardNoTextView = convertView.findViewById(R.id.card_no_txt);
            applyCardImg = convertView.findViewById(R.id.delete_card_img);
            cancelImg = convertView.findViewById(R.id.cancel_card_img);
            applyCardImg.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);


            cursor.moveToPosition(position);
            cardHolderName = cursor.getString(cursor.getColumnIndexOrThrow(SavedCardDatabaseAdapter.CARDHOLDERNAME));
            cardNumber = cursor.getString(cursor.getColumnIndexOrThrow(SavedCardDatabaseAdapter.CARDNUMBER));
            expiryMonth = cursor.getString(cursor.getColumnIndexOrThrow(SavedCardDatabaseAdapter.EXPIRYMONTH));
            expiryYear = cursor.getString(cursor.getColumnIndexOrThrow(SavedCardDatabaseAdapter.EXPIRYYEAR));

            cardNoTextView.setText(cardNumber);


            applyCardImg.setOnClickListener(v -> {
                cardHolderNameTxt.setText(cardHolderName);
                cardNumberTxt.setText(cardNumber);
                expiryMonthTxt.setText(expiryMonth);
                expiryYearTxt.setText(expiryYear);
                applyCardImg.setVisibility(View.GONE);
                cancelImg.setVisibility(View.VISIBLE);
                if (prevApplyCardImg != null && prevCancelImg != null && prevApplyCardImg != applyCardImg && prevCancelImg != cancelImg) {
                    prevApplyCardImg.setVisibility(View.VISIBLE);
                    prevCancelImg.setVisibility(View.GONE);
                }
                prevApplyCardImg = applyCardImg;
                prevCancelImg = cancelImg;
            });

            cancelImg.setOnClickListener(v -> {
                cardHolderNameTxt.setText("");
                cardNumberTxt.setText("");
                expiryMonthTxt.setText("");
                expiryYearTxt.setText("");
                applyCardImg.setVisibility(View.VISIBLE);
                cancelImg.setVisibility(View.GONE);
            });

            return convertView;
        }
    }

    public void makePayment() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = "Are you sure you want to pay ₹ " + grandTotal + "\nvia : " + paidVia;
        if (paymentShouldBeVia != null && !paymentShouldBeVia.isEmpty() && !paidVia.equals(paymentShouldBeVia)) {
            message = "Coupon code " + couponCode + " will be removed and coupon price of ₹ "
                    + couponPrice + " will not be consider for payment via " + paidVia + ". Are you sure you want to pay ₹ " +
                    Math.round((grandTotal + couponPrice) * 100.0) / 100.0 + " through " + paidVia;
            pay_view.setText("Pay ₹ " + Math.round((grandTotal + couponPrice) * 100.0) / 100.0);
        }
        dialog.setMessage(message);
        dialog.setPositiveButton("Pay ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                if (paymentShouldBeVia != null && !paymentShouldBeVia.isEmpty() && !paidVia.equals(paymentShouldBeVia)) {
                    pay_view.setText("Pay ₹ " + Math.round((grandTotal + couponPrice) * 100.0) / 100.0);
                    grandTotal = Math.round((grandTotal + couponPrice) * 100.0) / 100.0;
                    getIntent().putExtra("grandTotal", grandTotal);

                    getIntent().putExtra("couponCode", "");
                    getIntent().putExtra("couponDiscountPrice", 0.0);

                }
                startPayment();
            }
        });
        dialog.setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
            pay_view.setText("Pay ₹ " + grandTotal);
            paramDialogInterface.dismiss();
        });
        dialog.show();
    }

    private void startPayment() {
        savedCardList.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        walletList.setVisibility(View.GONE);
        makeViewClose(cardView);
        makeViewClose(walletView);
        cardView.setEnabled(false);
        walletView.setEnabled(false);
        codView.setEnabled(false);
        pay_view.setText("Payment processing for   ₹ " + grandTotal);
        ProgressBar progressBar = findViewById(R.id.payment_progress_bar);
        isNotAllowedToMoveBack = true;
        PayAsync payAsyn = new PayAsync(progressBar);
        payAsyn.start();
    }

    class PayAsync extends Thread {
        ProgressBar progressBar;

        PayAsync(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                this.sleep(2000);
                progressBar.setProgress(2, true);
                this.sleep(2000);
                progressBar.setProgress(3, true);
                pay_view.setText("Payment done for   ₹ " + grandTotal);
                String couponCode = getIntent().getStringExtra("couponCode");

                addCouponHistoryIfCouponUsed(couponCode);
                couponHistoryDatabaseAdapter.close();
                addSavedCardsIfUsed();
                savedCardDatabaseAdapter.close();

                setContentValuesForOrderEntry();
                long orderId = ordersDatabaseAdapter.insert(contentValues);

                addOrderItems(orderId);
                ordersDatabaseAdapter.close();
                orderItemDatabaseAdapter.close();

                RestaurantBasketDetails.clearBasket(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));

                runOnUiThread(() -> {
                    Toast.makeText(context, "Payment success.", Toast.LENGTH_SHORT).show();
                });

                Intent i = new Intent(context, OrderTrackActivity.class);
                i.putExtra(OrderItemDatabaseAdapter.ORDER_ID, orderId);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                return;
            }
        }

        private void addOrderItems(long orderId) throws JSONException {
            JSONArray itemKeys = RestaurantBasketDetails.getItemObjectKeysNames();
            for (int i = 0; i < itemKeys.length(); i++) {
                contentValues.clear();
                JSONObject jsonItemObject = RestaurantBasketDetails.getItemObject(itemKeys.getString(i));
                contentValues.put(OrderItemDatabaseAdapter.ORDER_ID, orderId);
                contentValues.put(OrderItemDatabaseAdapter.ITEM_NAME, jsonItemObject.getString(RestaurantItemsDatabaseAdapter.ITEM_NAME));
                contentValues.put(OrderItemDatabaseAdapter.ITEM_PRICE, jsonItemObject.getDouble(RestaurantItemsDatabaseAdapter.ITEM_PRICE));
                contentValues.put(OrderItemDatabaseAdapter.ITEM_OFFER_PRICE, jsonItemObject.getDouble("offerPrice"));
                contentValues.put(OrderItemDatabaseAdapter.ITEM_QUANTITY, jsonItemObject.getInt("itemQuantity"));
                contentValues.put(OrderItemDatabaseAdapter.ITEM_IS_VEG, jsonItemObject.getBoolean(RestaurantItemsDatabaseAdapter.IS_VEG) ? 0 : 1);
                orderItemDatabaseAdapter.insertOrderItems(contentValues);
            }
        }

        private void setContentValuesForOrderEntry() {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            contentValues.clear();
            contentValues.put(OrdersDatabaseAdapter.USERID, HomeActivity.userId);
            contentValues.put(OrdersDatabaseAdapter.RESTAURANT_ID, RestaurantBasketDetails.getRestaurantId());
            contentValues.put(OrdersDatabaseAdapter.RESTAURANT_NAME, RestaurantBasketDetails.getRestaurantName());
            contentValues.put(OrdersDatabaseAdapter.DATE, formatter.format(new Date()));
            contentValues.put(OrdersDatabaseAdapter.DELIVERY_ADDRESS, getIntent().getStringExtra(OrdersDatabaseAdapter.DELIVERY_ADDRESS));
            contentValues.put(OrdersDatabaseAdapter.GST_CHARGES, getIntent().getDoubleExtra("gstChargesPrice", 0.0));
            contentValues.put(OrdersDatabaseAdapter.PACKING_CHARGES, RestaurantBasketDetails.getRestaurantPackingCharges());
            contentValues.put(OrdersDatabaseAdapter.TOTAL_PRICE, getIntent().getDoubleExtra("total", 0.0));
            contentValues.put(OrdersDatabaseAdapter.SUB_TOTAL_PRICE, getIntent().getDoubleExtra("subTotal", 0.0));
            contentValues.put(OrdersDatabaseAdapter.GRAND_TOTAL_PRICE, getIntent().getDoubleExtra("grandTotal", 0.0));
            contentValues.put(OrdersDatabaseAdapter.COUPON_CODE, getIntent().getStringExtra("couponCode"));
            contentValues.put(OrdersDatabaseAdapter.COUPON_DISCOUNT_PRICE, getIntent().getDoubleExtra("couponDiscountPrice", 0.0));
            contentValues.put(OrdersDatabaseAdapter.RESTAURANT_OFFER_PERCENTAGE, RestaurantBasketDetails.getRestaurantOfferPercentage());
            contentValues.put(OrdersDatabaseAdapter.RESTAURANT_OFFER_PRICE, getIntent().getDoubleExtra("restaurantOfferPrice", 0.0));
            contentValues.put(OrdersDatabaseAdapter.ORDER_STATUS, OrderTrackActivity.ORDER_PLACED);
            contentValues.put(OrdersDatabaseAdapter.ORDER_MESSAGE, OrderTrackActivity.orderStatusMessageMap.get(OrderTrackActivity.ORDER_PLACED));
            contentValues.put(OrdersDatabaseAdapter.PAID_VIA, paidVia);
            contentValues.put(OrdersDatabaseAdapter.REVIEW, 0);
        }

        private void addSavedCardsIfUsed() {
            if (paidVia.equals("CARD")) {
                contentValues.clear();
                contentValues.put(SavedCardDatabaseAdapter.USERID, HomeActivity.userId);
                contentValues.put(SavedCardDatabaseAdapter.CARDHOLDERNAME, cardHolderNameTxt.getText().toString());
                contentValues.put(SavedCardDatabaseAdapter.CARDNUMBER, cardNumberTxt.getText().toString());
                contentValues.put(SavedCardDatabaseAdapter.EXPIRYMONTH, Integer.parseInt(expiryMonthTxt.getText().toString()));
                contentValues.put(SavedCardDatabaseAdapter.EXPIRYYEAR, Integer.parseInt(expiryYearTxt.getText().toString()));
                savedCardDatabaseAdapter.insert(contentValues);
            }
        }

        private void addCouponHistoryIfCouponUsed(String couponCode) {
            if (!couponCode.isEmpty()) {
                contentValues.clear();
                contentValues.put(CouponHistoryDatabaseAdapter.USERID, HomeActivity.userId);
                contentValues.put(CouponHistoryDatabaseAdapter.OFFERID, getIntent().getIntExtra(CouponHistoryDatabaseAdapter.OFFERID, 0));
                contentValues.put(CouponHistoryDatabaseAdapter.COUNT, 1);
                Cursor couponHistoryCursor = couponHistoryDatabaseAdapter.getCouponHistoryByUserIdAndOfferId(
                        HomeActivity.userId, getIntent().getIntExtra(CouponHistoryDatabaseAdapter.OFFERID, 0));
                if (couponHistoryCursor.getCount() == 0) {
                    couponHistoryDatabaseAdapter.insert(contentValues);
                } else {
                    contentValues.clear();
                    couponHistoryCursor.moveToFirst();
                    contentValues.put(CouponHistoryDatabaseAdapter.COUNT,
                            couponHistoryCursor.getInt(couponHistoryCursor.getColumnIndexOrThrow(CouponHistoryDatabaseAdapter.COUNT)) + 1);
                    couponHistoryDatabaseAdapter.update(contentValues, HomeActivity.userId, getIntent().getIntExtra(CouponHistoryDatabaseAdapter.OFFERID, 0));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
