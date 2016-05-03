package straw.polito.it.straw.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sylvain on 03/04/2016.
 */
public class ImageManager {

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

    public static void setImage(Context context, ImageView imageView, Uri uri) {
        Logger.d( "setImageURI uri = " + uri);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null)
            Logger.d("bitmap null");
        else {
            Bitmap small = Bitmap.createScaledBitmap(bitmap, 600, 800, false);
            imageView.setImageBitmap(small);
        }
    }
}
