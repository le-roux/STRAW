package straw.polito.it.straw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private Button take_photo_button;
    private Button choose_photo_button;
    private Button add_button;
    private ImageView image;
    private Uri fileUri = null;
    private Context context;
    private Drink drink;
    private SharedPreferences sharedPreferences;

    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    private static final String TAG = "FoodApp";
    private static final String DRINK = "Drink";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.volume_field = (EditText)findViewById(R.id.volume_field);
        this.take_photo_button = (Button)findViewById(R.id.take_photo_button);
        this.choose_photo_button =(Button)findViewById(R.id.choose_photo_button);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(DRINK));
        else
            this.drink = new Drink();

        //Add a listener on the button to take a photo
        take_photo_button.setOnClickListener(new View.OnClickListener() {
            private Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            @Override
            public void onClick(View view) {
                fileUri = ImageManager.getOutputMediaFileUri(context, name_field.getText().toString()); //Create a file to store the photo
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //Set the image file name
                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE); //Launch the camera app
            }
        }); //End of the listener

        //Add a listener on the button to choose a picture
        choose_photo_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePictureIntent.setType("image/*");
                startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST_CODE);
            }
        }); //End of listener

        //Add a listener on the Add button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString(drink.getName(), drink.toString());
                //TO DO : switch activity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PICTURE_REQUEST_CODE) {
                fileUri = data.getData();
            }
            //In case of "Select picture", the fileUri is already set
            ImageManager.setImage(context, image, fileUri);
        } else if(resultCode == RESULT_CANCELED) {
            Log.d(TAG, "result canceled");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        update();
        b.putString(DRINK, this.drink.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        restoreValues(b.getString(DRINK));
    }

    private void restoreValues(String drink_descriptor) {
        this.drink = (Drink) Food.create(drink_descriptor);
        this.name_field.setText(this.drink.getName());
        this.price_field.setText(String.valueOf(this.drink.getPrice()));
        this.volume_field.setText(String.valueOf(this.drink.getVolume()));
        String uri = this.drink.getImageURI();
        if (uri != null)
            this.fileUri = Uri.parse(uri);
        else {
            this.fileUri = null;
            ImageManager.setImage(this.context, this.image, this.fileUri);
        }
    }

    public void update() {
        this.drink.setName(this.name_field.getText().toString());
        this.drink.setPrice(Double.parseDouble(this.price_field.getText().toString()));
        this.drink.setVolume(Double.parseDouble(this.volume_field.getText().toString()));
        this.drink.setImageURI(this.fileUri.toString());
    }
}
