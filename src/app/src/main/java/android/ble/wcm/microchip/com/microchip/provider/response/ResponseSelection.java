package android.ble.wcm.microchip.com.microchip.provider.response;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import android.ble.wcm.microchip.com.microchip.provider.base.AbstractSelection;

/**
 * Selection for the {@code response} table.
 */
public class ResponseSelection extends AbstractSelection<ResponseSelection> {
    @Override
    public Uri uri() {
        return ResponseColumns.CONTENT_URI;
    }
    
    /**
     * Query the given content resolver using this selection.
     * 
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code ResponseCursor} object, which is positioned before the first entry, or null.
     */
    public ResponseCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new ResponseCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null}.
     */
    public ResponseCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null}.
     */
    public ResponseCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }
    
    
    public ResponseSelection id(long... value) {
        addEquals(ResponseColumns._ID, toObjectArray(value));
        return this;
    }

    public ResponseSelection sha1(Integer... value) {
        addEquals(ResponseColumns.SHA1, value);
        return this;
    }
    
    public ResponseSelection sha1Not(Integer... value) {
        addNotEquals(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseSelection sha1Gt(int value) {
        addGreaterThan(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseSelection sha1GtEq(int value) {
        addGreaterThanOrEquals(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseSelection sha1Lt(int value) {
        addLessThan(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseSelection sha1LtEq(int value) {
        addLessThanOrEquals(ResponseColumns.SHA1, value);
        return this;
    }

    public ResponseSelection statusCode(Integer... value) {
        addEquals(ResponseColumns.STATUS_CODE, value);
        return this;
    }
    
    public ResponseSelection statusCodeNot(Integer... value) {
        addNotEquals(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseSelection statusCodeGt(int value) {
        addGreaterThan(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseSelection statusCodeGtEq(int value) {
        addGreaterThanOrEquals(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseSelection statusCodeLt(int value) {
        addLessThan(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseSelection statusCodeLtEq(int value) {
        addLessThanOrEquals(ResponseColumns.STATUS_CODE, value);
        return this;
    }

    public ResponseSelection url(String... value) {
        addEquals(ResponseColumns.URL, value);
        return this;
    }
    
    public ResponseSelection urlNot(String... value) {
        addNotEquals(ResponseColumns.URL, value);
        return this;
    }


    public ResponseSelection methodType(String... value) {
        addEquals(ResponseColumns.METHOD_TYPE, value);
        return this;
    }
    
    public ResponseSelection methodTypeNot(String... value) {
        addNotEquals(ResponseColumns.METHOD_TYPE, value);
        return this;
    }


    public ResponseSelection responseStatus(String... value) {
        addEquals(ResponseColumns.RESPONSE_STATUS, value);
        return this;
    }
    
    public ResponseSelection responseStatusNot(String... value) {
        addNotEquals(ResponseColumns.RESPONSE_STATUS, value);
        return this;
    }


    public ResponseSelection responseError(String... value) {
        addEquals(ResponseColumns.RESPONSE_ERROR, value);
        return this;
    }
    
    public ResponseSelection responseErrorNot(String... value) {
        addNotEquals(ResponseColumns.RESPONSE_ERROR, value);
        return this;
    }

}
