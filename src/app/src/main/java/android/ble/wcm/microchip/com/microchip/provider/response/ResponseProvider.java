package android.ble.wcm.microchip.com.microchip.provider.response;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.ble.wcm.microchip.com.microchip.model.ResponseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: WillowTree
 * Date: 11/18/14
 * Time: 3:47 PM.
 */
public class ResponseProvider {


    public static List<ResponseModel> getAll(Context context) {
        Cursor cursor = context.getContentResolver().query(ResponseColumns.CONTENT_URI, null, null, null, ResponseColumns.DEFAULT_ORDER + " DESC");
        ResponseCursor responseCursor = new ResponseCursor(cursor);
        return getList(responseCursor);
    }

    public static void insert(Context context, ResponseModel responseModel) {
        if(responseModel.methodType.equals(ResponseModel.GET)){

            if(!matchLastStatusByType(context, responseModel, responseModel.methodType)){
                ContentValues pocketcardCV = ResponseContentValues.getSingleContentValue(responseModel);
                context.getContentResolver().insert(ResponseColumns.CONTENT_URI, pocketcardCV);
            }

        }else{

            if(!matchLastStatus(context, responseModel)){
                ContentValues pocketcardCV = ResponseContentValues.getSingleContentValue(responseModel);
                context.getContentResolver().insert(ResponseColumns.CONTENT_URI, pocketcardCV);
            }

        }
    }

    public static boolean contains(Context context, ResponseModel responseModel){
        ResponseSelection where = new ResponseSelection();
        where.sha1(responseModel.sha1);
        Cursor cursor = where.query(context.getContentResolver());
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public static boolean matchLastStatus(Context context, ResponseModel responseModel){
        return matchLastStatusByType(context, responseModel, null);
    }

    public static boolean matchLastStatusByType(Context context, ResponseModel responseModel, String methodType){
        ResponseSelection where = new ResponseSelection();
        if(methodType != null)
            where.methodType(methodType);

        Cursor cursor = context.getContentResolver().query(
                ResponseColumns.CONTENT_URI,
                ResponseColumns.FULL_PROJECTION,
                where.sel(),
                where.args(),
                ResponseColumns.DEFAULT_ORDER + " DESC LIMIT 1");
        ResponseCursor responseCursor = new ResponseCursor(cursor);
        ResponseModel lastStatus = getSingleItem(responseCursor);

        return lastStatus != null && lastStatus.sha1.equals(responseModel.sha1);
    }

    public static List<ResponseModel> getList(ResponseCursor cursor) {
        List<ResponseModel> items = new ArrayList<ResponseModel>();
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                items.add(new ResponseModel(cursor));
                cursor.moveToNext();
            }
        }
        return items;
    }

    public static ResponseModel getSingleItem(ResponseCursor cursor) {
        ResponseModel item = null;
        if(cursor.moveToFirst()){
            item = new ResponseModel(cursor);
        }
        return item;
    }

    public static void deleteAll(Context context) {
        context.getContentResolver().delete(ResponseColumns.CONTENT_URI, null, null);
    }

    public static void delete(Context context, int sha1) {
        ResponseSelection where = new ResponseSelection();
        where.sha1(sha1);
        context.getContentResolver().delete(ResponseColumns.CONTENT_URI, where.sel(), where.args());
    }

    public static ResponseModel getFirstNonPostResponse(Context context){
        Cursor cursor = context.getContentResolver()
                .query(ResponseColumns.CONTENT_URI,
                        null, null, null, ResponseColumns.DEFAULT_ORDER + " DESC");

        ResponseCursor responseCursor = new ResponseCursor(cursor);

        if(responseCursor.moveToFirst()){
            while(!responseCursor.isAfterLast()){
                ResponseModel item = new ResponseModel(responseCursor);
                if(!item.methodType.equals(ResponseModel.POST)){

                    responseCursor.close();
                    return  item;

                }
                responseCursor.moveToNext();
            }
            responseCursor.close();
        }

        return null;
    }
}
