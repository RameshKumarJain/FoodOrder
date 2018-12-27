package order.food.com.foodorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class BasketFragment extends Fragment {

    public static final int SELECT_OFFER_REQUEST_CODE = 1;

    CouponHistoryDatabaseAdapter couponHistoryDatabaseAdapter;

    OfferDatabaseAdapter offerDatabaseAdapter;

    OnItemModifyListener onItemModifyListener;

    TextView hotelName, editItems, deliveryAddressLocation, subTotal, packingCharges, tax, total, pay_txt;
    TextView offerPercentageLabel, offerPercentage, couponOfferLabel, couponOffer, grandTotal, totalLabel;
    EditText offer_code, deliveryAddress;
    TextView apply_offer, select_offer, applied_offer_txt;
    Button cancel_applied_offer_btn;

    LinearLayout selectOfferLayout, appliedOfferLayout;

    int offerId = -1;

    ListView item_content_list;
    BasketItemAdapter basketItemAdapter;
    SharedPreferences contextSharedPreferences;

    double offerDiscount = 0.0, packingChargePrice = 0.0, gstCharges = 0.0, subTotalPrice = 0.0, totalPrice = 0.0, couponOfferPrice = 0.0, grandTotalPrice = 0.0;
    String offerShdPayVia;

    Context context;
    View basketView;
    private FragmentTransaction transaction;

    public static BasketFragment newInstance(Context c, OnItemModifyListener onItemModifyListener) {
        BasketFragment basketFragment = new BasketFragment();
        basketFragment.context = c;
        basketFragment.onItemModifyListener = onItemModifyListener;
        return basketFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (RestaurantBasketDetails.getItemCount() == 0) {
            basketView = inflater.inflate(R.layout.empty_basket_fragment, null);
            return basketView;
        }

        basketView = inflater.inflate(R.layout.fragment_basket, null);

        couponHistoryDatabaseAdapter = new CouponHistoryDatabaseAdapter(context);
        offerDatabaseAdapter = new OfferDatabaseAdapter(context);

        initializeBasketElements(basketView);
        initializeBasketListeners();
        setValuesForTheView();

        basketItemAdapter = new BasketItemAdapter(onItemModifyListener);
        item_content_list.setAdapter(basketItemAdapter);
        setListViewHeightBasedOnChildren(basketItemAdapter, item_content_list);


        return basketView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_OFFER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                offer_code.setText(result);
                apply_offer.callOnClick();
            }
        }
    }

    private void initializeBasketListeners() {
        cancel_applied_offer_btn.setOnClickListener((View v) -> {
            selectOfferLayout.setVisibility(View.VISIBLE);
            appliedOfferLayout.setVisibility(View.INVISIBLE);
            offer_code.setText("");
            couponOffer.setText("");
            couponOffer.setVisibility(View.GONE);
            couponOfferLabel.setVisibility(View.GONE);

            totalLabel.setVisibility(View.GONE);
            total.setVisibility(View.GONE);

            grandTotalPrice = totalPrice;
            grandTotal.setText("" + grandTotalPrice);
            pay_txt.setText("Pay  ₹ " + grandTotalPrice);

            offerShdPayVia = "";

        });

        editItems.setOnClickListener(v -> {
            Intent restaurantItemIntent = new Intent(v.getContext(), RestaurantItemsActivity.class);
            restaurantItemIntent.putExtra(RestaurantItemsDatabaseAdapter.RESTAURANT_ID, RestaurantBasketDetails.getRestaurantId());
            restaurantItemIntent.putExtra("fromBasketPage", true);
            v.getContext().startActivity(restaurantItemIntent);
        });

        pay_txt.setOnClickListener(v -> {
            if (deliveryAddress.getText().toString().isEmpty()) {
                deliveryAddress.setError("Flat/Street name cannot be empty!!");
                deliveryAddress.requestFocus();
                return;
            }
            Intent paymentIntent = new Intent(context, BasketPaymentActivity.class);
            paymentIntent.putExtra("subTotal", subTotalPrice);
            paymentIntent.putExtra("total", totalPrice);
            paymentIntent.putExtra("grandTotal", grandTotalPrice);
            paymentIntent.putExtra("deliveryAddress", deliveryAddress.getText().toString() + ", " + deliveryAddressLocation.getText().toString());
            paymentIntent.putExtra("paidVia", offerShdPayVia);
            paymentIntent.putExtra("offerId", offerId);
            paymentIntent.putExtra("couponCode", applied_offer_txt.getText().toString());
            paymentIntent.putExtra("couponDiscountPrice", couponOfferPrice);
            paymentIntent.putExtra("gstChargesPrice", gstCharges);
            paymentIntent.putExtra("restaurantOfferPrice", offerDiscount);
            startActivity(paymentIntent);
        });

        select_offer.setOnClickListener(v -> {
            Intent selectOfferIntent = new Intent(context, SelectLiveOfferActivity.class);
            startActivityForResult(selectOfferIntent, SELECT_OFFER_REQUEST_CODE);
        });

        offer_code.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                offer_code.setError(null);
                offer_code.clearFocus();
            }
        });

        apply_offer.setOnClickListener(v -> {
            String offerCode = offer_code.getText().toString();
            if (offerCode.isEmpty()) {
                offer_code.setError("Enter the offer code.");
                offer_code.requestFocus();
                return;
            }
            Cursor offerCursor = offerDatabaseAdapter.getOffersByCode(offerCode);
            if (offerCursor.getCount() != 1) {
                offer_code.setError("Invalid offer code.");
                offer_code.requestFocus();
                return;
            }
            offerDatabaseAdapter.close();
            offerCursor.moveToFirst();
            Cursor couponHistoryCursor = couponHistoryDatabaseAdapter.getCouponHistoryByUserIdAndOfferId(HomeActivity.userId, offerCursor.getInt(0));
            if (couponHistoryCursor.getCount() != 0) {
                couponHistoryCursor.moveToFirst();
                int maxCount = offerCursor.getInt(offerCursor.getColumnIndexOrThrow(OfferDatabaseAdapter.COUNT_ALLOWED_PER_USER));
                int usedCount = couponHistoryCursor.getInt(couponHistoryCursor.getColumnIndexOrThrow(CouponHistoryDatabaseAdapter.COUNT));
                if (maxCount <= usedCount) {
                    offer_code.setError("Offer code already used.");
                    offer_code.requestFocus();
                    return;
                }
            }
            couponHistoryDatabaseAdapter.close();

            offerShdPayVia = offerCursor.getString(offerCursor.getColumnIndexOrThrow(OfferDatabaseAdapter.PAID_VIA));

            double offerPercentage = offerCursor.getDouble(offerCursor.getColumnIndexOrThrow(OfferDatabaseAdapter.DISCOUNT_PERCENTAGE));
            double offerMaxDiscount = offerCursor.getDouble(offerCursor.getColumnIndexOrThrow(OfferDatabaseAdapter.MAX_DISCOUNT_PRICE));
            couponOfferPrice = Math.round((totalPrice * offerPercentage / 100.0) * 100.0) / 100.0;
            couponOfferPrice = couponOfferPrice >= offerMaxDiscount ? couponOfferPrice = offerMaxDiscount : couponOfferPrice;

            couponOffer.setText("" + couponOfferPrice);
            couponOffer.setVisibility(View.VISIBLE);
            couponOfferLabel.setVisibility(View.VISIBLE);

            totalLabel.setVisibility(View.VISIBLE);
            total.setVisibility(View.VISIBLE);

            grandTotalPrice = Math.round((totalPrice - couponOfferPrice) * 100.0) / 100.0;
            grandTotal.setText("" + grandTotalPrice);
            pay_txt.setText("Pay  ₹ " + grandTotalPrice);
            selectOfferLayout.setVisibility(View.GONE);
            appliedOfferLayout.setVisibility(View.VISIBLE);
            applied_offer_txt.setText(offerCode);
            offerId = offerCursor.getInt(offerCursor.getColumnIndexOrThrow(DatabaseConstant.ID));
            Toast.makeText(context, "Hurrah code applied successfully!!", Toast.LENGTH_SHORT).show();

        });
    }

    public static void setListViewHeightBasedOnChildren(BaseAdapter adapter, ListView listview) {

        if (adapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listview.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, listview);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listview.getLayoutParams();

        params.height = totalHeight + (listview.getDividerHeight() *
                (adapter.getCount() - 1));

        listview.setLayoutParams(params);
        listview.requestLayout();
    }

    private void initializeBasketElements(View basketView) {
        hotelName = basketView.findViewById(R.id.hotelName);
        editItems = basketView.findViewById(R.id.editItems);
        deliveryAddress = basketView.findViewById(R.id.deliveryAddress);
        deliveryAddressLocation = basketView.findViewById(R.id.deliveryAddressLocation);
        subTotal = basketView.findViewById(R.id.subTotal);
        offerPercentageLabel = basketView.findViewById(R.id.offerPercentageLabel);
        offerPercentage = basketView.findViewById(R.id.offerPercentage);
        packingCharges = basketView.findViewById(R.id.packingCharges);
        tax = basketView.findViewById(R.id.tax);
        total = basketView.findViewById(R.id.total);
        totalLabel = basketView.findViewById(R.id.totalLabel);
        couponOffer = basketView.findViewById(R.id.couponOffer);
        couponOfferLabel = basketView.findViewById(R.id.couponOfferLabel);
        grandTotal = basketView.findViewById(R.id.grandTotal);

        apply_offer = basketView.findViewById(R.id.apply_offer);
        pay_txt = basketView.findViewById(R.id.pay_txt);
        offer_code = basketView.findViewById(R.id.offer_code);
        select_offer = basketView.findViewById(R.id.select_offer);
        item_content_list = basketView.findViewById(R.id.item_content);
        applied_offer_txt = basketView.findViewById(R.id.applied_offer_label);
        cancel_applied_offer_btn = basketView.findViewById(R.id.cancel_offer_btn);

        appliedOfferLayout = basketView.findViewById(R.id.applied_offer_layout);
        selectOfferLayout = basketView.findViewById(R.id.select_offer_layout);
    }

    private void setValuesForTheView() {
        contextSharedPreferences = context.getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        deliveryAddressLocation.setText(RestaurantBasketDetails.getRestaurantLocation());
        hotelName.setText(RestaurantBasketDetails.getRestaurantName());
        subTotalPrice = RestaurantBasketDetails.total;
        subTotal.setText(String.valueOf(subTotalPrice));
        int offer = RestaurantBasketDetails.getRestaurantOfferPercentage();
        double minOffer = RestaurantBasketDetails.getRestaurantMinOfferPrice();
        if (minOffer > 0
                && subTotalPrice > minOffer) {
            offerPercentageLabel.setVisibility(View.VISIBLE);
            offerPercentage.setVisibility(View.VISIBLE);
            offerPercentageLabel.append(" " + offer + "%");
            offerDiscount = Math.round((subTotalPrice * offer / 100) * 100.0) / 100.0;
            offerPercentage.setText(String.valueOf(offerDiscount));
        }
        packingChargePrice = RestaurantBasketDetails.getRestaurantPackingCharges();
        packingCharges.setText(String.valueOf(packingChargePrice));
        gstCharges = Math.round((subTotalPrice * 5 / 100) * 100.0) / 100.0;
        tax.setText(String.valueOf(gstCharges));
        totalPrice = Math.round((subTotalPrice - offerDiscount + packingChargePrice + gstCharges) * 100.0) / 100.0;
        grandTotalPrice = totalPrice;
        total.setText(String.valueOf(totalPrice));
        grandTotal.setText("₹ " + totalPrice);
        pay_txt.setText("Pay  ₹ " + grandTotalPrice);
    }

    class BasketItemAdapter extends BaseAdapter {
        OnItemModifyListener onItemModifyListener;

        BasketItemAdapter(OnItemModifyListener onItemModifyListener) {
            this.onItemModifyListener = onItemModifyListener;
        }

        JSONArray keyNames = RestaurantBasketDetails.getItemObjectKeysNames();

        @Override
        public int getCount() {
            return keyNames.length();
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
            ImageView vegStatusImg;
            TextView itemName, itemPrice, itemOfferPrice, itemQuantity, itemQuantitySum;
            Button incrementItem, decrementItem;
            convertView = getLayoutInflater().inflate(R.layout.basket_item_view, null);
            vegStatusImg = convertView.findViewById(R.id.basket_veg_status);
            itemName = convertView.findViewById(R.id.basket_item_name);
            itemPrice = convertView.findViewById(R.id.basket_item_price);
            itemOfferPrice = convertView.findViewById(R.id.basket_item_discount_price);
            itemQuantity = convertView.findViewById(R.id.item_quantity);
            itemQuantitySum = convertView.findViewById(R.id.item_quantity_sum);
            incrementItem = convertView.findViewById(R.id.btn_item_increment);
            decrementItem = convertView.findViewById(R.id.btn_item_decrement);

            initializeValuesToView(position, vegStatusImg, itemName, itemPrice, itemOfferPrice, itemQuantity, itemQuantitySum);
            initializeListenersToView(incrementItem, decrementItem, itemName);
            return convertView;
        }

        private void initializeListenersToView(Button incrementItem, Button decrementItem, TextView itemName) {
            incrementItem.setOnClickListener(v -> {
                RestaurantBasketDetails.addOneQuantityToItem(itemName.getText().toString());
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, BasketFragment.newInstance(context, onItemModifyListener), getString(R.string.basket));
                transaction.commit();
                onItemModifyListener.onChange();
            });

            decrementItem.setOnClickListener(v -> {
                RestaurantBasketDetails.removeOneQuantityFromItem(itemName.getText().toString());
                Intent homeActivityIntent = new Intent(context, HomeActivity.class);
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, BasketFragment.newInstance(context, onItemModifyListener), getString(R.string.basket));
                transaction.commit();
                onItemModifyListener.onChange();
            });
        }

        private void initializeValuesToView(int position, ImageView vegStatusImg, TextView itemName, TextView itemPrice, TextView itemOfferPrice, TextView itemQuantity, TextView itemQuantitySum) {
            try {
                JSONObject itemObject = RestaurantBasketDetails.getItemObject(keyNames.getString(position));
                itemName.setText(itemObject.getString(RestaurantItemsDatabaseAdapter.ITEM_NAME));
                if (itemObject.getBoolean(RestaurantItemsDatabaseAdapter.IS_VEG)) {
                    vegStatusImg.setImageResource(R.drawable.veg_logo);
                }
                double itemPriceVal = itemObject.getDouble(RestaurantItemsDatabaseAdapter.ITEM_PRICE);

                double offerPrice = itemObject.getDouble(RestaurantBasketDetails.OFFER_PRICE);
                int quantity = itemObject.getInt(RestaurantBasketDetails.ITEM_QUANTITY);
                double total;

                itemPrice.setText("₹ " + itemPriceVal);
                if (offerPrice > 0.0) {
                    itemOfferPrice.setText("₹ " + offerPrice);
                    itemOfferPrice.setVisibility(View.VISIBLE);
                    itemPrice.setPaintFlags(itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    total = Math.round(offerPrice * quantity * 100.0) / 100.0;
                } else {
                    total = Math.round(itemPriceVal * quantity * 100.0) / 100.0;
                }
                itemQuantity.setText("" + quantity);
                itemQuantitySum.setText("" + total);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

interface OnItemModifyListener {
    void onChange();
}
