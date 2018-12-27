package order.food.com.foodorder;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RestaurantItemAdapter extends RecyclerView.Adapter<RestaurantMenuHolder> {
    Context context;
    Cursor menuCursor;
    public static BottomAppBar bottomAppBar;
    public static TextView checkoutTxtView;
    public int position = -1;

    RestaurantItemsDatabaseAdapter restaurantItemsDatabaseAdapter;
    int offerPercent;
    double minPrice;

    public RestaurantItemAdapter(Context c, Cursor cursor, BottomAppBar bottomAppBar, TextView checkoutTxtView) {
        context = c;
        menuCursor = cursor;
        this.bottomAppBar = bottomAppBar;
        this.checkoutTxtView = checkoutTxtView;
        restaurantItemsDatabaseAdapter = new RestaurantItemsDatabaseAdapter(c);
        offerPercent = RestaurantItemsActivity.restaurantCursor.getInt(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD));
        minPrice = RestaurantItemsActivity.restaurantCursor.getDouble(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD));

    }

    @NonNull
    @Override
    public RestaurantMenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RestaurantMenuHolder(LayoutInflater.from(context).inflate(R.layout.restaurant_menu_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantMenuHolder restaurantMenuHolder, int i) {
        menuCursor.moveToPosition(i);
        String menuType = menuCursor.getString(menuCursor.getColumnIndexOrThrow(RestaurantItemsDatabaseAdapter.ITEM_MENU));
        restaurantMenuHolder.menuText.setText(menuType);
        Cursor itemCursor = restaurantItemsDatabaseAdapter.getRestaurantItemsByRestaurantIdAndMenuType(RestaurantItemsActivity.restaurantId, menuType);
        RestaurantEachItemAdapter restaurantEachItemAdapter = new RestaurantEachItemAdapter(context, itemCursor, offerPercent, minPrice);
        restaurantMenuHolder.itemListView.setAdapter(restaurantEachItemAdapter);
        BasketFragment.setListViewHeightBasedOnChildren(restaurantEachItemAdapter, restaurantMenuHolder.itemListView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RestaurantMenuHolder holder) {
        super.onViewAttachedToWindow(holder);
        position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return menuCursor.getCount();
    }
}

class RestaurantMenuHolder extends RecyclerView.ViewHolder {
    TextView menuText;
    ListView itemListView;

    public RestaurantMenuHolder(@NonNull View itemView) {
        super(itemView);
        menuText = (TextView) itemView.findViewById(R.id.item_menu_txt);
        itemListView = (ListView) itemView.findViewById(R.id.menu_items_list_view);
    }
}


class RestaurantEachItemAdapter extends BaseAdapter {

    Context context;
    Cursor itemCursor;
    int offerPercent;
    double minOffer;

    DecimalFormat f = new DecimalFormat("##.00");


    public RestaurantEachItemAdapter(Context c, Cursor cursor, int offerPercent, double minOffer) {
        this.context = c;
        this.itemCursor = cursor;
        this.offerPercent = offerPercent;
        this.minOffer = minOffer;
    }

    @Override
    public int getCount() {
        return itemCursor.getCount();
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
    public View getView(int position, View itemView, ViewGroup parent) {
        ImageView vegStatusImg;
        TextView itemNameTxt, itemPriceTxt, discountPriceTxt, noOfItemsTxt;
        Button addBtn, incrementBtn, decrementBtn;
        boolean isVeg;

        itemView = LayoutInflater.from(context).inflate(R.layout.restaurant_each_item_view, null);
        vegStatusImg = (ImageView) itemView.findViewById(R.id.veg_status);
        itemNameTxt = (TextView) itemView.findViewById(R.id.item_name);
        itemPriceTxt = (TextView) itemView.findViewById(R.id.item_price);
        discountPriceTxt = (TextView) itemView.findViewById(R.id.item_discount_price);
        noOfItemsTxt = (TextView) itemView.findViewById(R.id.no_of_items);
        addBtn = (Button) itemView.findViewById(R.id.item_add_btn);
        incrementBtn = (Button) itemView.findViewById(R.id.btn_item_increment);
        decrementBtn = (Button) itemView.findViewById(R.id.btn_item_decrement);

        itemCursor.moveToPosition(position);
        isVeg = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(RestaurantItemsDatabaseAdapter.IS_VEG)) == 0;
        setItemVegStatus(vegStatusImg, isVeg);
        itemNameTxt.setText(itemCursor.getString(itemCursor.getColumnIndexOrThrow(RestaurantItemsDatabaseAdapter.ITEM_NAME)));
        setItemPriceAndDiscountPriceIfExist(itemPriceTxt, discountPriceTxt);

        setItemDetailsIfAddedInBasket(addBtn, incrementBtn, decrementBtn, noOfItemsTxt, itemNameTxt);

        addBtn.setOnClickListener((v) -> {
            if (RestaurantBasketDetails.isBasketEmpty) {
                addItemToBasketDetails(itemNameTxt, itemPriceTxt, discountPriceTxt, noOfItemsTxt, addBtn, incrementBtn, decrementBtn, isVeg);
                RestaurantBasketDetails.isBasketEmpty = false;
                addRestaurantDetailsToBasketDetails();
            } else if (RestaurantBasketDetails.getRestaurantId() == RestaurantItemsActivity.restaurantId) {
                addItemToBasketDetails(itemNameTxt, itemPriceTxt, discountPriceTxt, noOfItemsTxt, addBtn, incrementBtn, decrementBtn, isVeg);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Basket already contains items from other restaurants. Do you want to replace it?");
                dialog.setPositiveButton("Replace it.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        RestaurantBasketDetails.clearBasket(context.getSharedPreferences(context.getString(R.string.food_order), Context.MODE_PRIVATE));
                        addBtn.callOnClick();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        incrementBtn.setOnClickListener(v -> {
            int noOfCount = Integer.parseInt(noOfItemsTxt.getText().toString());
            noOfItemsTxt.setText(String.valueOf(++noOfCount));
            double total = RestaurantBasketDetails.addOneQuantityToItem(itemNameTxt.getText().toString());
            RestaurantItemAdapter.checkoutTxtView.setText("Checkout  ₹ " + total);
        });

        decrementBtn.setOnClickListener(v -> {
            int noOfCount = Integer.parseInt(noOfItemsTxt.getText().toString());
            if (noOfCount == 1) {
                addBtn.setVisibility(View.VISIBLE);
                incrementBtn.setVisibility(View.INVISIBLE);
                decrementBtn.setVisibility(View.INVISIBLE);
                noOfItemsTxt.setVisibility(View.INVISIBLE);
            } else {
                noOfItemsTxt.setText(String.valueOf(--noOfCount));
            }
            double total = RestaurantBasketDetails.removeOneQuantityFromItem(itemNameTxt.getText().toString());
            RestaurantItemAdapter.checkoutTxtView.setText("Checkout  ₹ " + total);
            if (RestaurantBasketDetails.count == 0) {
                RestaurantItemAdapter.bottomAppBar.setVisibility(View.GONE);
                RestaurantBasketDetails.clearBasket(context.getSharedPreferences(context.getString(R.string.food_order), context.MODE_PRIVATE));
            }
        });

        return itemView;
    }

    private void addRestaurantDetailsToBasketDetails() {
        RestaurantBasketDetails.addRestaurantId(RestaurantItemsActivity.restaurantId);
        String hotelName = RestaurantItemsActivity.restaurantCursor.getString(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.NAME_FIELD));
        RestaurantBasketDetails.addRestaurantName(hotelName);
        String area = RestaurantItemsActivity.restaurantCursor.getString(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.AREA_FIELD));
        String city = RestaurantItemsActivity.restaurantCursor.getString(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.CITY_FIELD));
        RestaurantBasketDetails.addRestaurantLocation(city);
        RestaurantBasketDetails.addRestaurantOfferPercentage(offerPercent);
        RestaurantBasketDetails.addRestaurantMinOfferPrice(minOffer);
        double packingCharges = RestaurantItemsActivity.restaurantCursor.getDouble(RestaurantItemsActivity.restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.PACKING_CHARGES_FIELD));
        RestaurantBasketDetails.addRestaurantPackingCharges(packingCharges);
    }

    private void setItemDetailsIfAddedInBasket(Button addBtn, Button incrementBtn, Button decrementBtn, TextView noOfItemsTxt, TextView itemNameTxt) {
        String itemName = itemNameTxt.getText().toString();
        if (RestaurantBasketDetails.getRestaurantId() == RestaurantItemsActivity.restaurantId &&
                RestaurantBasketDetails.isItemNameExistInItemObject(itemName)) {
            addBtn.setVisibility(View.GONE);
            incrementBtn.setVisibility(View.VISIBLE);
            decrementBtn.setVisibility(View.VISIBLE);
            noOfItemsTxt.setVisibility(View.VISIBLE);
            int itemCount = RestaurantBasketDetails.getItemQuantityByItemName(itemName);
            noOfItemsTxt.setText(String.valueOf(itemCount));
        }
    }

    private void addItemToBasketDetails(TextView itemNameTxt, TextView itemPriceTxt, TextView discountPriceTxt, TextView noOfItemsTxt, Button addBtn, Button incrementBtn, Button decrementBtn, boolean isVeg) {
        addBtn.setVisibility(View.GONE);
        incrementBtn.setVisibility(View.VISIBLE);
        decrementBtn.setVisibility(View.VISIBLE);
        noOfItemsTxt.setVisibility(View.VISIBLE);
        noOfItemsTxt.setText("1");
        if (RestaurantBasketDetails.count == 0) {
            RestaurantItemAdapter.bottomAppBar.setVisibility(View.VISIBLE);
        }
        double discountPrice = 0;
        try {
            discountPrice = Double.valueOf(discountPriceTxt.getText().toString().replace("₹ ", ""));
        } catch (Exception e) {
        }
        double total = RestaurantBasketDetails.addNewItemToBasket(itemNameTxt.getText().toString(),
                isVeg, Double.valueOf(itemPriceTxt.getText().toString().replace("₹ ", "")),
                discountPrice);
        RestaurantItemAdapter.checkoutTxtView.setText("Checkout  ₹ " + total);
    }

    private void setItemPriceAndDiscountPriceIfExist(TextView itemPriceTxt, TextView discountPriceTxt) {
        double itemPrice = itemCursor.getDouble(itemCursor.getColumnIndexOrThrow(RestaurantItemsDatabaseAdapter.ITEM_PRICE));
        itemPriceTxt.setText("₹ " + String.valueOf(itemPrice));
        if (offerPercent > 0 && minOffer <= 0.0) {
            double discountPrice = Math.round((itemPrice * offerPercent / 100) * 100.0) / 100.0;
            discountPriceTxt.setText("₹ " + String.valueOf(itemPrice - discountPrice));
            discountPriceTxt.setVisibility(View.VISIBLE);
            itemPriceTxt.setPaintFlags(itemPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void setItemVegStatus(ImageView vegStatusImg, boolean isVeg) {
        if (isVeg) {
            vegStatusImg.setImageResource(R.drawable.veg_logo);
        } else {
            vegStatusImg.setImageResource(R.drawable.nv_logo);
        }
    }
}
