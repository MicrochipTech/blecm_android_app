
package android.ble.wcm.microchip.com.microchip.api.model.Status;

import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("device-type")
    public Integer deviceType;
    public String uuid;
    public Integer status;
    public String message;
    public Data data;

    public Device(){
        data = new Data();
    }

}
