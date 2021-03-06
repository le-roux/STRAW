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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

public class CreatePlateActivity extends AppCompatActivity {

    private TextView title;
    private EditText name_field;
    private EditText price_field;
    private EditText ingredients_field;
    private Button add_button;
    private CheckBox vegan_checkbox;
    private CheckBox glutenfree_checkbox;
    private ImageView image;
    private Uri fileUri = null;
    private Context context;
    private Plate plate;
    private Intent intent;
    private ListView listView;
    private PopupWindow popupWindow;

    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    private static final String PLATE = "Plate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Create_Plate));
            setSupportActionBar(toolbar);
        }
        this.setPopupWindow();
        this.getViews();
        this.intent = getIntent();
        this.context = getApplicationContext();

        //If needed, restore the values saved
        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(PLATE));
        else {
            //Init the values with the one provided by the calling activity
            String action = this.intent.getStringExtra(CreateMenuActivity.ACTION);
            if (action.equals(CreateMenuActivity.ADD_ELEMENT)) {
                //New plate
                this.plate = new Plate();
            } else if(action.equals(CreateMenuActivity.EDIT_ELEMENT)) {
                //Modification of an existing plate
                String description = this.intent.getStringExtra(CreateMenuActivity.ELEMENT);
                restoreValues(description);
                this.title.setText(getText(R.string.Edit_plate));
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

        //Add a listener on the "Add" button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
                Intent result = new Intent(getApplicationContext(), CreateMenuActivity.class);
                Bundle data = new Bundle();
                data.putString(CreateMenuActivity.ID, intent.getStringExtra(CreateMenuActivity.ID));
                data.putString(CreateMenuActivity.ELEMENT, plate.toString());
                data.putInt(CreateMenuActivity.TYPE, Menu.PLATES);
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
            String imageString = ImageManager.getImageFromUri(context, fileUri);
            ImageManager.setImage(context, image, imageString);
        } else if(resultCode == RESULT_CANCELED) {
            Logger.d("result cancelled");
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

    /**
     * Decode the string provided as a JSON object describing a plate and use these infos to
     * set the proper values of the different fields
     * @param drink_descriptor a string representation of a JSON object describing a plate
     */
    private void restoreValues(String drink_descriptor) {
        this.plate = (Plate) Food.create(drink_descriptor);

        // In case of error
        if (this.plate == null)
            return;

        if (!this.plate.getName().equals(Food.DEFAULT_NAME))
            this.name_field.setText(this.plate.getName());
        if (this.plate.getPrice() != Food.DEFAULT_PRICE)
            this.price_field.setText(String.valueOf(this.plate.getPrice()));
        this.ingredients_field.setText(String.valueOf(this.plate.getIngredients()));
        this.vegan_checkbox.setActivated(this.plate.isVegan());
        this.glutenfree_checkbox.setActivated(this.plate.isGlutenFree());

        String image = this.plate.getImage();
        if (image != null) {
            ImageManager.setImage(this.context, this.image, image);
        }
        else {
            this.fileUri = null;
        }
    }

    /**
     * Update the plate object according to the values in the different fields of the page
     */
    private void update() {
        //Name
        String name = this.name_field.getText().toString();
        if(name.equals(""))
            this.plate.setName(getText(R.string.Default).toString());
        else
            this.plate.setName(name);

        //Price
        String price = this.price_field.getText().toString();
        if(price.equals(""))
            this.plate.setPrice(0d);
        else
            this.plate.setPrice(Double.parseDouble(price));

        //Vegan & gluten_free
        this.plate.setVegan(this.vegan_checkbox.isChecked());
        this.plate.setGlutenFree(this.glutenfree_checkbox.isChecked());

        //Ingredients
        this.plate.setIngredients(this.ingredients_field.getText().toString());

        //Image URI
        if(this.fileUri != null)
            this.plate.setImage(ImageManager.getImageFromUri(this, this.fileUri));
        else
            this.plate.setImage(null);
    }

    private void setPopupWindow() {
        this.popupWindow = new PopupWindow(this.getApplicationContext());

        //Content
        ArrayList<String> content = new ArrayList<String>();
        content.add(getString(R.string.Choose_photo));
        content.add(getString(R.string.Take_photo));

        //Adapter
        ArrayAdapter<String> popupAdapter = new ArrayAdapter<String>(this.getApplicationContext(),
                android.R.layout.simple_list_item_1, content);
        this.listView = new ListView(this.getApplicationContext());
        listView.setAdapter(popupAdapter);

        //Listener for the popup window
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Choose photo
                    Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePictureIntent.setType("image/*");
                    startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST_CODE);
                } else {
                    //Take photo
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = ImageManager.getOutputMediaFileUri(context,
                            name_field.getText().toString()); //Create a file to store the photo
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //Set the image file name
                    startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE); //Launch the camera app
                }
                popupWindow.dismiss();
            }
        });

        //Settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    /**
     * Get the java representation of the different views of the activity
     */
    private void getViews() {
        this.title = (TextView)findViewById(R.id.title);
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.ingredients_field = (EditText)findViewById(R.id.ingredients_field);
        this.vegan_checkbox = (CheckBox)findViewById(R.id.vegan_checkbox);
        this.glutenfree_checkbox = (CheckBox)findViewById(R.id.glutenfree_checkbox);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.image = (ImageView)findViewById(R.id.photo_imageView);
        Uri uri = Uri.parse(ImageManager.URI_NO_PHOTO);
        ImageManager.setImage(this, this.image, ImageManager.getImageFromUri(getApplicationContext(), uri));
    }
}
