package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

/**
 * This activity allows the manager to create a new drink and to add it to the menu of his/her
 * restaurant and also to modify an existing drink.
 */
public class CreateDrinkActivity extends AppCompatActivity {

    //The views
    private TextView title;
    private EditText name_field;
    private EditText price_field;
    private EditText volume_field;
    private Button add_button;
    private ImageView image;
    private ListView listView;
    private PopupWindow popupWindow;

    //Data from the model
    private Uri fileUri = null;
    private Drink drink;

    //
    private Context context;
    private Intent intent;
    private String action;

    //Request codes for the onResult method
    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    //Key for storing/retrieving the data to/from dictionaries
    private static final String DRINK = "Drink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drink);
        this.intent = getIntent();

        setPopupWindow();

        //Get the views from the display
        this.title = (TextView)findViewById(R.id.title);
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.volume_field = (EditText)findViewById(R.id.volume_field);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.image = (ImageView)findViewById(R.id.photo_imageView);

        this.context = getApplicationContext();

        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(DRINK));
        else {
            //No temporarily stored data --> retrieve it from the intent that launched the activity
            action = this.intent.getStringExtra(CreateMenuActivity.ACTION);
            if(action.equals(CreateMenuActivity.ADD_ELEMENT)) {
                this.drink = new Drink();
            } else if (action.equals(CreateMenuActivity.EDIT_ELEMENT)) {
                String description = this.intent.getStringExtra(CreateMenuActivity.ELEMENT);
                this.drink = (Drink) Food.create(description);
                //Change the title of the page to describe properly the current action
                this.title.setText(getText(R.string.Edit_drink));
            }
        }

        //Add a listener to the imageView which displays the popup window
        this.image.setFocusable(true);
        this.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAsDropDown(view, 0, -100);
            }
        });

        //Add a listener to the "Add" button
        this.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();

                //Prepare the return to the calling activity
                Intent result = new Intent(getApplicationContext(), CreateMenuActivity.class);
                Bundle data = new Bundle();
                data.putString(CreateMenuActivity.ACTION, action);
                if (action.equals(CreateMenuActivity.EDIT_ELEMENT)) {
                    data.putString(CreateMenuActivity.ID, intent.getStringExtra(CreateMenuActivity.ID));
                }
                data.putString(CreateMenuActivity.ELEMENT, drink.toString());
                data.putInt(CreateMenuActivity.TYPE, Menu.DRINKS);
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
            Logger.d("result canceled");
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

    /**
     * A simple method that takes care of setting the content of all the fields in the activity
     * according to the description given
     * @param drink_descriptor String representation of a JSON object describing a drink
     *                         See the Drink class for a description of the expected fields on this
     *                         JSON object.
     */
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

    /**
     * Update the values in the Drink object according to the fields of the display
     */
    public void update() {
        //Name
        String name = this.name_field.getText().toString();
        if (name.equals(""))
            this.drink.setName(getText(R.string.Default).toString());
        else
            this.drink.setName(name);

        //Price
        String price = this.price_field.getText().toString();
        if (price.equals(""))
            this.drink.setPrice(0d);
        else
            this.drink.setPrice(Double.parseDouble(price));

        //Volume
        String volume = this.volume_field.getText().toString();
        if (volume.equals(""))
            this.drink.setVolume(0d);
        else
            this.drink.setVolume(Double.parseDouble(volume));

        //Image
        if(this.fileUri != null)
            this.drink.setImageURI(this.fileUri.toString());
        else
            this.drink.setImageURI(null);
    }

    /**
     * Initialisation of the popup that appears when clicking on the ImageView
     */
    private void setPopupWindow() {
        this.popupWindow = new PopupWindow(this.getApplicationContext());

        //Create the content of the popup menu
        //WARNING : when modifying it, don't forget to change also the constants
        //definition that follows
        ArrayList<String> content = new ArrayList<String>();
        content.add(getString(R.string.Choose_photo));
        content.add(getString(R.string.Take_photo));
        final int CHOOSE_PHOTO = 0;
        final int TAKE_PHOTO = 1;


        ArrayAdapter<String> popupAdapter = new ArrayAdapter<String>(this.getApplicationContext(),
                android.R.layout.simple_list_item_1, content);
        this.listView = new ListView(this.getApplicationContext());
        listView.setAdapter(popupAdapter);
        //Add a listener to react when one of the element of the popup is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == CHOOSE_PHOTO) {
                    Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePictureIntent.setType("image/*");
                    startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST_CODE);
                } else if (position == TAKE_PHOTO) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Create a file to store the photo
                    fileUri = ImageManager.getOutputMediaFileUri(context, name_field.getText().toString());
                    //Set the image file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    //Launch the camera app
                    startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
                }
                popupWindow.dismiss();
            }
        });

        //Set the appearance and content of the popup menu
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }
}
