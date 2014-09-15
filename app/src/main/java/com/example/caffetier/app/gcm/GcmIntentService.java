package com.example.caffetier.app.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.caffetier.app.OneCafeActivity;
import com.example.caffetier.app.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Aleksandar on 16-Aug-14.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String title;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            //saveMsgToFile(extras.toString());
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                title = extras.getString("title");
                int tableNumber = extras.getInt("table_number");
                String reservationConf = extras.getString("reservation_confirmation");
                String reservation = "";
                if (reservationConf.equals("YES")){
                    reservation = "PRIHVACENA REZERVACIJA ZA STO BROJ:" + tableNumber;
                }else{
                    reservation = "Nije Vam prihvacena rezervacija";
                }

                sendNotification(reservation);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

        // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Log.i(msg, "PORUKA");
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, OneCafeActivity.class), 0);
        /*
        Notification notification = new Notification();
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.notification_title, title);
        contentView.setTextViewText(R.id.notification_text, msg);
        notification.contentView = contentView;*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg).setSound(Uri.parse("android.resource://"+
                        getApplicationContext().getPackageName() + "/" + R.raw.fbpop)).setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        /*notification.sound = Uri.parse("android.resource://"+
                getApplicationContext().getPackageName() + "/" + R.raw.fbpop);
        notification.vibrate = new long[] {1000, 1000, 1000, 1000, 1000};*/


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

}
