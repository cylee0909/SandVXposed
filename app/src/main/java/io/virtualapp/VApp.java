package io.virtualapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.utils.OSUtils;
import com.lody.virtual.sandxposed.SandXposed;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.trend.lazyinject.buildmap.Auto_ComponentBuildMap;
import com.trend.lazyinject.lib.LazyInject;
import com.trend.lazyinject.lib.utils.ProcessUtils;

import java.util.List;

import io.virtualapp.delegate.MyAppRequestListener;
import io.virtualapp.delegate.MyComponentDelegate;
import io.virtualapp.delegate.MyPhoneInfoDelegate;
import io.virtualapp.delegate.MyTaskDescriptionDelegate;
import jonathanfinerty.once.Once;

/**
 * @author Lody
 */
public class VApp extends MultiDexApplication {

    private static VApp gApp;
    private SharedPreferences mPreferences;

    public static VApp getApp() {
        return gApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SandXposed.init(base);
        mPreferences = base.getSharedPreferences("va", Context.MODE_MULTI_PROCESS);
        VASettings.ENABLE_IO_REDIRECT = true;
        VASettings.ENABLE_INNER_SHORTCUT = false;
        try {
            VirtualCore.get().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        gApp = this;
        super.onCreate();
        lazyInjectInit();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {

            @Override
            public void onMainProcess() {
                Once.initialise(VApp.this);
                new FlurryAgent.Builder()
                        .withLogEnabled(true)
                        .withListener(() -> {
                            // nothing
                        })
                        .build(VApp.this, "48RJJP7ZCZZBB6KMMWW5");
            }

            @Override
            public void onVirtualProcess() {
                List<PackageInfo> packageInfos =  VirtualCore.get().getUnHookPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
                if (packageInfos != null) {
                    for (int i = 0; i < packageInfos.size(); i++) {
                        PackageInfo info = packageInfos.get(i);
                        ApplicationInfo ai = info.applicationInfo;
                        if (ai.metaData != null && ai.metaData.containsKey("xposedmodule")) {
                            VirtualCore.get().addVisibleOutsidePackage(info.packageName);
                        }
                    }
                }
                L.v("virtual process : " + ProcessUtils.getProcessName(gApp));
                //listener components
                virtualCore.setComponentDelegate(new MyComponentDelegate());
                //fake phone imei,macAddress,BluetoothAddress
                virtualCore.setPhoneInfoDelegate(new MyPhoneInfoDelegate());
                //fake task description's icon and title
                virtualCore.setTaskDescriptionDelegate(new MyTaskDescriptionDelegate());
            }

            @Override
            public void onServerProcess() {
                virtualCore.setAppRequestListener(new MyAppRequestListener(VApp.this));
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqq");
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqqi");
                virtualCore.addVisibleOutsidePackage("com.tencent.minihd.qq");
                virtualCore.addVisibleOutsidePackage("com.tencent.qqlite");
                virtualCore.addVisibleOutsidePackage("com.facebook.katana");
                virtualCore.addVisibleOutsidePackage("com.whatsapp");
                virtualCore.addVisibleOutsidePackage("com.tencent.mm");
                virtualCore.addVisibleOutsidePackage("com.immomo.momo");
            }
        });
    }

    public static SharedPreferences getPreferences() {
        return getApp().mPreferences;
    }

    private void lazyInjectInit() {
        LazyInject.init(this);
        LazyInject.addBuildMap(Auto_ComponentBuildMap.class);
    }

}
