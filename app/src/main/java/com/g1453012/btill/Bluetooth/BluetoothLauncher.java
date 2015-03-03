package com.g1453012.btill.Bluetooth;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.g1453012.btill.UI.AppStartup;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by dlmiddlecote on 03/03/15.
 */
public class BluetoothLauncher extends Application implements BootstrapNotifier, BeaconConsumer {

    private static final String TAG = "BluetoothLauncherApp";
    private RegionBootstrap regionBootstrap;
    private Region mRegion;
    BeaconManager mBeaconManager;
    private BackgroundPowerSaver mPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        mPowerSaver = new BackgroundPowerSaver(this);
        mRegion = new Region(getPackageName(), null, null, null);
        regionBootstrap = new RegionBootstrap(this, mRegion);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        mBeaconManager.setBackgroundBetweenScanPeriod(2000);
        mBeaconManager.setForegroundBetweenScanPeriod(2000);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "did enter region.");
        regionBootstrap.disable();
        Intent intent = new Intent(BluetoothLauncher.this, AppStartup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        BluetoothLauncher.this.startActivity(intent);
    }

    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
