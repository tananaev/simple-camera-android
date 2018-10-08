package com.tananaev.simplecamera;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import java.io.IOException;

public class CameraActivity extends BaseCameraActivity implements
        View.OnClickListener, TextureView.SurfaceTextureListener, Camera.AutoFocusCallback, Camera.PictureCallback {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private TextureView mTextureView;

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mTextureView = findViewById(R.id.texture_view);

        findViewById(R.id.capture_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        if (mTextureView.isAvailable()) {
            onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(), 0, 0);
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    public void onClick(View v) {
        mCamera.autoFocus(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            Camera.Size bestSize = parameters.getPictureSize();
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                int min = Math.min(size.height, size.width);
                if (min >= MIN_WIDTH && min < Math.min(bestSize.height, bestSize.width)) {
                    bestSize = size;
                }
            }
            parameters.setPictureSize(bestSize.width, bestSize.height);

            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCamera.setParameters(parameters);

            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.w(TAG, e);
        }
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

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Intent intent = new Intent();
        intent.putExtra(KEY_IMAGE, bytes);
        setResult(RESULT_OK, intent);
        finish();
    }

}
