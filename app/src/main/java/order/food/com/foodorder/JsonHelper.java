package order.food.com.foodorder;

import android.content.ContentValues;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JsonHelper {

    public static JSONArray readJsonFromFile(AssetManager assertManager, String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(assertManager.open(filePath)));
            StringBuilder jsonContent = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                jsonContent.append(mLine);
            }
            return new JSONArray(jsonContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static ContentValues convertJsonObjectToContentValues(JSONArray jsonArray, int i) {
        ContentValues cv = new ContentValues();
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            jsonObject.keys().forEachRemaining(
                    key -> {
                        Object val = null;
                        try {
                            val = jsonObject.get(key);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (val != null && val instanceof Double || val instanceof Integer || val instanceof String || val instanceof Boolean || val instanceof Long || val instanceof Float || val instanceof Short)
                            cv.put(key, val.toString());
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cv;
    }
}
