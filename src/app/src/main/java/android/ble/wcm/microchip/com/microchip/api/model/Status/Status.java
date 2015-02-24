
package android.ble.wcm.microchip.com.microchip.api.model.Status;

public class Status {

    public Mchp mchp;

    public Status(){
        mchp = new Mchp();
    }

    public boolean isEmpty(){
        return mchp == null || mchp.device == null || mchp.device.data == null;
    }

    public String getMessage(){
        return mchp.device.message;
    }

    public Data getData(){
        return mchp.device.data;
    }

    public boolean getLed1(){
        return !isEmpty() && getData().led1;
    }

    public boolean getLed2(){
        return !isEmpty() && getData().led2;
    }

    public boolean getLed3(){
        return !isEmpty() && getData().led3;
    }

    public boolean getLed4(){
        return !isEmpty() && getData().led4;
    }

    public boolean getButton1(){
        return !isEmpty() && getData().button1;
    }

    public boolean getButton2(){
        return !isEmpty() && getData().button2;
    }

    public boolean getButton3(){
        return !isEmpty() && getData().button3;
    }

    public boolean getButton4(){
        return !isEmpty() && getData().button4;
    }

    public int getPotentiometer(){
        if(!isEmpty()) {
            return getData().potentiometer;
        }
        return 0;
    }

    public void setDeviceType(int deviceType){
        mchp.device.deviceType = deviceType;
    }

    public void setUuid(String uuid){
        mchp.device.uuid = uuid;
    }

    public void setButton1(boolean checked){
        mchp.device.data.button1 = checked;
    }

    public void setButton2(boolean checked){
        mchp.device.data.button2 = checked;
    }

    public void setButton3(boolean checked){
        mchp.device.data.button3 = checked;
    }

    public void setButton4(boolean checked){
        mchp.device.data.button4 = checked;
    }

    public void setLed1(boolean checked){
        mchp.device.data.led1 = checked;
    }

    public void setLed2(boolean checked){
        mchp.device.data.led2 = checked;
    }

    public void setLed3(boolean checked){
        mchp.device.data.led3 = checked;
    }

    public void setLed4(boolean checked){
        mchp.device.data.led4 = checked;
    }

    public void setPotentiometer(int value){
        mchp.device.data.potentiometer = value;
    }

}
