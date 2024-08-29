package com.steelkiwi.cropiwa.sample.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

/**
 * Created by FelipeGAlmeida. https://github.com/FelipeGAlmeida
 * on 08.28.2024.
 */
public class PermissionUtil {

    /* Request constants */
    private static final int REQUEST_PERMISSION = 1001;

    private static OnResultListener mListener;

    public interface OnResultListener {
        void onSuccess();
    }

    /***
     * Need to be called where some action that requires the user's permission will be performed.
     *
     * @param act the activity
     * @param permission the permission that is going to be asked to the user
     * @param listener a listener which the result will be dispatch to.
     *                 The onSuccess' body must handle the action when the permission is granted.
     */
    public static void handlePermission(Activity act, String permission, OnResultListener listener) {
        mListener = listener;

        if (shouldAskPermission(act.getApplicationContext(), permission)) {
            ActivityCompat.requestPermissions(act, new String[] {permission}, REQUEST_PERMISSION);
        } else {
            mListener.onSuccess();
        }
    }

    /***
     * Need to be called where some action that requires the user's permission will be performed.
     *
     * @param act the activity
     * @param permissions array with the permissions that is going to be asked to the user
     * @param listener a listener which the result will be dispatch to.
     *                 The onSuccess' body must handle the action when the permissions are granted.
     */
    public static void handlePermissions(Activity act, String[] permissions, OnResultListener listener) {
        mListener = listener;
        for (int i = 0; i < permissions.length; i++) {
            if (shouldAskPermission(act.getApplicationContext(), permissions[i])) {
                ActivityCompat.requestPermissions(act, permissions, REQUEST_PERMISSION);
                return;
            }
        }
        mListener.onSuccess();
    }

    /***
     * Need to be called on onRequestPermissionsResult method.
     *
     * @param act the activity
     * @param requestCode the request code that came as parameter from onRequestPermissionsResult method
     * @param permission the permission code that came as parameter from onRequestPermissionsResult method
     */
    public static void processResult(Activity act, int requestCode, String permission) {

        if (requestCode != REQUEST_PERMISSION) {
            return;
        }

        PermissionStatus permissionStatus = getPermissionStatus(act, permission);

        switch (permissionStatus) {
            case GRANTED:
                if (mListener != null) {
                    mListener.onSuccess();
                }
                break;

            case DENIED:

                break;

            case NEVER:
                displayAlertPermissionDenied(act, permission);
                break;
        }
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    private static void displayAlertPermissionDenied(Activity act, String permission) {
        Toast.makeText(act, "You've not been granted the necessary permissions, please, review the app permissions", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + act.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        act.startActivityForResult(intent, REQUEST_PERMISSION);
    }

    public enum PermissionStatus {
        GRANTED(PackageManager.PERMISSION_GRANTED),
        DENIED(PackageManager.PERMISSION_DENIED),
        NEVER(2);

        private int value;

        PermissionStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /***
     * Returns the current status of a given permission
     *
     * @param act the activity
     * @param permission the permission to know the status
     * @return PermissionStatus.GRANTED, PermissionStatus.DENIED or PermissionStatus.NEVER.
     */
    public static PermissionStatus getPermissionStatus(Activity act, String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(act, permission)) {
            return PermissionStatus.DENIED;
        } else {
            if (ActivityCompat.checkSelfPermission(act, permission) == PackageManager.PERMISSION_GRANTED) {
                return PermissionStatus.GRANTED;
            } else {
                return PermissionStatus.DENIED;
            }
        }
    }
}
