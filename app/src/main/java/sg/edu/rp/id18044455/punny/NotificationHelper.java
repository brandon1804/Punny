package sg.edu.rp.id18044455.punny;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.util.Random;


class NotificationHelper {

    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private Random rand;
    private Providers provider;
    private int randomPunIndex;
    private String randomPun;


    NotificationHelper(Context context) {
        mContext = context;
    }


    void createNotification() {


        provider = new Providers();
        rand = new Random();
        randomPunIndex = rand.nextInt(provider.getProvidedPunsLength());
        randomPun = provider.getProvidedPuns()[randomPunIndex];

        Intent intent = new Intent(mContext , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        mBuilder.setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(randomPun)
                );

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }//end of if

        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());


    }//end of createNotification


}//end of class
