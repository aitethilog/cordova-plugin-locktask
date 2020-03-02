package com.oddmouse.plugins;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public class LockTask extends CordovaPlugin {

  private static final String ACTION_START_LOCK_TASK = "startLockTask";
  private static final String ACTION_STOP_LOCK_TASK = "stopLockTask";

  private Activity activity = null;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    activity = cordova.getActivity();
    ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
    String adminClassName = "com.mama.deviceadmin.CordovaDeviceAdminReceiver";

	/*
    if (args.length() > 0) {
      adminClassName = args.getString(0);
    }
	*/

    try {
      if (ACTION_START_LOCK_TASK.equals(action)) {

        if (!activityManager.isInLockTaskMode()) {

          if (!adminClassName.isEmpty()) {

            DevicePolicyManager mDPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName mDeviceAdmin = new ComponentName(activity.getPackageName(), adminClassName);

            if (mDPM.isDeviceOwnerApp(activity.getPackageName())) {
              String[] packages = {activity.getPackageName()};
              mDPM.setLockTaskPackages(mDeviceAdmin, packages);
			  
			  activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			  activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

          }

          activity.startLockTask();
        }

        callbackContext.success();

        return true;

      } else if (ACTION_STOP_LOCK_TASK.equals(action)) {

        if (activityManager.isInLockTaskMode()) {
          activity.stopLockTask();
        }

        callbackContext.success();
        return true;

      } else {

        callbackContext.error("The method '" + action + "' does not exist.");
        return false;

      }
    } catch (Exception e) {

      callbackContext.error(e.getMessage());
      return false;

    }
  }
}
