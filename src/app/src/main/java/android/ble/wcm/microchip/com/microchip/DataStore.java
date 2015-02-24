package android.ble.wcm.microchip.com.microchip;

import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.utils.GGson;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by: WillowTree
 * Date: 11/18/14
 * Time: 2:49 PM.
 */
public class DataStore {

    private static final String AWS_AMI = "aws_ami";
    private static final String IGNORE_SSL_ERRORS = "ignore_ssl_errors";
    private static final String BLE_DEVICE_NAME = "ble_device_name";
    private static final String BLE_DEVICE_MAC_ADDRESS = "ble_devie_mac_address";
    private static final String BLE_DEVICE_UUID = "ble_device_uuid";
    private static final String BLE_STATUS = "ble_status";
    private static final String USER_POST_IN_PROGRESS = "user_post_in_progress";

    private static final String NOT_CONFIGURED = "NOT_CONFIGURED";

    private static EncryptedSharedPreferences getDataStore(Context context) {
        return new EncryptedSharedPreferences(context,
                PreferenceManager.getDefaultSharedPreferences(context));
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getDataStore(context).edit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return getDataStore(context);
    }

    public static String getBleDeviceName(Context context) {
        return getPrefs(context).getString(BLE_DEVICE_NAME, "");
    }

    public static void persistBleDeviceName(Context context, String name) {
        getEditor(context).putString(BLE_DEVICE_NAME, name).commit();
    }

    public static String getBleDeviceMacAddress(Context context){
        return getPrefs(context).getString(BLE_DEVICE_MAC_ADDRESS, "");
    }

    public static void persistBleDeviceMacAddress(Context context, String mac){
        getEditor(context).putString(BLE_DEVICE_MAC_ADDRESS, mac).commit();
    }

    public static String getBleDeviceUuid(Context context){
        String uuid = getPrefs(context).getString(BLE_DEVICE_UUID, NOT_CONFIGURED);
        return uuid;
    }

    public static void persistBleDeviceUuid(Context context, String uuid){
        getEditor(context).putString(BLE_DEVICE_UUID, uuid).commit();
    }

    public static void clearBleDevice(Context context){
        persistBleDeviceMacAddress(context, "");
        persistBleDeviceName(context, "");
        persistBleDeviceUuid(context, NOT_CONFIGURED);
    }

    public static String getAwsAmi(Context context) {
        return getPrefs(context).getString(AWS_AMI, "");
    }

    public static void persistAwsAmi(Context context, String awsAmi) {
        getEditor(context).putString(AWS_AMI, awsAmi).commit();
    }

    public static boolean getIgnoreSllErrors(Context context) {
        return getPrefs(context).getBoolean(IGNORE_SSL_ERRORS, false);
    }

    public static void persistIgnoreSllErrors(Context context, boolean ignoreSllErrors) {
        getEditor(context).putBoolean(IGNORE_SSL_ERRORS, ignoreSllErrors).commit();
    }

    public static Status getBleStatus(Context context){
        return GGson.fromJson(getPrefs(context).getString(BLE_STATUS, ""), Status.class);
    }

    public static void persistBleStatus(Context context, Status status){
        getEditor(context).putString(BLE_STATUS, GGson.toJson(status)).commit();
    }

    public static void persistBleStatusLeds(Context context, Status status){
        Status s = new Status();
        if(getBleStatus(context) != null){
            s = getBleStatus(context);
        }

        s.setLed1(status.getLed1());
        s.setLed2(status.getLed2());
        s.setLed3(status.getLed3());
        s.setLed4(status.getLed4());

        persistBleStatus(context, s);
    }

    public static boolean getUserPostInProgress(Context context){
        return  getPrefs(context).getBoolean(USER_POST_IN_PROGRESS, false);
    }

    public static void persistUserPostInProgress(Context context, boolean userPostInProgress){
        getEditor(context).putBoolean(USER_POST_IN_PROGRESS, userPostInProgress).commit();
    }

}
