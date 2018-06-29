package com.dryseed.dstracker;

import android.content.Context;
import android.widget.Toast;

final class NotificationService implements TimeCostInterceptor {

    private static final String TAG = "NotificationService";

    @Override
    public void onExceed(Context context, TimeCostInfo timeCostInfo) {
        Toast.makeText(context, "onExceed : " + timeCostInfo.getName(), Toast.LENGTH_SHORT).show();


//        Intent intent = new Intent(context, DisplayActivity.class);
//        intent.putExtra("show_latest", blockInfo.timeStart);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);
//        String contentTitle = context.getString(R.string.block_canary_class_has_blocked, blockInfo.timeStart);
//        String contentText = context.getString(R.string.block_canary_notification_message);
//        show(context, contentTitle, contentText, pendingIntent);
    }

   /* @TargetApi(HONEYCOMB)
    private void show(Context context, String contentTitle, String contentText, PendingIntent pendingIntent) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        if (SDK_INT < HONEYCOMB) {
            notification = new Notification();
            notification.icon = R.drawable.block_canary_notification;
            notification.when = System.currentTimeMillis();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND;
            try {
                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(notification, context, contentTitle, contentText, pendingIntent);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                Log.w(TAG, "Method not found", e);
            }
        } else {
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.block_canary_notification)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND);
            if (SDK_INT < JELLY_BEAN) {
                notification = builder.getNotification();
            } else {
                notification = builder.build();
            }
        }
        notificationManager.notify(0xDEAFBEEF, notification);
    }*/

}
