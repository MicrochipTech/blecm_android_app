package android.ble.wcm.microchip.com.microchip.api;

import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface RestService{

    public static final String JSON_WCM_GET_STATUS = "/json_xcm_get_status.php";
    public static final String JSON_WCM_POST_STATUS = "/json_xcm_post_status.php";

    /**
     * Asynchronous Calls
     */
    @GET(JSON_WCM_GET_STATUS)
    void getStatus(@Query("uuid") String uuid, Callback<Status> callback);

    @POST(JSON_WCM_POST_STATUS)
    void postStatus(@Body Status status, Callback<Status> callback);

    /**
     * Synchronous Calls
     */
    @GET(JSON_WCM_GET_STATUS)
    Response getStatus(@Query("uuid") String uuid);

    @POST(JSON_WCM_POST_STATUS)
    Response postStatus(@Body Status status);

}