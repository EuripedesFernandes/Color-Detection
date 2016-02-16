package com.example.colortest.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.colortest.MainActivity;
import com.mobilecontest.colordection.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

public class Utils {

	public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
	    int rotate = 0;
	    try {
	        context.getContentResolver().notifyChange(imageUri, null);
	        File imageFile = new File(imagePath);

	        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            rotate = 270;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            rotate = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            rotate = 90;
	            break;
	        }

	        Log.i("RotateImage", "Exif orientation: " + orientation);
	        Log.i("RotateImage", "Rotate value: " + rotate);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return rotate;
	}
	
	public static Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);              
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=80;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            o.inJustDecodeBounds = true;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
	
	public static Bitmap checkBitmap(Context context,Bitmap selectedImages,Uri selectedImage,String filePath,int layout_width,int layout_height){
		int rotateImage = Utils.getCameraPhotoOrientation(context, selectedImage, filePath);
		File photos= new File(filePath);
		selectedImages = decodeFile(photos);
		String nameFile = photos.getName();
		boolean isScreenshot = nameFile.startsWith("Screenshot");
		if(isScreenshot){
			Matrix matrix = new Matrix();
			matrix.postRotate(-90);
			selectedImages = Bitmap.createBitmap(selectedImages, 0, 0, selectedImages.getWidth(), selectedImages.getHeight(), matrix, true);
		}else if(rotateImage==270){
			Matrix matrix = new Matrix();
			matrix.postRotate(180);
			selectedImages = Bitmap.createBitmap(selectedImages, 0, 0, selectedImages.getWidth(), selectedImages.getHeight(), matrix, true);
		}
		selectedImages = Bitmap.createScaledBitmap(selectedImages,layout_width, layout_height, true);
		return selectedImages;
	}
	
	public static ShapeDrawable setShapeDrawable(int color,Context context){
		ShapeDrawable footerBackground = new ShapeDrawable();
		// The corners are ordered top-left, top-right, bottom-right,
		// bottom-left. For each corner, the array contains 2 values,
		// [X_radius,
		// Y_radius]
		float[] radii = new float[8];
		radii[0] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[1] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[2] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[3] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[4] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[5] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[6] = context.getResources().getDimension(R.dimen.footer_corners);
		radii[7] = context.getResources().getDimension(R.dimen.footer_corners);
		footerBackground.setShape(new RoundRectShape(radii, null, null));
		footerBackground.getPaint().setColor(color);
		return footerBackground;
	}
	
	public static String getBestMatchingColorName(int paramInt, Map<String, Integer> mColors) {
		int i = 765;
		Object localObject = null;
		int j = Color.red(paramInt);
		int k = Color.green(paramInt);
		int m = Color.blue(paramInt);
		Iterator localIterator = mColors.keySet().iterator();
		while (true) {
			if ((!localIterator.hasNext()) || (i <= 0))
				return (String) localObject;
			String str = (String) localIterator.next();
			int n = ((Integer) mColors.get(str)).intValue();
			int i1 = Color.red(n);
			int i2 = Color.green(n);
			int i3 = Color.blue(n);
			int i4 = Math.abs(j - i1) + Math.abs(k - i2) + Math.abs(m - i3);
			if (i <= i4)
				continue;
			i = i4;
			localObject = str;
		}
	}
	public static Map ColorInit () {
	    
	    Map<String, Integer> mColors = new HashMap();
        // RED
        mColors.put("Red", Integer.valueOf(Color.parseColor("#FF0000")));
        mColors.put("Red", Integer.valueOf(Color.parseColor("#EE0000")));
        mColors.put("Red", Integer.valueOf(Color.parseColor("#CD0000")));
        mColors.put("IndianRed", Integer.valueOf(Color.parseColor("#CD5C5C")));
        mColors.put("DarkRed", Integer.valueOf(Color.parseColor("#8B0000")));
        mColors.put("BrightRed", Integer.valueOf(Color.parseColor("#FF000D")));
        
        // GREEN
        mColors.put("Green", Integer.valueOf(Color.parseColor("#008000")));
        mColors.put("LightGreen", Integer.valueOf(Color.parseColor("#90EE90")));
        mColors.put("SpringGreen", Integer.valueOf(Color.parseColor("#00FF7F")));
        mColors.put("DarkGreen", Integer.valueOf(Color.parseColor("#006400")));
        mColors.put("LightGreen", Integer.valueOf(Color.parseColor("#90EE90")));
        mColors.put("GreenYellow", Integer.valueOf(Color.parseColor("#ADFF2F")));
        mColors.put("YellowGreen", Integer.valueOf(Color.parseColor("#9ACD32")));
        
        
        // Pink
        mColors.put("LightPink", Integer.valueOf(Color.parseColor("#FFB6C1")));
        mColors.put("HotPink", Integer.valueOf(Color.parseColor("#FF69B4")));
        mColors.put("DeepPink", Integer.valueOf(Color.parseColor("#FF1493")));
        mColors.put("Pink", Integer.valueOf(Color.parseColor("#FFC0CB")));
        
        // Blue
        mColors.put("DarkBlue", Integer.valueOf(Color.parseColor("#0000EE")));
        mColors.put("PowderBlue", Integer.valueOf(Color.parseColor("#B0E0E6")));
        mColors.put("SkyBlue", Integer.valueOf(Color.parseColor("#87CEEB")));
        mColors.put("DarkBlue", Integer.valueOf(Color.parseColor("#00008B")));
        mColors.put("Blue", Integer.valueOf(Color.parseColor("#0000FF")));
        mColors.put("SkyBlue", Integer.valueOf(Color.parseColor("#87CEEB")));
        mColors.put("LightBlue", Integer.valueOf(Color.parseColor("#ADD8E6")));
        
         // Yellow 
        mColors.put("Yellow", Integer.valueOf(Color.parseColor("#FFFF00")));
        mColors.put("GreenYellow", Integer.valueOf(Color.parseColor("#ADFF2F")));
        mColors.put("LightYellow", Integer.valueOf(Color.parseColor("#FFD700")));
        
        //  Chocolate
        mColors.put("Chocolate", Integer.valueOf(Color.parseColor("#D2691E")));
        mColors.put("Chocolate", Integer.valueOf(Color.parseColor("#FF7F24")));
        mColors.put("Chocolate", Integer.valueOf(Color.parseColor("#EE7621")));
        mColors.put("Chocolate", Integer.valueOf(Color.parseColor("#CD661D")));
        mColors.put("Chocolate", Integer.valueOf(Color.parseColor("#8B4513")));
        
        // orange
        mColors.put("Orange", Integer.valueOf(Color.parseColor("#FFA500")));
        mColors.put("Orange", Integer.valueOf(Color.parseColor("#EE9A00")));
        mColors.put("Orange", Integer.valueOf(Color.parseColor("#CD8500")));
        mColors.put("Orange", Integer.valueOf(Color.parseColor("#8B5A00")));
        mColors.put("DarkOrange", Integer.valueOf(Color.parseColor("#FF8C00")));
        mColors.put("DarkOrange", Integer.valueOf(Color.parseColor("#FF7F00")));
        mColors.put("DarkOrange", Integer.valueOf(Color.parseColor("#EE7600")));
        mColors.put("DarkOrange", Integer.valueOf(Color.parseColor("#CD6600")));
        mColors.put("OrangeRed", Integer.valueOf(Color.parseColor("#FF4500")));
        mColors.put("OrangeRed", Integer.valueOf(Color.parseColor("#EE4000")));
        mColors.put("OrangeRed", Integer.valueOf(Color.parseColor("#CD3700")));
        mColors.put("OrangeRed", Integer.valueOf(Color.parseColor("#8B2500")));
        
        // brown
        mColors.put("RosyBrown", Integer.valueOf(Color.parseColor("#B58D8D")));
        mColors.put("FallowBrown", Integer.valueOf(Color.parseColor("#C4996B")));
        mColors.put("CopperBrown", Integer.valueOf(Color.parseColor("#7A452A")));
        mColors.put("BoleBrown", Integer.valueOf(Color.parseColor("#784A40")));
        mColors.put("Brown", Integer.valueOf(Color.parseColor("#785433")));
        mColors.put("SaddleBrown", Integer.valueOf(Color.parseColor("#8B4513")));
        mColors.put("Brown", Integer.valueOf(Color.parseColor("#A52A2A")));
        
        
        mColors.put("Black", Integer.valueOf(Color.parseColor("#000000")));
        mColors.put("Black", Integer.valueOf(Color.parseColor("#0F0F0F")));
        
        // Grey
        mColors.put("DarkGrey", Integer.valueOf(Color.parseColor("#A9A9A9")));
        mColors.put("EmpressGrey", Integer.valueOf(Color.parseColor("#737373")));
        mColors.put("Grey", Integer.valueOf(Color.parseColor("#7D7D7D")));
        mColors.put("SlateGray", Integer.valueOf(Color.parseColor("#708090")));
        mColors.put("DimGray", Integer.valueOf(Color.parseColor("#696969")));
        
        mColors.put("GhostWhite", Integer.valueOf(Color.parseColor("#F8F8FF")));
        mColors.put("White", Integer.valueOf(Color.parseColor("#FFFFFF")));
        mColors.put("White", Integer.valueOf(Color.parseColor("#D3D3D3")));
        mColors.put("WhiteSmoke", Integer.valueOf(Color.parseColor("#F5F5F5")));
        mColors.put("LightWood", Integer.valueOf(Color.parseColor("#8A6667")));
        
        return  mColors;
    }
}
