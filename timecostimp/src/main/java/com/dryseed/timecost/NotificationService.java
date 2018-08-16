package com.dryseed.timecost;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.ui.TimeCostInfoListActivity;
import com.dryseed.timecost.utils.DebugLog;
import com.dryseed.timecostimpl.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.O;

/**
 * @author caiminming
 */
final class NotificationService implements TimeCostInterceptor {

    private static final String TAG = "TimeCostNotificationService";

    @Override
    public void onExceed(Context context, TimeCostInfo timeCostInfo) {
        if (TimeCostCanary.get().getConfig().isShowDetailUI()) {
            //DebugLog.d(TAG, "onExceed : " + timeCostInfo);
            Intent intent = new Intent(context, TimeCostInfoListActivity.class);
            //intent.putExtra("show_latest", blockInfo.timeStart);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);
            String contentTitle = context.getString(
                    R.string.time_cost_canary_class_has_blocked,
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getExceedMilliTime()
            );
            String contentText = timeCostInfo.getName();//context.getString(R.string.time_cost_canary_notification_message);
            show(context, contentTitle, contentText, pendingIntent);
        }
    }

    @TargetApi(HONEYCOMB)
    private void show(Context context, String contentTitle, String contentText, PendingIntent pendingIntent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "time_cost_channel_id";
        String channelDescription = "Time Cost Channel";
        //Check if notification channel exists and if not create one
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Notification notification;
        if (SDK_INT < HONEYCOMB) {
            notification = new Notification();
            notification.icon = R.drawable.time_cost_canary_notification;
            notification.when = System.currentTimeMillis();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND;
            try {
                Method deprecatedMethod = notification.getClass().getMethod(
                        "setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class
                );
                deprecatedMethod.invoke(notification, context, contentTitle, contentText, pendingIntent);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                DebugLog.e(TAG, "Method not found", e);
            }
        } else {
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.time_cost_canary_notification)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND);
            if (SDK_INT >= O) {
                builder.setChannelId(channelId);
            }

            if (SDK_INT < JELLY_BEAN) {
                notification = builder.getNotification();
            } else {
                notification = builder.build();
            }
        }
        notificationManager.notify(0xDEAFBEEF, notification);
    }

}
