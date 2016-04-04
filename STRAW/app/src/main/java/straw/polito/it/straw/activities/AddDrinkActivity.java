package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import java.util.ArrayList;

import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.R;

public class AddDrinkActivity extends AppCompatActivity {

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
    private PopupWindow popupWindow;
    private ListView listView;
    private Intent intent;

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
        this.name_field = (EditText)findViewById(R.id.name_field);
        this.price_field = (EditText)findViewById(R.id.price_field);
        this.volume_field = (EditText)findViewById(R.id.volume_field);
        this.take_photo_button = (Button)findViewById(R.id.take_photo_button);
        this.choose_photo_button =(Button)findViewById(R.id.choose_photo_button);
        this.add_button = (Button)findViewById(R.id.confirm_button);
        this.image = (ImageView)findViewById(R.id.photo_imageView);
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (savedInstanceState != null)
            restoreValues(savedInstanceState.getString(DRINK));
        else {
            String description = this.intent.getStringExtra(CreateMenuActivity.ELEMENT);
            this.drink = (Drink)Food.create(description);
        }

        //Add a listener to the imageView which displays the popup window
        this.image.setFocusable(true);
        this.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAsDropDown(view, 0, 0);
            }
        });
        this.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString(drink.getName(), drink.toString());
                Intent result = new Intent(getApplicationContext(), CreateMenuActivity.class);
                Bundle data = new Bundle();
                data.putString(CreateMenuActivity.ID, intent.getStringExtra(CreateMenuActivity.ID));
                data.putString(CreateMenuActivity.ELEMENT, drink.toString());
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

    private void setPopupWindow() {
        this.popupWindow = new PopupWindow(this.getApplicationContext());
        ArrayList<String> content = new ArrayList<String>();
        content.add(getString(R.string.Choose_photo));
        content.add(getString(R.string.take_photo));
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
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }
}
