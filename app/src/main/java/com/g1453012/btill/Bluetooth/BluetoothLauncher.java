package com.g1453012.btill.Bluetooth;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.g1453012.btill.R;
import com.g1453012.btill.UI.AppStartup;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

/**
 * Created by dlmiddlecote on 03/03/15.
 */
public class BluetoothLauncher extends Application implements BootstrapNotifier, RangeNotifier {

    private static final String TAG = "BluetoothLauncherApp";
    private RegionBootstrap mRegionBootstrap;
    private Region mRegion;
    BeaconManager mBeaconManager;
    private BackgroundPowerSaver mPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();

        mRegion = new Region(getPackageName(), null, null, null);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mPowerSaver = new BackgroundPowerSaver(this);
        mRegionBootstrap = new RegionBootstrap(this, mRegion);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //backgroundPowerSaver = new BackgroundPowerSaver(this);
        // TODO remove this small time (5 secs) (This is just for testing)
        mBeaconManager.setBackgroundBetweenScanPeriod(5000);

        /*mPowerSaver = new BackgroundPowerSaver(this);
        mRegion = new Region(getPackageName(), null, null, null);
        regionBootstrap = new RegionBootstrap(this, mRegion);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        mBeaconManager.setBackgroundBetweenScanPeriod(2000);
        mBeaconManager.setForegroundBetweenScanPeriod(2000);*/
    }

    @Override
    public void didEnterRegion(Region region) {
       try
        {
            Log.d(TAG, "entered region.  starting ranging");
            mBeaconManager.setRangeNotifier(this);
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
        }
        catch(RemoteException e)
        {
            Log.e(TAG, "Cannot start ranging");
        }
        /*
        Intent intent = new Intent(BluetoothLauncher.this, AppStartup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        BluetoothLauncher.this.startActivity(intent);*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("B-Till")
                        .setContentText("Imperial is nearby!")
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AppStartup.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AppStartup.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(10, mBuilder.build());

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

    }
}
