package straw.polito.it.straw;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;

public class AddDrink extends AppCompatActivity {

    private EditText name_field;
    private EditText price_field;
    private EditText volume_field;
    private Button take_photo;
    private Button choose_photo;
    private ImageView image;
    private Uri fileUri;
    private Context context;

    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;
    private static final String TAG = "FoodApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.volume_field = (EditText)findViewById(R.id.volume_field);
        this.take_photo = (Button)findViewById(R.id.take_photo_button);
        this.choose_photo =(Button)findViewById(R.id.choose_photo_button);
        this.context = getApplicationContext();

        //Add a listener on the button to take a photo
        take_photo.setOnClickListener(new View.OnClickListener() {
            private Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            @Override
            public void onClick(View view) {
                fileUri = ImageManager.getOutputMediaFileUri(context, name_field.getText().toString()); //Create a file to store the photo
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //Set the image file name
                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE); //Launch the camera app
            }
        }); //End of the listener
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PICTURE_REQUEST_CODE) {
                fileUri = data.getData();
            }
            ImageManager.setImage(context, image, fileUri);
        } else if(resultCode == RESULT_CANCELED) {
            Log.d(TAG, "result canceled");
        }

    }
}
