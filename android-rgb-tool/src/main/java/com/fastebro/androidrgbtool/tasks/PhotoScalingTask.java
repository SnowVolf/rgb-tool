package com.fastebro.androidrgbtool.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import com.fastebro.androidrgbtool.events.PhotoScaling;
import com.fastebro.androidrgbtool.utils.UImage;

import java.io.*;

/**
 * Created by danielealtomare on 5/22/13.
 */
public class PhotoScalingTask extends AsyncTask<Void, Void, Boolean> {
    private String mPhotoPath;
    private PhotoScaling mPhotoScalingCallback;
    private boolean mUseTempFile;
    private Context mContext;

    public PhotoScalingTask(Activity activity, String photoPath, boolean useTempFile) {
        mPhotoPath = photoPath;
        mUseTempFile = useTempFile;
        mContext = activity;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mPhotoScalingCallback = (PhotoScaling) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PhotoScaling interface");
        }
    }


    @Override
    protected void onCancelled(Boolean result) {
        super.onCancelled(result);
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        // Resize the image
        try {
            if (mUseTempFile) {
                copyFile(mPhotoPath);
            }

            savePrescaledBitmap(mPhotoPath);

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mPhotoScalingCallback.onScalingComplete(mPhotoPath, mUseTempFile);
        }
    }


    private void copyFile(String inputPath) {
        InputStream in = null;
        OutputStream out = null;
        String filename;

        try {
            filename = new File(inputPath).getName();
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(mContext.getFilesDir() + "/" + filename);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            mPhotoPath = mContext.getFilesDir() + "/" + filename;
        } catch (FileNotFoundException e) {

        } catch (Exception e) {

        }
    }


    private void savePrescaledBitmap(String filename) throws IOException {
        File file = null;
        FileInputStream fis;

        BitmapFactory.Options opts;
        int resizeScale;
        Bitmap bmp;

        file = new File(filename);

        // This bit determines only the width/height of the bitmap without loading the contents
        opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        fis = new FileInputStream(file);
        BitmapFactory.decodeStream(fis, null, opts);
        fis.close();

        // Find the correct scale value. It should be a power of 2
        resizeScale = 1;

        if (opts.outHeight > UImage.JPEG_FILE_IMAGE_MAX_SIZE ||
                opts.outWidth > UImage.JPEG_FILE_IMAGE_MAX_SIZE) {
            resizeScale = (int) Math.pow(2, (int) Math.round(
                    Math.log(UImage.JPEG_FILE_IMAGE_MAX_SIZE /
                            (double) Math.max(opts.outHeight, opts.outWidth))
                            / Math.log(0.5)
            ));
        }

        // Load pre-scaled bitmap
        opts = new BitmapFactory.Options();
        opts.inSampleSize = resizeScale;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        fis = new FileInputStream(file);

        bmp = BitmapFactory.decodeStream(fis, null, opts);

        fis.close();

        // Adjust image orientation
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, bounds);

        ExifInterface exif = new ExifInterface(filename);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;

        if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
            rotationAngle = 0;
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationAngle = 90;
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationAngle = 180;
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationAngle = 270;
        }

        // Android BUG: fix orientation if value is equal to 0.
        if (orientation == 0) {
            // set orientation to portrait
            if (bmp.getHeight() > bmp.getWidth()) {
                rotationAngle = 0;
            } else {
                rotationAngle = 90;
            }
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(),
                bmp.getHeight(), matrix, true);

        // Compress image
        OutputStream outStream = null;
        File scaledImageFile = new File(filename);

        outStream = new FileOutputStream(scaledImageFile);
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
        outStream.flush();
        outStream.close();
    }
}