package com.androidnative.features;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.androidnative.AN_Bridge;
import com.androidnative.AndroidNativeProxy;
import com.androidnative.utils.Base64;
import com.androidnative.utils.NativeUtility;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.unity3d.player.UnityPlayer;

@SuppressLint("SimpleDateFormat")
public class CameraAPI implements ImageChooserListener {

	public static final int RESULT_IMAGE_CAPTURE = 2930;
	private static final String CAMERA_SERVICE_LISTNER_NAME = "AndroidCamera";

	private static ImageChooserManager imageChooserManager;
	private static CameraAPI _instance = null;

	private static boolean SaveCameraImageToGallery = false;
	private static boolean TakeFullSizePhoto = true;
	private static String GalleryFolderName = null;
	private static int MaxImageAllowedSize = 1024;
	private static int ImageFormat = 0;

	ArrayList<String> GalleryList = new ArrayList<String>();

	public static CameraAPI GetInstance() {
		if (_instance == null) {
			_instance = new CameraAPI();
		}

		return _instance;

	}

	public void Init(String folderName, int maxSize, int mode, int format) {
		GalleryFolderName = folderName;
		MaxImageAllowedSize = maxSize;

		if (mode != 0) {
			TakeFullSizePhoto = true;
		} else {
			TakeFullSizePhoto = false;
		}
		
		ImageFormat = format;

	}

	@SuppressLint("NewApi")
	public void SaveToGalalry(String ImageData, String name) {
		byte[] byteArray;
		try {
			byteArray = Base64.decode(ImageData);
			Bitmap bmp;
			bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

			OutputStream fOut = null;

			String appDirectoryName = GalleryFolderName;// "XWZ";
			Log.d("AndroidNative", "appDirectoryName: " + appDirectoryName);
			File imageRoot = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					appDirectoryName);

			imageRoot.mkdirs();

			String FileName = name + (ImageFormat == 0 ? ".jpg" : ".png");
			File file = new File(imageRoot, FileName);
			int index = 1;
			while (file.exists()) {

				FileName = name + Integer.toString(index) + (ImageFormat == 0 ? ".jpg" : ".png");
				file = new File(imageRoot, FileName);
				index++;
			}

			Log.d("AndroidNative", " FileName: " + FileName);
			Log.d("AndroidNative", "is esixts: " + file.exists());

			fOut = new FileOutputStream(file);

			bmp.compress(ImageFormat == 0 ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();
			fOut.close();

			// File f = new File(imageRoot + name);

			// String timeStamp = new
			// SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			ContentValues values = new ContentValues();
			values.put(Images.Media.TITLE, name);
			values.put(Images.Media.DESCRIPTION, "");
			values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
			values.put(Images.ImageColumns.BUCKET_ID, file.toString()
					.toLowerCase(Locale.US).hashCode());
			values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName()
					.toLowerCase(Locale.US));
			values.put("_data", file.getAbsolutePath());

			ContentResolver cr = NativeUtility.GetLauncherActivity()
					.getContentResolver();
			Uri path = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					values);
			Log.d("AndroidNative", "Saved: " + path.toString());

			UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
					"OnImageSavedEvent", path.toString());

			// scan just created photo:
			Intent mediaScanIntent = new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(path);
			NativeUtility.GetLauncherActivity().sendBroadcast(mediaScanIntent);

		} catch (Exception e) {
			Log.d("AndroidNative", "Not saved");
			UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
					"OnImageSaveFailedEvent", "");
			e.printStackTrace();

		}
	}

	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	// get the base 64 string

	@SuppressLint("NewApi")
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Log.d("AndroidNative", "Saving captured picture to: "
				+ mCurrentPhotoPath);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		NativeUtility.GetLauncherActivity().sendBroadcast(mediaScanIntent);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		StringBuilder result = null;

		if (requestCode == ChooserType.REQUEST_PICK_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				if (imageChooserManager == null) {
					imageChooserManager = new ImageChooserManager(
							NativeUtility.GetLauncherActivity(),
							ChooserType.REQUEST_PICK_PICTURE);
					imageChooserManager.setImageChooserListener(this);
				}

				imageChooserManager.submit(requestCode, data);
			} else {
				result = new StringBuilder();
				result.append(resultCode);
				result.append(AN_Bridge.UNITY_SPLITTER);
				result.append("");
				result.append(AN_Bridge.UNITY_SPLITTER);
				result.append("");
				UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
						"OnImagePickedEvent", result.toString());
			}

			return;
		}

		if (requestCode == RESULT_IMAGE_CAPTURE) {

			Log.d("AndroidNative", "RESULT_IMAGE_CAPTURE captured ");
			result = new StringBuilder();
			result.append(resultCode);
			result.append(AN_Bridge.UNITY_SPLITTER);

			if (resultCode == Activity.RESULT_OK) {
				Log.d("AndroidNative",
						"RESULT_IMAGE_CAPTURE captured. ActivityResult RESULT_OK");
				if (TakeFullSizePhoto) {
					File img = new File(mCurrentPhotoPath);
					Log.d("AndroidNative", "image is exsist: " + img.exists());
					Log.d("AndroidNative", "image size: " + img.length());

					result.append(mCurrentPhotoPath);
					result.append(AN_Bridge.UNITY_SPLITTER);

					BitmapFactory.Options bmOptions = new BitmapFactory.Options();
					bmOptions.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

					int photoW = bmOptions.outWidth;
					int photoH = bmOptions.outHeight;

					int scaleFactor = 1;
					if (photoW > photoH) {
						scaleFactor = photoW / MaxImageAllowedSize;
					} else {
						scaleFactor = photoH / MaxImageAllowedSize;
					}

					bmOptions.inJustDecodeBounds = false;
					bmOptions.inSampleSize = scaleFactor;
					bmOptions.inPurgeable = true;

					Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath,
							bmOptions);

					int w = MaxImageAllowedSize > b.getWidth() ? b.getWidth() : MaxImageAllowedSize;
					int h = MaxImageAllowedSize > b.getHeight() ? b.getHeight() : MaxImageAllowedSize;
					if (photoW > photoH) {
						float aspect = (float) b.getWidth()
								/ (float) b.getHeight();
						float new_height = w / aspect;
						h = (int) new_height;
					} else {
						float aspect = (float) b.getHeight()
								/ (float) b.getWidth();
						float new_width = h / aspect;
						w = (int) new_width;
					}
					Log.d("AndroidNative", "It's time to create scaled Bitmap");

					Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, w, h,
							false);

					Log.d("AndroidNative", "w: " + scaledBitmap.getWidth()
							+ " h: " + scaledBitmap.getHeight());
					String imgString = Base64
							.encode(getBytesFromBitmap(scaledBitmap));
					result.append(imgString);

					// This is ##### ridiculous. Some versions of Android save to the MediaStore as well. Not sure why!
					// I don't know what name Android will give either, so we get to search for this manually and remove it.
					String[] projection = {
							MediaStore.Images.ImageColumns.SIZE,
							MediaStore.Images.ImageColumns.DISPLAY_NAME,
							MediaStore.Images.ImageColumns.DATA,
							BaseColumns._ID, };
					//
					// Intialize the Uri and the Cursor, and the current expected size.
					Cursor c = null;
					Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					if (img != null) {
						// Query the Uri to get the data path. Only if the Uri is valid, and we had a valid size to be searching for.
						if ((u != null) && (img.length() > 0)) {
							c = NativeUtility.GetLauncherActivity().managedQuery(u, projection, null, null, null);
						}
						//
						// If we found the cursor and found a record in it (we also have the size).
						if ((c != null) && (c.moveToFirst())) {
							do {
								// Check each area in the gallery we built before
								boolean bFound = false;
								for (String sGallery : GalleryList) {
									if (sGallery.equalsIgnoreCase(c
											.getString(1))) {
										bFound = true;
										break;
									}
								}
								// To here we looped the full gallery.
								if (!bFound) {
									Log.d("AndroidNative",
											"DELETE IMAGE " + BaseColumns._ID
													+ " = " + c.getString(1)
													+ " " + c.getString(2)
													+ " " + c.getString(3));

									ContentResolver cr = NativeUtility.GetLauncherActivity().getContentResolver();
									cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,BaseColumns._ID + "=" + c.getString(3), null);
									break;
								}
							} while (c.moveToNext());
						}
					}

					if (SaveCameraImageToGallery) {
						galleryAddPic();
					}
				} else {
					result.append("");
					result.append(AN_Bridge.UNITY_SPLITTER);

					if (data != null) {
						Bundle extras = data.getExtras();
						Bitmap b = (Bitmap) extras.get("data");

						Log.d("AndroidNative", "w: " + b.getWidth() + " h: "
								+ b.getHeight());
						String imgString = Base64.encode(getBytesFromBitmap(b));

						result.append(imgString);
					} else {
						result.append("");
					}

				}

			} else {
				result.append("");
				result.append(AN_Bridge.UNITY_SPLITTER);
				result.append("");

				Log.d("AndroidNative",
						"RESULT_IMAGE_CAPTURE captured FAIL. BUT ActivityResult code = "
								+ requestCode);
			}

			UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
					"OnImagePickedEvent", result.toString());
		}

	}

	@SuppressLint("NewApi")
	public void StartImageChooser(Activity act) {
		try {

			imageChooserManager = new ImageChooserManager(act,
					ChooserType.REQUEST_PICK_PICTURE);
			imageChooserManager.setImageChooserListener(this);
			imageChooserManager.choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void GetImageFromGallery() {
		AndroidNativeProxy.startImageChooserProxy();
	}

	@SuppressLint("NewApi")
	public void GetImageFromCamera(boolean bSaveToGallery) {
		TakeFullSizePhoto = bSaveToGallery;
		
		Log.d("AndroidNative", "GetImageFromCamera: ");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(NativeUtility
				.GetLauncherActivity().getPackageManager()) != null) {

			if (TakeFullSizePhoto) {
				File photoFile = createImageFile();
				// Continue only if the File was successfully created
				if (photoFile != null) {
					Log.d("AndroidNative", "Getting full size photo: ");
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					AndroidNativeProxy.startProxyForResult(takePictureIntent,
							RESULT_IMAGE_CAPTURE);
				} else {
					TakeFullSizePhoto = false;
					AndroidNativeProxy.startProxyForResult(takePictureIntent,
							RESULT_IMAGE_CAPTURE);
				}
			} else {
				AndroidNativeProxy.startProxyForResult(takePictureIntent,
						RESULT_IMAGE_CAPTURE);
			}

		}
	}

	private static String mCurrentPhotoPath;

	@SuppressWarnings("deprecation")
	private File createImageFile() {
		// initialize the list!
		GalleryList.clear();
		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
		// intialize the Uri and the Cursor, and the current expected size.
		Cursor c = null;
		Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		//
		// Query the Uri to get the data path. Only if the Uri is valid.
		if (u != null) {
			c = NativeUtility.GetLauncherActivity().managedQuery(u, projection, null, null, null);
		}

		// If we found the cursor and found a record in it (we also have the id).
		if ((c != null) && (c.moveToFirst())) {
			do {
				// Loop each and add to the list.
				GalleryList.add(c.getString(0));
			} while (c.moveToNext());
		}
		Log.d("AndroidNative", "SaveToGalalry MEDIS FILES LIST " + GalleryList.toString());

		// Create an image file name
		Log.d("AndroidNative", "createImageFile: ");
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = (ImageFormat == 0 ? "JPEG" : "PNG") + "_CAMERASHOT_" + timeStamp;
		File imageRoot = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				GalleryFolderName);
		Log.d("AndroidNative", "GalleryFolderName: " + GalleryFolderName);
		Log.d("AndroidNative", "imageRoot folder: " + imageRoot.getPath());

		imageRoot.mkdirs();

		File image = null;
		try {
			image = File.createTempFile(imageFileName, /* prefix */
					ImageFormat == 0 ? ".jpg" : ".png", /* suffix */
					imageRoot /* directory */
			);
		} catch (IOException e) {
			Log.d("AndroidNative", "Failed To create temp File: ");
			e.printStackTrace();
		}

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		mCurrentPhotoPath = image.getAbsolutePath();

		Log.d("AndroidNative", "mCurrentPhotoPath: " + mCurrentPhotoPath);

		return image;
	}

	@Override
	public void onError(String arg0) {
		Log.d("AndroidNative", "chooser onError: ");
		StringBuilder result = new StringBuilder();
		result.append(Activity.RESULT_OK);
		result.append(AN_Bridge.UNITY_SPLITTER);
		result.append("");
		result.append(AN_Bridge.UNITY_SPLITTER);
		result.append("");
		UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
				"OnImagePickedEvent", result.toString());
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onImageChosen(ChosenImage image) {

		Log.d("AndroidNative", "onImageChosen: ");

		StringBuilder result = null;
		result = new StringBuilder();
		result.append(Activity.RESULT_OK);
		result.append(AN_Bridge.UNITY_SPLITTER);
		result.append(image.getFilePathOriginal());
		result.append(AN_Bridge.UNITY_SPLITTER);

		String imgString = "";
		if (image != null) {

			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(image.getFilePathOriginal(), bmOptions);

			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			int scaleFactor = 1;
			if (photoW > photoH) {
				scaleFactor = photoW / MaxImageAllowedSize;
			} else {
				scaleFactor = photoH / MaxImageAllowedSize;
			}

			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;

			bmOptions.inPurgeable = true;
			Bitmap b = BitmapFactory.decodeFile(image.getFilePathOriginal(),
					bmOptions);

			int w = MaxImageAllowedSize > b.getWidth() ? b.getWidth() : MaxImageAllowedSize;
			int h = MaxImageAllowedSize > b.getHeight() ? b.getHeight() : MaxImageAllowedSize;
			if (photoW > photoH) {
				float aspect = (float) b.getWidth() / (float) b.getHeight();
				float new_height = w / aspect;
				h = (int) new_height;
			} else {
				float aspect = (float) b.getHeight() / (float) b.getWidth();
				float new_width = h / aspect;
				w = (int) new_width;
			}

			Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, w, h, false);

			imgString = Base64.encode(getBytesFromBitmap(scaledBitmap));
			Log.d("AndroidNative", "w: " + scaledBitmap.getWidth() + " h: "
					+ scaledBitmap.getHeight());
		}

		result.append(imgString);

		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/bimagechooser");
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				Log.d("AndroidNative", "bimagechooser file deleted "
						+ children[i]);
				new File(dir, children[i]).delete();
			}
		}
		dir.delete();

		UnityPlayer.UnitySendMessage(CAMERA_SERVICE_LISTNER_NAME,
				"OnImagePickedEvent", result.toString());

	}

}
