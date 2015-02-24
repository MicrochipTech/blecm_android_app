package android.ble.wcm.microchip.com.microchip.api;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import android.ble.wcm.microchip.com.microchip.DataStore;
import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseProvider;
import android.ble.wcm.microchip.com.microchip.utils.GGson;
import android.ble.wcm.microchip.com.microchip.utils.SSLHttpClient;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ApiService extends IntentService {

    private static final int TIMEOUT = 3000;

    public static final String BROADCAST = "ApiService.broadcast";
    public static final String API_TYPE = "api_type";

    public static final String STATUS = "status";
    public static final String USER_POST = "user_post";

    public static final int TYPE_GET_STATUS = 0;
    public static final int TYPE_POST_STATUS = 1;

    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    RestService service;
    LocalBroadcastManager broadcast;

    public ApiService() {
        super("Api Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = getApi(this);
        broadcast = LocalBroadcastManager.getInstance(this);
    }

    public static RestService getApi(Context context){

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(DataStore.getAwsAmi(context))
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if(DataStore.getIgnoreSllErrors(context))
            builder.setClient(new ApacheClient(SSLHttpClient.getIgnoreSllClient()));

        RestAdapter restAdapter = builder.build();
        return restAdapter.create(RestService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch(intent.getExtras().getInt(API_TYPE, -1)){

            case TYPE_GET_STATUS:
                getStatus();
                break;

            case TYPE_POST_STATUS:
                postStatus(intent);
                break;

        }
    }

    private void getStatus() {
        ResponseModel responseModel;
        try{
            Response response = service.getStatus(DataStore.getBleDeviceUuid(this));
            responseModel = new ResponseModel(response, ResponseModel.GET);
            sendStatusBroadcast(SUCCESS, TYPE_GET_STATUS);

        }catch (RetrofitError error){

            responseModel = new ResponseModel(error, ResponseModel.GET);
            sendStatusBroadcast(ERROR, TYPE_GET_STATUS);

        }
        ResponseProvider.insert(this, responseModel);
    }

    private void postStatus(Intent intent) {
        // Get Status and boolean to see if user initiated the POST
        Status status = GGson.fromJson(intent.getStringExtra(STATUS), Status.class);
        boolean userPost = intent.getBooleanExtra(USER_POST, false);

        // Insert Post
        ResponseProvider.insert(this, getPostResponseModel(status));

        ResponseModel responseModel;
        try{

            DataStore.persistUserPostInProgress(this, userPost);
            Response response = service.postStatus(status);

        }catch (RetrofitError error){

            responseModel = new ResponseModel(error, ResponseModel.POST);
            ResponseProvider.insert(this, responseModel);

        }

        DataStore.persistUserPostInProgress(this, false);
    }

    private void sendStatusBroadcast(int status, int type){
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(API_TYPE, type);
        intent.putExtra(STATUS, status);
        broadcast.sendBroadcast(intent);
    }

    private ResponseModel getPostResponseModel(Status status) {
        return new ResponseModel(
                        status,
                        ResponseModel.POST,
                        DataStore.getAwsAmi(this) + RestService.JSON_WCM_POST_STATUS);
    }

}