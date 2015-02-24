package android.ble.wcm.microchip.com.microchip.model;

import com.google.common.hash.Hashing;
import android.ble.wcm.microchip.com.microchip.api.ErrorTypedInput;
import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseCursor;
import android.ble.wcm.microchip.com.microchip.utils.GGson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Model object for the {@code response}.
 */
public class ResponseModel{

    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int INTERNAL_ERROR = 500;

    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final int DEVICE_TYPE_IPHONE = 3;
    public static final int DEVICE_TYPE_ANDROID = 4;
    public static final int DEVICE_TYPE_BLECM_CM4020 = 5;
    public static final int DEVICE_TYPE_BLECM_CM4220 = 6;

    private static final String RN4020 = "RN4020";
    private static final String RN4220 = "RN4220";

    public Integer sha1;
    public Integer statusCode;
    public String url;
    public String methodType;
    public String responseStatus;
    public String responseError;

    public ResponseModel(){}

    public ResponseModel(ResponseCursor cursor){
        this.sha1 = cursor.getSha1();
        this.statusCode = cursor.getStatusCode();
        this.url = cursor.getUrl();
        this.methodType = cursor.getMethodType();
        this.responseStatus = cursor.getResponseStatus();
        this.responseError = cursor.getResponseError();
    }

    public ResponseModel(Status status, String methodType, String url){
        this.statusCode = OK;
        this.url = url;
        this.methodType = methodType;
        this.responseStatus = GGson.toJson(status);
        this.sha1 = Hashing.sha1().hashString(this.responseStatus, Charset.defaultCharset()).asInt();
    }

    public ResponseModel(Response response, String methodType) {
        setContent(response, methodType);
    }

    public ResponseModel(RetrofitError error, String methodType){
        Response response = error.getResponse() == null
                ? new Response(
                            error.getUrl(),
                            400,
                            error.getMessage(),
                            new ArrayList<Header>(),
                            new ErrorTypedInput(error.getMessage()))

                : error.getResponse();

        setContent(response, methodType);
    }

    private void setContent(Response response, String methodType) {
        this.statusCode = response.getStatus();
        this.url = response.getUrl();
        this.methodType = methodType;

        switch (this.statusCode){
            case OK:
                this.responseStatus = getResponseAsString(response);
                this.sha1 = Hashing.sha1().hashString(this.responseStatus, Charset.defaultCharset()).asInt();
                break;

            case BAD_REQUEST:
            case INTERNAL_ERROR:
                this.responseError = getResponseAsString(response);
                this.sha1 = Hashing.sha1().hashString(this.responseError, Charset.defaultCharset()).asInt();
                break;

            default:
                this.responseError = getResponseAsString(response);
                this.sha1 = Hashing.sha1().hashString(this.responseError, Charset.defaultCharset()).asInt();
        }
    }

    public String parseError(){
        if(responseError != null){
            Status status = GGson.fromJson(responseError, Status.class);
            return status == null ? responseError : status.getMessage();
        }
        return null;
    }

    /**
     * Try to get response body as a String
     *
     * @param response Retrofit response
     * @return response body as string
     */
    private String getResponseAsString(Response response) {
        BufferedReader reader;
        String sb = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            try {
                String line;
                sb = "";
                while ((line = reader.readLine()) != null) {
                    sb += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof ResponseModel){
            ResponseModel oResponse = (ResponseModel) o;
            return sha1.equals(oResponse.sha1);
        }
        return false;
    }

    public static int getDeviceType(String modelNumber) {
        if(modelNumber == null) {
            return DEVICE_TYPE_ANDROID;
        }else if(modelNumber.toUpperCase().equals(RN4020)){
            return DEVICE_TYPE_BLECM_CM4020;
        }else if(modelNumber.toUpperCase().equals(RN4220)){
            return DEVICE_TYPE_BLECM_CM4220;
        }
        return DEVICE_TYPE_ANDROID;
    }
}