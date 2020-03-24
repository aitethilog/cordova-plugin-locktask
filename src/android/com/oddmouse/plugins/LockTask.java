package com.oddmouse.plugins;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.View;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class LockTask extends CordovaPlugin {

  private static final String ACTION_START_LOCK_TASK = "startLockTask";
  private static final String ACTION_STOP_LOCK_TASK = "stopLockTask";
  private static final String ACTION_IS_IN_KIOSK = "isInKiosk";
  private static final String ACTION_REMOVE_DEVICE_OWNER = "removeDeviceOwner";

  public static final String IS_SET_AS_LAUNCHER = "isSetAsLauncher";

  private Activity activity = null;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
	  if (ACTION_IS_IN_KIOSK.equals(action)) {
      activity = cordova.getActivity();
      ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		  callbackContext.success(Boolean.toString(activityManager.isInLockTaskMode()));
		  return true;

	  } else if (ACTION_START_LOCK_TASK.equals(action)) {
	      customStartLockTask();
        callbackContext.success();
        return true;

      } else if (ACTION_STOP_LOCK_TASK.equals(action)) {
      activity = cordova.getActivity();
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager.isInLockTaskMode()) {
          activity.stopLockTask();
        }

        callbackContext.success();
        return true;

      } else if (ACTION_REMOVE_DEVICE_OWNER.equals(action)) {
        DevicePolicyManager mDPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.clearDeviceOwnerApp(activity.getPackageName());

        callbackContext.success();
        return true;

      } else if (ACTION_REMOVE_DEVICE_OWNER.equals(action)) {
        String myPackage = cordova.getActivity().getApplicationContext().getPackageName();
        callbackContext.success(Boolean.toString(myPackage.equals(findLauncherPackageName())));
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

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		customStartLockTask();
		hideSystemUI();
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		hideSystemUI();
	}

	private void hideSystemUI() {
        View mDecorView = cordova.getActivity().getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                cordova.getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
    }

    private void customStartLockTask() {
      activity = cordova.getActivity();
      ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
      String adminClassName = "com.mama.deviceadmin.CordovaDeviceAdminReceiver";

      if (!activityManager.isInLockTaskMode()) {
        if (!adminClassName.isEmpty()) {
          DevicePolicyManager mDPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
          ComponentName mDeviceAdmin = new ComponentName(activity.getPackageName(), adminClassName);

          if (mDPM.isDeviceOwnerApp(activity.getPackageName())) {
            String[] packages = {activity.getPackageName()};
            mDPM.setLockTaskPackages(mDeviceAdmin, packages);
          }
        }

        activity.startLockTask();
      }
    }

    private String findLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = this.cordova.getActivity().getPackageManager().resolveActivity(intent, 0);
        return res.activityInfo.packageName;
    }
}
