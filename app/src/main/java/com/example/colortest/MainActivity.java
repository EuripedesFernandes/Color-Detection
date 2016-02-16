package com.example.colortest;

import java.util.HashMap;
import java.util.Map;

import com.example.colortest.adapter.ArrayAdapterWithIcon;
import com.example.colortest.model.Utils;
import com.example.colortest.view.Cam_Preview;
import com.example.colortest.view.DrawingView;
import com.mobilecontest.colordection.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final int SELECT_GALLERY_IMAGE_CODE = 100;
	private Map<String, Integer> mColors;
	private Cam_Preview mCameraPreview;
	private DrawingView mDrawingView;
	private TextView mTextHexcolor;
	private TextView mTextNameColor;
	private TextView mTextRedcolor;
	private TextView mTextGreencolor;
	private TextView mTextBluecolor;
	private TextView mTextHcolor;
	private TextView mTextScolor;
	private TextView mTextVcolor;
	private ImageView mImageColor;
	private ImageView mImageImport;
	private ImageView mImageFlash;
	private ImageView mImagesSwitchCamera;

	private ProgressBar mProgressBarR;
	private ProgressBar mProgressBarG;
	private ProgressBar mProgressBarB;

	private boolean flashOn = false;
	private boolean MODE_LIVE_CAMERA = true;

	private int layout_width;
	private int layout_height;

	private int delayTime = 1;
	private Handler handler = new Handler();

	private ImageView mImageViewImport;
	Bitmap selectedImages;
	
	public float xTouch;
	public float yTouch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		getWindow().setFormat(-3);
		getWindow().setFlags(1024, 1024);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		layout_width = localDisplayMetrics.widthPixels;
		layout_height = localDisplayMetrics.heightPixels;

		mTextHexcolor = ((TextView) findViewById(R.id.tv_hexcolor));
		mTextNameColor = ((TextView) findViewById(R.id.tv_color_name));
		mTextRedcolor = ((TextView) findViewById(R.id.tv_red));
		mTextGreencolor = ((TextView) findViewById(R.id.tv_green));
		mTextBluecolor = ((TextView) findViewById(R.id.tv_blue));
		mTextHcolor = ((TextView) findViewById(R.id.tv_h));
		mTextScolor = ((TextView) findViewById(R.id.tv_s));
		mTextVcolor = ((TextView) findViewById(R.id.tv_v));
		mImageColor = ((ImageView) findViewById(R.id.image_display_color));

		mProgressBarR = (ProgressBar) findViewById(R.id.progress_red);
		mProgressBarG = (ProgressBar) findViewById(R.id.progress_green);
		mProgressBarB = (ProgressBar) findViewById(R.id.progress_blue);

		mDrawingView = new DrawingView(this);
		mCameraPreview = new Cam_Preview(this, mDrawingView);
		mColors = new HashMap();
		mColors = Utils.ColorInit();

		((FrameLayout) findViewById(R.id.preview)).addView(mCameraPreview);

		addContentView(mDrawingView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		LayoutInflater controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.control, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		mImageImport = ((ImageView) findViewById(R.id.im_import_file));
		mImageFlash = ((ImageView) findViewById(R.id.im_flash));
		mImagesSwitchCamera = ((ImageView) findViewById(R.id.im_switch_camera));

		handler.removeCallbacks(this.ColorDetect);
		handler.postDelayed(this.ColorDetect, this.delayTime);
		init_action();
	}

	private void init_action() {
		mImageImport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAlertDialog();
			}
		});
		mImageFlash.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				OnOffFlash();
			}
		});
		mImagesSwitchCamera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SwitchCamera();
			}
		});
	}

	public void OnOffFlash() {
		if (MODE_LIVE_CAMERA) {
			if (flashOn == false) {
				flashOn = true;
				mCameraPreview.setFlashOn(flashOn);
				return;
			}
			flashOn = false;
			mCameraPreview.setFlashOn(flashOn);
		}
	}

	public void SwitchCamera() {
		if (mCameraPreview != null && MODE_LIVE_CAMERA) {
			handler.removeCallbacks(ColorDetect);
			mCameraPreview.switchCamera(MainActivity.this);
			handler.postDelayed(ColorDetect, delayTime);
		}
	}

	public void showAlertDialog() {
		String names[] = { "Import Image", "Live Camera" };
		Integer[] icons = new Integer[] { R.drawable.ic_bar_gallery,
				R.drawable.ic_tab_camera_off, R.drawable.ic_launcher };
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		LayoutInflater inflater = getLayoutInflater();
		View convertView = (View) inflater.inflate(
				R.layout.listview_alertdialog, null);
		alertDialog.setView(convertView);
		alertDialog.setTitle("Choose Settings");
		ListView lv = (ListView) convertView.findViewById(R.id.listView);
		ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(
				getApplicationContext(), names, icons);
		lv.setAdapter(adapter);
		final AlertDialog optionDialog = alertDialog.create();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0: {
					Intent localIntent = new Intent(
							"android.intent.action.PICK");
					localIntent.setType("image/*");
					startActivityForResult(localIntent,
							SELECT_GALLERY_IMAGE_CODE);
					optionDialog.dismiss();
					break;
				}
				case 1: {
					if (MODE_LIVE_CAMERA == false) {
						if (mImageViewImport != null)
							((FrameLayout) findViewById(R.id.preview))
									.removeView(mImageViewImport);
						((FrameLayout) findViewById(R.id.preview))
								.addView(mCameraPreview);
						handler.removeCallbacks(ColorDetectImport);
						handler.removeCallbacks(ColorDetect);
						handler.postDelayed(ColorDetect, delayTime);
						MODE_LIVE_CAMERA = true;
					}
					optionDialog.dismiss();
					break;
				}
				}
			}
		});

		optionDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_GALLERY_IMAGE_CODE) {

				MODE_LIVE_CAMERA = false;
				Uri mUriImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(mUriImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				selectedImages = Utils.checkBitmap(getApplicationContext(),
						selectedImages, mUriImage, filePath, layout_width,
						layout_height);
				mImageViewImport = new ImageView(getApplicationContext());
				mImageViewImport.setImageBitmap(selectedImages);
				if (mCameraPreview != null)
					((FrameLayout) findViewById(R.id.preview))
							.removeView(mCameraPreview);
				((FrameLayout) findViewById(R.id.preview))
						.addView(mImageViewImport);
				this.handler.removeCallbacks(ColorDetect);
				handler.removeCallbacks(this.ColorDetectImport);
				handler.postDelayed(this.ColorDetectImport, this.delayTime);

			}
		}
	}

	private Runnable ColorDetect = new Runnable() {
		public void run() {
			int rG = mCameraPreview.getColorGreen();
			int rB = mCameraPreview.getColorBlue();
			int rR = mCameraPreview.getColorRed();
			showColor(rR, rG, rB);
			doFocus();
			handler.postDelayed(this, MainActivity.this.delayTime);
		}
	};

	private Runnable ColorDetectImport = new Runnable() {
		public void run() {

			int x = (int) mDrawingView.touch_x;
			int y = (int) mDrawingView.touch_y;
			int pixel = selectedImages.getPixel(x, y);
			int redValue = Color.red(pixel);
			int blueValue = Color.blue(pixel);
			int greenValue = Color.green(pixel);
			showColor(redValue, greenValue, blueValue);
			handler.postDelayed(this, MainActivity.this.delayTime);
		}
	};
	
	public void doFocus(){
		if(mDrawingView.isTouched){
			xTouch = mDrawingView.touch_x;
			yTouch = mDrawingView.touch_y;
			Rect touchRect = new Rect((int) (xTouch - 100),
					(int) (yTouch - 100), (int) (xTouch + 100),
					(int) (yTouch + 100));

			final Rect targetFocusRect = new Rect(touchRect.left * 2000
					/ layout_width - 1000, touchRect.top * 2000
					/ layout_height - 1000, touchRect.right * 2000
					/ layout_width - 1000, touchRect.bottom * 2000
					/ layout_height - 1000);
			mCameraPreview.doTouchFocus(targetFocusRect);
		}
	}

	public void showColor(int redValue, int greenValue, int blueValue) {
		int j = Color.rgb(redValue, greenValue, blueValue);
		String name = Utils.getBestMatchingColorName(j, mColors);
		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = redValue;
		arrayOfObject[1] = greenValue;
		arrayOfObject[2] = blueValue;
		String hex = String.format("#%02x%02x%02x", arrayOfObject)
				.toUpperCase();
		mTextRedcolor.setText("RED: " + redValue);
		mTextGreencolor.setText("GREEN: " + blueValue);
		mTextBluecolor.setText("BLUE: " + greenValue);
		mTextHexcolor.setText(String.format("Hex:%s", hex));
		mTextNameColor.setText(name);
		mImageColor.setBackgroundDrawable(Utils.setShapeDrawable(j,
				getApplicationContext()));
		float[] pixelHSV = new float[3];
		Color.RGBToHSV(redValue, greenValue, blueValue, pixelHSV);
		mTextHcolor.setText("H: " + (int) pixelHSV[0] + "�");
		mTextScolor.setText("S: " + (int) (pixelHSV[1] * 100) + "%");
		mTextVcolor.setText("V: " + (int) (pixelHSV[2] * 100) + "%");
		mProgressBarR.setProgress(redValue);
		mProgressBarG.setProgress(greenValue);
		mProgressBarB.setProgress(blueValue);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (MODE_LIVE_CAMERA) {
			handler.removeCallbacks(ColorDetect);
		} else {
			handler.removeCallbacks(ColorDetectImport);
		}
		super.onDestroy();
	}
}
