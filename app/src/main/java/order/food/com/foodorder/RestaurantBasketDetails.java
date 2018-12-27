package order.food.com.foodorder;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static order.food.com.foodorder.CouponHistoryDatabaseAdapter.COUNT;

public class RestaurantBasketDetails {

    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String OFFER_PRICE = "offerPrice";
    public static final String ITEM_OBJECT = "itemObject";
    public static final String ITEM_QUANTITY = "itemQuantity";
    public static final String TOTAL = "total";
    public static final String IS_BASKET_EMPTY = "isBasketEmpty";
    public static final String RESTAURANT_LOCATION = "restaurantLocation";

    private static JSONObject restaurantDetails = new JSONObject();
    private static JSONObject jsonItemObject = new JSONObject();

    public static Double total;

    public static int count;

    public static boolean isBasketEmpty;

    public static void initializeRestaurantDetailsFromSharedPreferences(SharedPreferences sharedPreferences, int userId) {
        String basketDetails = sharedPreferences.getString(String.valueOf(userId), "");
        restaurantDetails = null;
        try {
            restaurantDetails = new JSONObject(basketDetails);
            total = restaurantDetails.getDouble(TOTAL);
            isBasketEmpty = restaurantDetails.getBoolean(IS_BASKET_EMPTY);
            jsonItemObject = restaurantDetails.getJSONObject(ITEM_OBJECT);
            count = restaurantDetails.getInt(COUNT);
        } catch (Exception e) {
            restaurantDetails = new JSONObject();
            isBasketEmpty = true;
            total = 0.0;
            count = 0;
        }
    }

    public static void saveRestaurantDetailsToSharedPreferences(SharedPreferences sharedPreferences) {
        try {
            if (!isBasketEmpty) {
                updateSharedPreferences(sharedPreferences);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateSharedPreferences(SharedPreferences sharedPreferences) throws JSONException {
        restaurantDetails.put(TOTAL, total);
        restaurantDetails.put(IS_BASKET_EMPTY, isBasketEmpty);
        restaurantDetails.put(ITEM_OBJECT, jsonItemObject);
        restaurantDetails.put(COUNT, count);
        sharedPreferences.edit().putString(String.valueOf(HomeActivity.userId), restaurantDetails.toString()).commit();
    }

    public static void clearBasket(SharedPreferences sharedPreferences) {
        try {
            clearInstanceData();
            updateSharedPreferences(sharedPreferences);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void clearForLogout() {
        clearInstanceData();
    }

    private static void clearInstanceData() {
        isBasketEmpty = true;
        total = 0.0;
        count = 0;
        jsonItemObject = null;
        jsonItemObject = new JSONObject();
        restaurantDetails.remove(RestaurantItemsDatabaseAdapter.RESTAURANT_ID);
        restaurantDetails.remove(RESTAURANT_NAME);
        restaurantDetails.remove(RESTAURANT_LOCATION);
        restaurantDetails.remove(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD);
        restaurantDetails.remove(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD);
        restaurantDetails.remove(RestaurantDatabaseAdapter.PACKING_CHARGES_FIELD);
    }

    public static int getRestaurantId() {
        try {
            return restaurantDetails.getInt(RestaurantItemsDatabaseAdapter.RESTAURANT_ID);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void addRestaurantId(int restaurantId) {
        try {
            restaurantDetails.put(RestaurantItemsDatabaseAdapter.RESTAURANT_ID, restaurantId);
        } catch (Exception e) {
        }
    }

    public static String getRestaurantName() {
        try {
            return restaurantDetails.getString(RESTAURANT_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    public static void addRestaurantName(String restaurantName) {
        try {
            restaurantDetails.put(RESTAURANT_NAME, restaurantName);
        } catch (Exception e) {
        }
    }

    public static String getRestaurantLocation() {
        try {
            return restaurantDetails.getString(RESTAURANT_LOCATION);
        } catch (Exception e) {
            return null;
        }
    }

    public static void addRestaurantLocation(String restaurantLocation) {
        try {
            restaurantDetails.put(RESTAURANT_LOCATION, restaurantLocation);
        } catch (Exception e) {
        }
    }

    public static int getRestaurantOfferPercentage() {
        try {
            return restaurantDetails.getInt(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void addRestaurantOfferPercentage(int offerPercentage) {
        try {
            restaurantDetails.put(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD, offerPercentage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getRestaurantPackingCharges() {
        try {
            return restaurantDetails.getDouble(RestaurantDatabaseAdapter.PACKING_CHARGES_FIELD);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void addRestaurantPackingCharges(double offerPercentage) {
        try {
            restaurantDetails.put(RestaurantDatabaseAdapter.PACKING_CHARGES_FIELD, offerPercentage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getRestaurantMinOfferPrice() {
        try {
            return restaurantDetails.getDouble(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void addRestaurantMinOfferPrice(double minOfferPrice) {
        try {
            restaurantDetails.put(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD, minOfferPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double addNewItemToBasketWithQuantity(String itemName, boolean isVeg, double actualPrice, double offerPrice, int quantity) {
        JSONObject itemObject = new JSONObject();
        try {
            itemObject.put(RestaurantItemsDatabaseAdapter.ITEM_NAME, itemName);
            itemObject.put(RestaurantItemsDatabaseAdapter.IS_VEG, isVeg);
            itemObject.put(RestaurantItemsDatabaseAdapter.ITEM_PRICE, actualPrice);
            itemObject.put(OFFER_PRICE, offerPrice);
            itemObject.put(ITEM_QUANTITY, quantity);
            jsonItemObject.put(itemName, itemObject);
            calculateTotal(actualPrice, offerPrice);
            count++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }


    public static double addNewItemToBasket(String itemName, boolean isVeg, double actualPrice, double offerPrice) {
        JSONObject itemObject = new JSONObject();
        try {
            itemObject.put(RestaurantItemsDatabaseAdapter.ITEM_NAME, itemName);
            itemObject.put(RestaurantItemsDatabaseAdapter.IS_VEG, isVeg);
            itemObject.put(RestaurantItemsDatabaseAdapter.ITEM_PRICE, actualPrice);
            itemObject.put(OFFER_PRICE, offerPrice);
            itemObject.put(ITEM_QUANTITY, 1);
            jsonItemObject.put(itemName, itemObject);
            calculateTotal(actualPrice, offerPrice);
            count++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static double addOneQuantityToItem(String itemName) {
        JSONObject itemObject;
        try {
            itemObject = jsonItemObject.getJSONObject(itemName);
            if (itemObject != null) {
                int quantity = itemObject.getInt(ITEM_QUANTITY);
                itemObject.put(ITEM_QUANTITY, ++quantity);
                jsonItemObject.put(itemName, itemObject);
                restaurantDetails.put(ITEM_OBJECT, jsonItemObject);
                double actualPrice = itemObject.getDouble(RestaurantItemsDatabaseAdapter.ITEM_PRICE);
                double offerPrice = itemObject.getDouble(OFFER_PRICE);
                calculateTotal(actualPrice, offerPrice);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    private static void calculateTotal(double actualPrice, double offerPrice) {
        if (offerPrice <= 0) {
            total = total + actualPrice;
        } else {
            total = total + offerPrice;
        }
        total = Math.round(total * 100.0) / 100.0;
    }

    private static void getDiffTotal(double actualPrice, double offerPrice) {
        if (offerPrice <= 0) {
            total = total - actualPrice;
        } else {
            total = total - offerPrice;
        }
        total = Math.round(total * 100.0) / 100.0;
    }

    public static double removeOneQuantityFromItem(String itemName) {
        JSONObject itemObject;
        try {
            itemObject = jsonItemObject.getJSONObject(itemName);
            if (itemName != null) {
                int quantity = itemObject.getInt(ITEM_QUANTITY);
                if (quantity > 1) {
                    itemObject.put(ITEM_QUANTITY, --quantity);
                    jsonItemObject.put(itemName, itemObject);
                } else {
                    jsonItemObject.remove(itemName);
                }
                restaurantDetails.put(ITEM_OBJECT, jsonItemObject);
                double actualPrice = itemObject.getDouble(RestaurantItemsDatabaseAdapter.ITEM_PRICE);
                double offerPrice = itemObject.getDouble(OFFER_PRICE);
                getDiffTotal(actualPrice, offerPrice);
                count--;
                if (count == 0) {
                    isBasketEmpty = true;
                    total = 0.0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static boolean isItemNameExistInItemObject(String itemName) {
        try {
            jsonItemObject.getJSONObject(itemName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getItemQuantityByItemName(String itemName) {
        JSONObject itemObject;
        try {
            itemObject = jsonItemObject.getJSONObject(itemName);
            return itemObject.getInt(ITEM_QUANTITY);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getItemCount() {
        return jsonItemObject.length();
    }

    public static JSONObject getItemObject(String itemName) {
        try {
            return jsonItemObject.getJSONObject(itemName);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONArray getItemObjectKeysNames() {
        return jsonItemObject.names();
    }

}
