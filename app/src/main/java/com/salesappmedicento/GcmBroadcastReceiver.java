package com.salesappmedicento;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("data", "onReceive: " );
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                MyFireBaseInstanceService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        setResultCode(Activity.RESULT_OK);
    }
}
