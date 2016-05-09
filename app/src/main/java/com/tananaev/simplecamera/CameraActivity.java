package com.tananaev.simplecamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements
        View.OnClickListener, TextureView.SurfaceTextureListener, Camera.AutoFocusCallback {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private TextureView mTextureView;
    private ImageView mCaptureButton;

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mTextureView = (TextureView) findViewById(R.id.texture_view);
        mCaptureButton = (ImageView) findViewById(R.id.capture_button);

        try {
            mCamera = Camera.open();
            mTextureView.setSurfaceTextureListener(this);
            mCaptureButton.setOnClickListener(this);
        } catch (RuntimeException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    public void onClick(View v) {
        mCamera.autoFocus(this);
        mCaptureButton.setEnabled(false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            parameters.setPreviewSize(width, height);
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
        Log.i(TAG, "onAutoFocus");

    }

}
