package com.udacity.stockhawk;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockProvider;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.R.attr.description;

/**
 * Created by Jakub on 2017-02-21.
 */

public class WRemoteService extends RemoteViewsService {

    static final int INDEX_QUOTE_ID = 0;
    static final int INDEX_QUOTE_SYMBOL = 1;
    static final int INDEX_QUOTE_BIDPRICE = 2;
    static final int INDEX_QUOTE_PERCENT_CHANGE = 4;

        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new RemoteViewsFactory() {
                private Cursor data = null;

                @Override
                public void onCreate() {
                    // Nothing to do
                }
                @Override
                public void onDataSetChanged() {
                    if (data != null) {
                        data.close();

                    }
                    // This method is called by the app hosting the widget (e.g., the launcher)
                    // However, our ContentProvider is not exported so it doesn't have access to the
                    // data. Therefore we need to clear (and finally restore) the calling identity so
                    // that calls use our process and permission
                    final long identityToken = Binder.clearCallingIdentity();

                 //QuoteSyncJob.syncImmediately(getApplicationContext());
                   // Intent nowIntent = new Intent(getApplicationContext(), QuoteIntentService.class);
                   // getApplicationContext().startService(nowIntent);

                    data = getContentResolver().query(Contract.Quote.URI,
                            Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                            null, null, Contract.Quote.COLUMN_SYMBOL);


                    Binder.restoreCallingIdentity(identityToken);
                }

                @Override
                public void onDestroy() {
                    if (data != null) {
                        data.close();
                        data = null;
                    }
                }

                @Override
                public int getCount() {
                    return data == null ? 0 : data.getCount();
                }

                @Override
                public RemoteViews getViewAt(int position) {
                    if (position == AdapterView.INVALID_POSITION ||
                            data == null || !data.moveToPosition(position)) {
                        return null;
                    }
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.widget_detail_list_item);

                    data.moveToPosition(position);
                    views.setTextViewText(R.id.stock_symbol, data.getString(INDEX_QUOTE_SYMBOL));

                   DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                    dollarFormatWithPlus.setPositivePrefix("+$");
                    DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                    percentageFormat.setMaximumFractionDigits(2);
                    percentageFormat.setMinimumFractionDigits(2);
                    percentageFormat.setPositivePrefix("+");

                    double xxx= Double.parseDouble(data.getString(INDEX_QUOTE_BIDPRICE));
double yyy= Double.parseDouble(data.getString(INDEX_QUOTE_PERCENT_CHANGE))/100;


                    views.setTextViewText(R.id.bid_price, dollarFormat.format(xxx));
                    views.setTextViewText(R.id.change,percentageFormat.format(yyy));
                    final Intent fillInIntent = new Intent();

                    Uri quoteUri = Contract.Quote.URI;
                    fillInIntent.setData(quoteUri);
                    fillInIntent.putExtra("stock_position",position);
                    views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
                    return views;
                }



                @Override
                public RemoteViews getLoadingView() {
                    return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                }

                @Override
                public int getViewTypeCount() {
                    return 1;
                }

                @Override
                public long getItemId(int position) {
                    if (data.moveToPosition(position))
                        return data.getLong(INDEX_QUOTE_ID);
                    return position;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            };
    }
}
