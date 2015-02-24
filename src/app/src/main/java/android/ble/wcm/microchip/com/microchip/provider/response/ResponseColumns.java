package android.ble.wcm.microchip.com.microchip.provider.response;

import android.net.Uri;
import android.provider.BaseColumns;

import android.ble.wcm.microchip.com.microchip.provider.MicrochipProvider;

/**
 * Columns for the {@code response} table.
 */
public interface ResponseColumns extends BaseColumns {
    String TABLE_NAME = "response";
    Uri CONTENT_URI = Uri.parse(MicrochipProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    String _ID = BaseColumns._ID;
    String SHA1 = "sha1";
    String STATUS_CODE = "status_code";
    String URL = "url";
    String METHOD_TYPE = "method_type";
    String RESPONSE_STATUS = "response_status";
    String RESPONSE_ERROR = "response_error";

    String DEFAULT_ORDER = _ID;

	// @formatter:off
    String[] FULL_PROJECTION = new String[] {
            _ID,
            SHA1,
            STATUS_CODE,
            URL,
            METHOD_TYPE,
            RESPONSE_STATUS,
            RESPONSE_ERROR
    };
    // @formatter:on
}