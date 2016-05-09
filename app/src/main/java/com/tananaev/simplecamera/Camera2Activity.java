package com.tananaev.simplecamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AppCompatActivity implements
        View.OnClickListener, TextureView.SurfaceTextureListener {

    private static final String TAG = Camera2Activity.class.getSimpleName();

    private TextureView mTextureView;
    private ImageView mCaptureButton;

    private Surface mSurface;

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mTextureView = (TextureView) findViewById(R.id.texture_view);
        mCaptureButton = (ImageView) findViewById(R.id.capture_button);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = mCameraManager.getCameraIdList()[0];
            mCameraManager.openCamera(cameraId, cameraDeviceStateCallback, null);
        } catch (CameraAccessException e) {
            Log.w(TAG, e);
        }

        try {
            mTextureView.setSurfaceTextureListener(this);
            mCaptureButton.setOnClickListener(this);
        } catch (RuntimeException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
    }

    @Override
    public void onClick(View v) {
        mCaptureButton.setEnabled(false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        startCameraPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    private CameraCaptureSession.StateCallback cameraSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(mSurface);
                CaptureRequest captureRequest = captureRequestBuilder.build();
                session.setRepeatingRequest(captureRequest, cameraSessionCaptureCallback, null);
            } catch (CameraAccessException e) {
                Log.w(TAG, e);
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    private CameraCaptureSession.CaptureCallback cameraSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
    };

    private void startCameraPreview() {
        if (mSurface != null && mCameraDevice != null) {
            try {
                mCameraDevice.createCaptureSession(Arrays.asList(mSurface), cameraSessionStateCallback, null);
            } catch (CameraAccessException e) {
                Log.w(TAG, e);
            }
        }
    }

}
