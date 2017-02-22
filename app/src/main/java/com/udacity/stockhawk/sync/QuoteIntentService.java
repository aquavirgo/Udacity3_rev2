package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockHawkWidget;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.util.List;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    Handler mHandler;

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
        mHandler = new Handler();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");

        QuoteSyncJob qjs=new QuoteSyncJob(this);

         List isData = qjs.getQuotes(getApplicationContext());

        Boolean isStock = (Boolean) isData.get(0);
        Log.d("ISSTOCK", String.valueOf(isStock));
        if(isStock) {
//           new MainActivity().toastMaker(context);

        }else{

            String name= (String) isData.get(1);
         //   PrefUtils.removeStock(context,name);

            name=getString(R.string.toast_no_stock) +" "+ name;
            mHandler.post(new DisplayToast(this, name));
        }

      updateAllWidgets();

    }



    public class DisplayToast implements Runnable {
        private final Context mContext;
        String mText;

        public DisplayToast(Context mContext, String text){
            this.mContext = mContext;
            mText = text;
        }

        public void run(){
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }


    private void updateAllWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StockHawkWidget.class));
        if (appWidgetIds.length > 0) {
            new StockHawkWidget().onUpdate(this, appWidgetManager, appWidgetIds);
        }



        Intent intent = new Intent(this,StockHawkWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = {R.layout.widget_detail_list_item};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);


    }
}
