package android.ble.wcm.microchip.com.microchip.ble;

import java.util.UUID;

/**
 * Created by: WillowTree
 * Date: 1/13/15
 * Time: 4:49 PM
 */
public class GattServiceAttributes {

    /**
     * 16-bit Service UUIDs
     */
    private static String BASE_16_BIT = "0000XXXX-0000-1000-8000-00805f9b34fb";
    private static String SERVICE_DEVICE_INFORMATION = "180A";

    /**
     * Private 128-bit Service UUID
     */
    private static String SERVICE = "28238791-ec55-4130-86e0-002cd96aec9d";

    /**
     * Private Characteristics UUIDs
     */
    private static String CHARACTERISTIC_BUTTONS = "8f7087bd-fdf3-4b87-b10f-abbf636b1cd5";
    private static String CHARACTERISTIC_POTENTIOMETER = "362232e5-c5a9-4af6-b30c-e208f1a9ae3e";
    private static String CHARACTERISTIC_LEDS = "cd830609-3afa-4a9d-a58b-8224cd2ded70";

    /**
     * Generic Characteristic
     */
    private static String CHARACTERISTIC_GENERIC = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * Assigned Characteristics
     */
    private static String CHARACTERISTIC_MODEL_NUMBER = "2A24";
    private static String CHARACTERISTIC_SERIAL_NUMBER = "2A25";

    /**
     * UUIDs
     */
    public static UUID UUID_SERVICE = UUID.fromString(SERVICE);
    public static UUID UUID_SERVICE_DEVICE_INFORMATION = UUID.fromString(BASE_16_BIT.replace("XXXX", SERVICE_DEVICE_INFORMATION));

    public static UUID UUID_CHARACTERITIC_GENERIC = UUID.fromString(CHARACTERISTIC_GENERIC);
    public static UUID UUID_CHARACTERISTIC_BUTTONS = UUID.fromString(CHARACTERISTIC_BUTTONS);
    public static UUID UUID_CHARACTERISTIC_POTENTIOMETER = UUID.fromString(CHARACTERISTIC_POTENTIOMETER);
    public static UUID UUID_CHARACTERISTIC_LEDS = UUID.fromString(CHARACTERISTIC_LEDS);
    public static UUID UUID_CHARACTERISITC_MODEL_NUMBER = UUID.fromString(BASE_16_BIT.replace("XXXX", CHARACTERISTIC_MODEL_NUMBER));
    public static UUID UUID_CHARACTERISITC_SERIAL_NUMBER = UUID.fromString(BASE_16_BIT.replace("XXXX", CHARACTERISTIC_SERIAL_NUMBER));

}
