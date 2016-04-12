package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import straw.polito.it.straw.straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;

public class AddDrinkActivity extends AppCompatActivity {

    private TextView title;
    private EditText name_field;
    private EditText price_field;
    private EditText volume_field;
    private Button add_button;
    private ImageView image;
    private Uri fileUri = null;
    private Context context;
    private Drink drink;
    private SharedPreferences sharedPreferences;
    private PopupWindow popupWindow;
    private ListView listView;
    private Intent intent;
    private String action;

    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_PICTURE_REQUEST_CODE = 2;

    private static final String TAG = "FoodApp";
    private static final String DRINK = "Drink";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);
        this.intent = getIntent();
        setPopupWindow();
        this.title = (TextView)findViewById(R.id.title);
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.volume_field = (EditText)findViewById(R.id.volume_field);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.image = (ImageView)findViewById(R.id.photo_imageView);
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(DRINK));
        else {
            action = this.intent.getStringExtra(CreateMenuActivity.ACTION);
            if(action.equals(CreateMenuActivity.ADD_ELEMENT)) {
                this.drink = new Drink();
            } else if (action.equals(CreateMenuActivity.EDIT_ELEMENT)) {
                String description = this.intent.getStringExtra(CreateMenuActivity.ELEMENT);
                this.drink = (Drink) Food.create(description);
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

        this.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
                sharedPreferences.edit().putString(drink.getName(), drink.toString());
                Intent result = new Intent(getApplicationContext(), CreateMenuActivity.class);
                Bundle data = new Bundle();
                data.putString(CreateMenuActivity.ACTION, action);
                if (action == CreateMenuActivity.EDIT_ELEMENT) {
                    data.putString(CreateMenuActivity.ID, intent.getStringExtra(CreateMenuActivity.ID));
                }
                data.putString(CreateMenuActivity.ELEMENT, drink.toString());
                Logger.d("drink stored");
                result.putExtras(data);
                setResult(Activity.RESULT_OK, result);
                Logger.d("result set");
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

    /**
     * Update the values in the Drink object according to the fields of the display
     */
    public void update() {
        String name = this.name_field.getText().toString();
        if (name.equals(""))
            this.drink.setName(getText(R.string.Default).toString());
        else
            this.drink.setName(name);
        String price = this.price_field.getText().toString();
        if (price.equals(""))
            this.drink.setPrice(0d);
        else
            this.drink.setPrice(Double.parseDouble(price));
        String volume = this.volume_field.getText().toString();
        if (volume.equals(""))
            this.drink.setVolume(0d);
        else
            this.drink.setVolume(Double.parseDouble(volume));
        if(this.fileUri != null)
            this.drink.setImageURI(this.fileUri.toString());
        else
            this.drink.setImageURI(null);
    }

    private void setPopupWindow() {
        this.popupWindow = new PopupWindow(this.getApplicationContext());
        ArrayList<String> content = new ArrayList<String>();
        content.add(getString(R.string.Choose_photo));
        content.add(getString(R.string.Take_photo));
        ArrayAdapter<String> popupAdapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, content);
        this.listView = new ListView(this.getApplicationContext());
        listView.setAdapter(popupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Choose photo
                    Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePictureIntent.setType("image/*");
                    startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Take photo
                    fileUri = ImageManager.getOutputMediaFileUri(context, name_field.getText().toString()); //Create a file to store the photo
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //Set the image file name
                    startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE); //Launch the camera app
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }
}
