package android.ble.wcm.microchip.com.microchip.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;

import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;

/**
 * Created by fdoyle on 9/24/14
 */
public class ShareIntentBuilder {

    public static Intent buildShareIntent(Context context, ResponseModel responseModel) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlSummary(responseModel)));
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        i.setType("text/plain");
        return i;
    }

    private static String htmlSummary(ResponseModel responseModel){
        String html = String.format("<html><body><h4>%s: %s</h4>", responseModel.methodType, responseModel.url);

        html += "<strong>Body</strong><hr />";

        if (responseModel.responseStatus != null){
            html += "<strong>Response</strong><hr />";
            html += String.format("<p><code>%s</code></p>", JsonPrettyPrint.convert(responseModel.responseStatus));
        }

        if (responseModel.responseError != null){
            html += "<strong>Error</strong><hr />";
            html += String.format("<p>(%s) %s</p>", responseModel.statusCode, JsonPrettyPrint.convert(responseModel.responseError));
        }

        html += "<br /><br /></body></html>";
        return html;
    }
}
