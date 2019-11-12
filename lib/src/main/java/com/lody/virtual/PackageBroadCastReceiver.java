package com.lody.virtual;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;

import io.virtualapp.L;

public class PackageBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        L.v("PackageBroadCastReceiver !"+intent.getAction());
        if (TextUtils.equals(intent.getAction(),Intent.ACTION_PACKAGE_REPLACED)){
            // 获取应用包名
            String packName = intent.getData().getSchemeSpecificPart();
            try {
                ApplicationInfo applicationInfo = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(packName,0);
                VirtualCore.get().installPackage(applicationInfo.sourceDir, InstallStrategy.DEPEND_SYSTEM_IF_EXIST | InstallStrategy.UPDATE_IF_EXIST);
                L.v("receive package "+ packName+" path "+applicationInfo.sourceDir+" changed!");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
