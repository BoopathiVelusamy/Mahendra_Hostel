package boopathi.app.mahendrahostel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start your service here
            Intent serviceIntent = new Intent(context, MyService.class);
            ContextCompat.startForegroundService(context,serviceIntent);
        }
    }
}
