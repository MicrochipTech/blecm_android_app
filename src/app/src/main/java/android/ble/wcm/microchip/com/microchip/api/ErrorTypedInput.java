package android.ble.wcm.microchip.com.microchip.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.mime.TypedInput;

/**
 * Created by: WillowTree
 * Date: 11/19/14
 * Time: 12:18 AM.
 */
public class ErrorTypedInput implements TypedInput {

    String message;

    public ErrorTypedInput(String message){
        this.message = message;
    }

    @Override
    public String mimeType() {
        return null;
    }

    @Override
    public long length() {
        return message == null ? 0 : message.length();
    }

    @Override
    public InputStream in() throws IOException {
        return new ByteArrayInputStream(message.getBytes("UTF-8"));
    }
}
