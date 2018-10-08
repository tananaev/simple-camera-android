package com.tananaev.simplecamera;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseCameraActivity extends AppCompatActivity {

    private static final String TAG = BaseCameraActivity.class.getSimpleName();

    public static final String KEY_IMAGE = "image";

    protected static final int MIN_WIDTH = 512;

    public static Intent createCameraIntent(Context context) {
        boolean legacy = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            legacy = true;
        } else {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                if (manager != null) {
                    for (String cameraId : manager.getCameraIdList()) {
                        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                        Integer deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        if (deviceLevel != null && deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                            legacy = true;
                        }
                    }
                }
            } catch (CameraAccessException | NullPointerException e) {
                Log.w(TAG, e);
            }
        }
        if (legacy) {
            return new Intent(context, CameraActivity.class);
        } else {
            return new Intent(context, Camera2Activity.class);
        }
    }

}
