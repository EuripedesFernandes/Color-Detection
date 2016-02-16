package com.example.colortest.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.colortest.model.ImageProcess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("NewApi")
public class Cam_Preview extends SurfaceView implements SurfaceHolder.Callback {
	
	public SurfaceHolder mHolder;
	public DrawingView mDrawingView;

	public Camera camera;
	private int currentCameraId;
	private boolean BACK_CAM = true;
	
	private Bitmap mTemBitmap;
	private int[] Drgb;
	private int mColor;
	public int rB;
	public int rG;
	public int rR;
	public String rHex;

	public int layout_height;
	public int layout_width;
	public float xTouch;
	public float yTouch;

	public Cam_Preview(Context paramContext, DrawingView mDrawingView) {
		super(paramContext);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		layout_width = localDisplayMetrics.widthPixels;
		layout_height = localDisplayMetrics.heightPixels;
		this.mDrawingView = mDrawingView;

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public Camera getCamera() {
		return camera;
	}

	public int getColorGreen() {
		return rG;
	}

	public int getColorBlue() {
		return rB;
	}

	public int getColorRed() {
		return rR;
	}

	public String getHex() {
		return rHex;
	}

	Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
		}
	};

	public void doTouchFocus(final Rect tfocusRect) {
		try {
			final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
			Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
			focusList.add(focusArea);

			Camera.Parameters para = camera.getParameters();
			para.setFocusAreas(focusList);
			para.setMeteringAreas(focusList);
			camera.setParameters(para);

			camera.autoFocus(autoFocusCB);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Camera.PreviewCallback mPreviewCallBack = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {
				if (mTemBitmap != null) {

					if (BACK_CAM) {
						xTouch = mDrawingView.touch_x;
						yTouch = mDrawingView.touch_y;
					} else {
						xTouch = layout_width - mDrawingView.touch_x;
						yTouch = mDrawingView.touch_y;
					}

					mColor = mTemBitmap.getPixel((int) xTouch, (int) yTouch);
					rR = Color.red(mColor);
					rG = Color.green(mColor);
					rB = Color.blue(mColor);
					Object[] arrayOfObject = new Object[3];
					arrayOfObject[0] = Integer.valueOf(rR);
					arrayOfObject[1] = Integer.valueOf(rG);
					arrayOfObject[2] = Integer.valueOf(rB);
					rHex = String.format("#%02x%02x%02x", arrayOfObject)
							.toUpperCase();
				}
				invalidate();
				Drgb = new int[data.length];
				ImageProcess.decodeYUV420SP(Drgb, data, layout_width,
						layout_height);
				mTemBitmap = Bitmap.createBitmap(Drgb, layout_width,
						layout_height, Bitmap.Config.RGB_565);

			} catch (Exception localException) {
				while (true) {
					mTemBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);
				}
			}
		}
	};

	public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(paramSurfaceHolder);
//			 camera.autoFocus(autoFocusCB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.setPreviewCallback(mPreviewCallBack);
	}

	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
			int paramInt2, int paramInt3) {
		if (paramSurfaceHolder.getSurface() == null) {
			return;
		}
		camera.stopPreview();

		try {
			camera.setPreviewDisplay(paramSurfaceHolder);
			camera.setPreviewCallback(mPreviewCallBack);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setFlashOn(boolean mode) {
		if (camera != null
				&& currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
			if (mode == true) {
				Parameters parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameters);
				return;
			}
			Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameters);
		}

	}

	public void switchCamera(Activity activity) {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
		}
		// NB: if you don't release the current camera before switching, you app
		// will crash
		camera.release();
		camera = null;
		// swap the id of the camera to be used
		if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			BACK_CAM = false;
		} else {
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			BACK_CAM = true;
		}
		camera = Camera.open(currentCameraId);

		setCameraDisplayOrientation(activity, currentCameraId, camera);
		try {
			camera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.setPreviewCallback(mPreviewCallBack);
		camera.startPreview();
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}
}