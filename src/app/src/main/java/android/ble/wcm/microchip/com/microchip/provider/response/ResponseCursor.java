package android.ble.wcm.microchip.com.microchip.provider.response;

import android.database.Cursor;

import android.ble.wcm.microchip.com.microchip.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code response} table.
 */
public class ResponseCursor extends AbstractCursor {
    public ResponseCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code sha1} value.
     * Can be {@code null}.
     */
    public Integer getSha1() {
        return getIntegerOrNull(ResponseColumns.SHA1);
    }

    /**
     * Get the {@code status_code} value.
     * Can be {@code null}.
     */
    public Integer getStatusCode() {
        return getIntegerOrNull(ResponseColumns.STATUS_CODE);
    }

    /**
     * Get the {@code url} value.
     * Can be {@code null}.
     */
    public String getUrl() {
        Integer index = getCachedColumnIndexOrThrow(ResponseColumns.URL);
        return getString(index);
    }

    /**
     * Get the {@code method_type} value.
     * Can be {@code null}.
     */
    public String getMethodType() {
        Integer index = getCachedColumnIndexOrThrow(ResponseColumns.METHOD_TYPE);
        return getString(index);
    }

    /**
     * Get the {@code response_status} value.
     * Can be {@code null}.
     */
    public String getResponseStatus() {
        Integer index = getCachedColumnIndexOrThrow(ResponseColumns.RESPONSE_STATUS);
        return getString(index);
    }

    /**
     * Get the {@code response_error} value.
     * Can be {@code null}.
     */
    public String getResponseError() {
        Integer index = getCachedColumnIndexOrThrow(ResponseColumns.RESPONSE_ERROR);
        return getString(index);
    }
}
