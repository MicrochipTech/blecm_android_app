package android.ble.wcm.microchip.com.microchip.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by: WillowTree
 * Date: 1/20/15
 * Time: 2:41 PM
 *
 * BLE Utils class to facilitate decoding and encoding of bytes, byte arrays, and hex strings.
 */
public class BleUtils {

    private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String decodeHexString(String hexString){
        StringBuilder sb = new StringBuilder();
        char[] hexData = hexString.toCharArray();
        for(int count = 0; count < hexData.length - 1; count += 2){
            int firstDigit = Character.digit(hexData[count], 16);
            int secondDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + secondDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static String bytesToDecodedHexString(byte[] bytes){
        return decodeHexString(bytesToHexString(bytes));
    }

    public static byte[] stringToHexByteArray(String string){
        byte[] bytes = new byte[4];
        if(string != null && string.length() >= 4){
            bytes[0] = (byte) (string.charAt(0) == '1' ? 0x01 : 0x00);
            bytes[1] = (byte) (string.charAt(1) == '1' ? 0x01 : 0x00);
            bytes[2] = (byte) (string.charAt(2) == '1' ? 0x01 : 0x00);
            bytes[3] = (byte) (string.charAt(3) == '1' ? 0x01 : 0x00);
        }
        return bytes;
    }

    public static List<UUID> parseUuids(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }

        return uuids;
    }

    public static String addColonsToAddress(String address){
        if(address != null && !address.contains(":")){
            String a = "";
            for(int i = 0; i < address.length(); i++){
                a += address.charAt(i);
                if(i % 2 == 1 && i != address.length() - 1){
                    a += ":";
                }
            }
            address = a;
        }
        return  address;
    }

}
