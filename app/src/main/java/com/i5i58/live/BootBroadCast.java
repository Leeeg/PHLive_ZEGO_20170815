package com.i5i58.live;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lee on 2017/7/25.
 */

public class BootBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent mintent) {

//        if (Intent.ACTION_BOOT_COMPLETED.equals(mintent.getAction())) {
//            // 启动完成
//            Intent intent = new Intent(context, Alarmreceiver.class);
//            intent.setAction("arui.alarm.action");
//            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//            long firstime = SystemClock.elapsedRealtime();
//            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//            // 10秒一个周期，不停的发送广播
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
//                    10 * 1000, sender);
//        }

    }
}
