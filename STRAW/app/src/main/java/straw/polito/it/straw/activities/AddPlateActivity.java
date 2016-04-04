package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.R;

public class AddPlateActivity extends AppCompatActivity {

    private EditText name_field;
    private EditText price_field;
    private EditText ingredients_field;
    private Button take_photo_button;
    private Button choose_photo_button;
    private Button add_button;
    private CheckBox vegan_checkbox;
    private CheckBox glutenfree_checkbox;
    private ImageView image;
    private Uri fileUri = null;
    private Context context;
    private Plate plate;
    private SharedPreferences sharedPreferences;
    private Intent intent;

    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    private static final String TAG = "FoodApp";
    private static final String PLATE = "Plate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plate);
        this.intent = getIntent();
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.ingredients_field = (EditText)findViewById(R.id.ingredients_field);
        this.vegan_checkbox = (CheckBox)findViewById(R.id.vegan_checkbox);
        this.glutenfree_checkbox = (CheckBox)findViewById(R.id.glutenfree_checkbox);
        this.take_photo_button = (Button)findViewById(R.id.take_photo_button);
        this.choose_photo_button =(Button)findViewById(R.id.choose_photo_button);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(PLATE));
        else {
            String description = this.intent.getStringExtra(CreateMenuActivity.ELEMENT);
            this.plate = (Plate)Food.create(description);
        }

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

        //Add a listener on the "Add" button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString(plate.getName(), plate.toString());
                Intent result = new Intent(getApplicationContext(), CreateMenuActivity.class);
                Bundle data = new Bundle();
                data.putString(CreateMenuActivity.ID, intent.getStringExtra(CreateMenuActivity.ID));
                data.putString(CreateMenuActivity.ELEMENT, plate.toString());
                result.putExtras(data);
                setResult(Activity.RESULT_OK, result);
                finish();
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
        b.putString(PLATE, this.plate.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        restoreValues(b.getString(PLATE));
    }

    private void restoreValues(String drink_descriptor) {
        this.plate = (Plate) Food.create(drink_descriptor);
        this.name_field.setText(this.plate.getName());
        this.price_field.setText(String.valueOf(this.plate.getPrice()));
        this.ingredients_field.setText(String.valueOf(this.plate.getIngredients()));
        this.vegan_checkbox.setActivated(this.plate.isVegan());
        this.glutenfree_checkbox.setActivated(this.plate.isGlutenFree());
        String uri = this.plate.getImageURI();
        if (uri != null)
            this.fileUri = Uri.parse(uri);
        else {
            this.fileUri = null;
            ImageManager.setImage(this.context, this.image, this.fileUri);
        }
    }

    private void update() {
        this.plate.setName(this.name_field.getText().toString());
        this.plate.setPrice(Double.parseDouble(this.price_field.getText().toString()));
        this.plate.setVegan(this.vegan_checkbox.isActivated());
        this.plate.setGlutenFree(this.glutenfree_checkbox.isActivated());
        //TO DO : update ingredients
    }
}
