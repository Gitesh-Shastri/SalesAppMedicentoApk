package com.salesappmedicento.networking.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils{

    public static String getJsonValueFromKey(JSONObject jsonObject, String key) {
        try {
            if(jsonObject.get(key) instanceof Integer) {
                return String.valueOf(jsonObject.getInt(key));
            }
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean getBooleanValueFromJsonKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int getIntegerValueFromJsonKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float getDoubleValue(JSONObject jsonObject, String key) {
        try {
            return (float) jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getStringValueFromJsonKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static JSONArray getJsonArrayFromKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

}
