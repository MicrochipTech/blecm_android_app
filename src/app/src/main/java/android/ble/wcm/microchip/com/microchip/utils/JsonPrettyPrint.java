package android.ble.wcm.microchip.com.microchip.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Created by: WillowTree
 * Date: 11/20/14
 * Time: 11:48 AM.
 */
public class JsonPrettyPrint {

    public static String convert(String jsonString){
        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(jsonString).getAsJsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(json);
        } catch (JsonSyntaxException jError){
            return jsonString;
        }
    }

}
