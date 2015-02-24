package android.ble.wcm.microchip.com.microchip.provider.response;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.base.AbstractContentValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Content values wrapper for the {@code response} table.
 */
public class ResponseContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return ResponseColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     * 
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, ResponseSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public ResponseContentValues putSha1(Integer value) {
        mContentValues.put(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseContentValues putSha1Null() {
        mContentValues.putNull(ResponseColumns.SHA1);
        return this;
    }


    public ResponseContentValues putStatusCode(Integer value) {
        mContentValues.put(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseContentValues putStatusCodeNull() {
        mContentValues.putNull(ResponseColumns.STATUS_CODE);
        return this;
    }


    public ResponseContentValues putUrl(String value) {
        mContentValues.put(ResponseColumns.URL, value);
        return this;
    }

    public ResponseContentValues putUrlNull() {
        mContentValues.putNull(ResponseColumns.URL);
        return this;
    }


    public ResponseContentValues putMethodType(String value) {
        mContentValues.put(ResponseColumns.METHOD_TYPE, value);
        return this;
    }

    public ResponseContentValues putMethodTypeNull() {
        mContentValues.putNull(ResponseColumns.METHOD_TYPE);
        return this;
    }


    public ResponseContentValues putResponseStatus(String value) {
        mContentValues.put(ResponseColumns.RESPONSE_STATUS, value);
        return this;
    }

    public ResponseContentValues putResponseStatusNull() {
        mContentValues.putNull(ResponseColumns.RESPONSE_STATUS);
        return this;
    }


    public ResponseContentValues putResponseError(String value) {
        mContentValues.put(ResponseColumns.RESPONSE_ERROR, value);
        return this;
    }

    public ResponseContentValues putResponseErrorNull() {
        mContentValues.putNull(ResponseColumns.RESPONSE_ERROR);
        return this;
    }


    public static ContentValues[] getContentValues(ResponseModel... items){
        List<ContentValues> values = new ArrayList<ContentValues>();
        for(ResponseModel item : items){
            values.add(getSingleContentValue(item));
        }
        return values.toArray(new ContentValues[values.size()]);
    }

    public static ContentValues getSingleContentValue(ResponseModel item){
        ResponseContentValues values = new ResponseContentValues();
        values.putSha1(item.sha1);
        values.putStatusCode(item.statusCode);
        values.putUrl(item.url);
        values.putMethodType(item.methodType);
        values.putResponseStatus(item.responseStatus);
        values.putResponseError(item.responseError);
        return values.values();
    }
}
