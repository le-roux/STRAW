package straw.polito.it.straw.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sylvain on 03/04/2016.
 */
public class ImageManager {

    public static final String URI_NO_PHOTO = "android.resource://straw.polito.it.straw/drawable/no_image";

    private static File getOutputMediaFile(Context context, String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        if(!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Logger.d( "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG" + timeStamp + ".jpg");
        return mediaFile;
    }

    public static Uri getOutputMediaFileUri(Context context, String fileName) {
        return Uri.fromFile(getOutputMediaFile(context, fileName));
    }

    public static void setImage(Context context, ImageView imageView, String image) {
        Bitmap bitmap = null;
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap == null)
            Logger.d("bitmap null");
        else {
            Bitmap small = Bitmap.createScaledBitmap(bitmap, 600, 800, false);
            imageView.setImageBitmap(small);
        }
    }

    public static String getImageFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null)
            return null;
        else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            bitmap.recycle();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
    }
}
