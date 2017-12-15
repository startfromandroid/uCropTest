package com.yongle.ucroptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtils {
	public static final int REQUE_CODE_CAMERA = 100;
	public static final int REQUE_CODE_PHOTO = 101;
	public static final int REQUE_CODE_CROP = 103;

	/**
	 * 平铺图片
	 *
	 * @param imageView
	 * @param src
	 * @return
	 */
	public static void tileImage(final ImageView imageView, final Bitmap src){
		ViewTreeObserver vto1 = imageView.getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setImageBitmap(ImageUtils.createRepeater(imageView.getWidth(),src));
			}
		});
	}
	public static Bitmap createRepeaters(int width, Bitmap src) {
		int count = width / src.getWidth();

		Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		for (int idx = 0; idx < count; ++idx) {
			canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
		}
		return bitmap;
	}

	/**
	 * 平铺图片
	 * 
	 * @param width
	 * @param src
	 * @return
	 */
	public static Bitmap createRepeater(int width, Bitmap src) {
		int count = (width + src.getWidth() - 1) / src.getWidth();

		Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		for (int idx = 0; idx < count; ++idx) {
			canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
		}
		return bitmap;
	}

	public static void startPhotoZoom(Context context, Uri uri,
			int REQUE_CODE_CROP) {
		int dp = 500;

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);// 输出是X方向的比例
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
		intent.putExtra("outputX", dp);// 输出X方向的像素
		intent.putExtra("outputY", dp);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);// 设置为不返回数据
		((Activity) context).startActivityForResult(intent, REQUE_CODE_CROP);
	}
	public final static String photoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ylpw/photo";
	public final static String takePhotoName = "takePhoto.jpeg";
	//打开照相机
	public static File getImageFromCamer(Context context,
			int REQUE_CODE_CAMERA) {
		// 保存裁剪后的图片文件
		File pictureFileDir = new File(photoPath);
		if (!pictureFileDir.exists()) {
			pictureFileDir.mkdirs();
		}
		File picFile = new File(pictureFileDir, takePhotoName);
		if (!picFile.exists()) {
			try {
				picFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Uri photoUri = Uri.fromFile(picFile);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		File fileDir = HelpUtil.getFile(context, "/Tour/user_photos");
//		cameraFile = new File(fileDir.getAbsoluteFile() + "/"
//				+ System.currentTimeMillis() + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
		((Activity) context).startActivityForResult(intent, REQUE_CODE_CAMERA);
		return picFile;
	}

	//打开相册
	public static void getImageFromPhoto(Context context, int REQUE_CODE_PHOTO) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		((Activity) context).startActivityForResult(intent, REQUE_CODE_PHOTO);
	}

	public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					mContext.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据图片的url路径获得Bitmap对象
	 * @param url
	 * @return
	 */
	public static Bitmap returnBitmap(String url) {
		URL fileUrl = null;
		Bitmap bitmap = null;

		try {
			fileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		try {
			HttpURLConnection conn = (HttpURLConnection) fileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;

	}
}
