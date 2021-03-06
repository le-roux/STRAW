package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import straw.polito.it.straw.AddressContainer;
import straw.polito.it.straw.R;

import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.fragments.AddressChooserFragment;
import straw.polito.it.straw.services.RegistrationIntentService;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class CreateManagerAccountActivity extends AppCompatActivity implements AddressContainer {

    private ImageView photo;
    private EditText c_pwd;
    private EditText cc_pwd;
    private EditText tel;
    private EditText email;
    private EditText r_n;
    private EditText min;
    private EditText max;
    private Spinner food;
    private Spinner r_t;
    private EditText addr;
    private EditText seats;
    private Button c_acc_button;
    private PopupWindow popUp;
    private boolean inEdit;
    private TextView o_pwd;
    private String old_email;
    private TextView n_pwd;

    private Bitmap bitmap;

    private String imageString;
    private List<Address> addressList;

    private List<String> types;
    private List<String> Ftypes;
    private SharedPreferencesHandler sharedPreferencesHandler;
    private SharedPreferences mShared;
    private static final int IMAGE_REQ = 1;
    private static final int CAMERA_REQ = 2;
    private JSONArray jsonArray;
    private Manager man;
    private boolean sw;
    private Context context;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static final String MANAGER = "manager";
    public static final String MANAGER_LIST = "ManagerList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Create_Manager_Account));
            setSupportActionBar(toolbar);
        }

        this.context = this;
        sharedPreferencesHandler = ((StrawApplication)getApplication()).getSharedPreferencesHandler();
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        this.jsonArray = new JSONArray();
        if(mShared.contains(MANAGER_LIST)){
            try{
                String ss = mShared.getString(MANAGER_LIST, "Error");
                jsonArray = new JSONArray(ss);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        initialize();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        types = new ArrayList<>();
        types.add(getString(R.string.bar));
        types.add(getString(R.string.restaurant));
        types.add(getString(R.string.canteen));
        types.add(getString(R.string.ta));

        Ftypes = new ArrayList<>();
        Ftypes.add(getString(R.string.italian));
        Ftypes.add(getString(R.string.jap));
        Ftypes.add(getString(R.string.pizzeria));
        Ftypes.add(getString(R.string.kebap));
        Ftypes.add(getString(R.string.chinese));
        Ftypes.add(getString(R.string.other));


        setListeners();
        if (getIntent().hasExtra(MANAGER)) {
            inEdit = true;
            man = new Manager(getIntent().getExtras().getString(MANAGER));
            old_email = man.getEmail();
            loadPrevInfo(man);
        } else {
            inEdit = false;
            man = new Manager();
            setPhoto();
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean("tokenSW", false);
                if (sentToken) {
                    man.setTokenGCM(sharedPreferences.getString("tokenGCM","Error"));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("complete"));
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    private void loadPrevInfo(Manager man) {
        imageString = man.getImage();
        ImageManager.setImage(getApplicationContext(), photo, imageString);
        tel.setText(String.valueOf(man.getTelephone()));
        email.setText(man.getEmail());
        r_n.setText(man.getRes_name());
        r_t.setSelection(types.indexOf(man.getRes_type()));
        addr.setText(man.getAddress());
        seats.setText(String.valueOf(man.getSeats()));
        c_acc_button.setText(getString(R.string.save));
        min.setText(String.valueOf(man.getMin_price()));
        max.setText(String.valueOf(man.getMax_price()));
        food.setSelection(Ftypes.indexOf(man.getFood_type()));
    }

    private void setListeners() {

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.showAsDropDown(v, 0, 0);
            }
        });

        r_t.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, types));
        food.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, Ftypes));

        c_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addr.getText().toString().equals("")) {
                    addressList = AddressChooserFragment.showAddressChooser(CreateManagerAccountActivity.this, addr.getText().toString());
                    if (addressList == null) {
                        man.setAddress(addr.getText().toString());
                        man.setLatitude(0);
                        man.setLongitude(0);
                        validate();
                    }
                } else {
                    showAlert(getString(R.string.m_addr), getString(R.string.error), false);
                }
            }
        });


    }

    private void showAlert(String message,String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(CreateManagerAccountActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (ex) {

                            finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
        alertDialog.show();
    }

    private void setPhoto() {
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        photo.setImageBitmap(bitmap);
        Uri uri = Uri.parse(ImageManager.URI_NO_PHOTO);
        imageString = ImageManager.getImageFromUri(getApplicationContext(), uri);
    }

    private void initialize() {
        photo = (ImageView)findViewById(R.id.photo_imageView);
        c_pwd = (EditText)findViewById(R.id.c_pwd_editText);
        cc_pwd = (EditText)findViewById(R.id.cc_pwd_editText);
        tel = (EditText)findViewById(R.id.tel_editText);
        r_n = (EditText)findViewById(R.id.diet_editText);
        r_t = (Spinner)findViewById(R.id.r_t_spinner);
        addr = (EditText)findViewById(R.id.pref_t_textView);
        seats = (EditText)findViewById(R.id.seats_editText);
        email = (EditText)findViewById(R.id.email_editText);
        c_acc_button = (Button)findViewById(R.id.create_button);
        min = (EditText)findViewById(R.id.min_price_editText);
        max = (EditText)findViewById(R.id.max_price_editText);
        food = (Spinner)findViewById(R.id.f_t_spinner);
        o_pwd = (TextView) findViewById(R.id.pwd_textView);
        n_pwd = (TextView) findViewById(R.id.cc_pwd_textView);
        if(inEdit){
            o_pwd.setText(getString(R.string.o_pwd));
            n_pwd.setText(getString(R.string.n_pwd));
        }
        setUpPopUpWindow();
        sw=false;

    }

    private void setUpPopUpWindow() {
        popUp = new PopupWindow(getBaseContext());
        final List<String> listOpt=new ArrayList<>();
        listOpt.add(getString(R.string.Choose_photo));
        listOpt.add(getString(R.string.Take_photo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateManagerAccountActivity.this, android.R.layout.simple_list_item_1, listOpt);
        final ListView opt_listView = new ListView(getBaseContext());
        opt_listView.setAdapter(adapter);
        opt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, getString(R.string.Choose_photo)), IMAGE_REQ);
                }
                if (position == 1){
                    //camera stuff
                    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                    imagesFolder.mkdirs();

                    File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
                    Uri uriSavedImage = Uri.fromFile(image);
                    imageString = ImageManager.getImageFromUri(getApplicationContext(), uriSavedImage);
                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(imageIntent, CAMERA_REQ);

                }
                popUp.dismiss();
            }
        });
        popUp.setFocusable(true);
        popUp.setWidth(500);
        popUp.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popUp.setContentView(opt_listView);
        popUp.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( data != null && resultCode == RESULT_OK && requestCode == IMAGE_REQ){
            sw = false;
            Uri uri = data.getData();
            imageString = ImageManager.getImageFromUri(getApplicationContext(), uri);
            ImageManager.setImage(getApplicationContext(), photo, imageString);
        }
        if( requestCode == CAMERA_REQ){
            sw = true;
            ImageManager.setImage(getApplicationContext(), photo, imageString);
        }
    }

    @Override
    public void setAddressNumber(int i) {
        if (i < this.addressList.size()) {
            Address address = this.addressList.get(i);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            String addressString = address.getAddressLine(0)
                    + ' '
                    + address.getAddressLine(1)
                    + ' '
                    + address.getAddressLine(2);
            this.man.setLatitude(latitude);
            this.man.setLongitude(longitude);
            this.man.setAddress(addressString);
            validate();
        }
    }

    public void validate() {
        boolean sw = false;
        if (!email.getText().toString().equals("")) {
            man.setEmail(email.getText().toString());
        } else {
            showAlert(getString(R.string.m_email), getString(R.string.error), false);
            sw = true;
        }
        if(!inEdit) {
            if (c_pwd.getText().toString().equals("") || !c_pwd.getText().toString().equals(cc_pwd.getText().toString())) {
                showAlert(getString(R.string.m_pwd), getString(R.string.error), false);
                sw = true;
            }
        }
        if (tel.getText().toString().length() > 6) {
            man.setTelephone(tel.getText().toString());
        } else {
            showAlert(getString(R.string.m_tel), getString(R.string.error), false);
            sw = true;
        }
        if (!r_n.getText().toString().equals("")) {
            man.setRes_name(r_n.getText().toString());
        } else {
            showAlert(getString(R.string.m_r_n), getString(R.string.error), false);
            sw = true;
        }
        man.setRes_type(types.get(r_t.getSelectedItemPosition()));
        man.setFood_type(Ftypes.get(food.getSelectedItemPosition()));
        if (!seats.getText().toString().equals("") && Integer.parseInt(seats.getText().toString()) > 0) {
            man.setSeats(Integer.parseInt(seats.getText().toString()));
        } else {
            showAlert(getString(R.string.m_seats), getString(R.string.error), false);
            sw = true;
        }
        if (!min.getText().toString().equals("") && Double.parseDouble(min.getText().toString()) > 0) {
            man.setMin_price(Double.parseDouble(min.getText().toString()));
        } else {
            showAlert(getString(R.string.m_price), getString(R.string.error), false);
            sw = true;
        }
        if (!max.getText().toString().equals("") && Double.parseDouble(max.getText().toString()) > 0) {
            man.setMax_price(Double.parseDouble(max.getText().toString()));
        } else {
            showAlert(getString(R.string.m_price), getString(R.string.error), false);
            sw = true;
        }
        man.setImage(imageString);
        if (!sw) {
            if(!inEdit) {
                ArrayList<Review> reviews = new ArrayList<>();
                man.setReviews(reviews);
                /**
                 * Set the new profile as the current manager.
                 */
                sharedPreferencesHandler.storeCurrentManager(man.toJSONObject());
                JSONObject jo = man.toJSONObjectTrans();
                jsonArray.put(jo.toString());
                sharedPreferencesHandler.storeListManager(jsonArray.toString());

                /**
                 * Save the profile in the database, log the manager and launch the profile activity.
                 */
                ProgressDialog dialog = new ProgressDialog(CreateManagerAccountActivity.this,
                        ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.AccountCreation));
                dialog.setCancelable(false);
                dialog.show();
                DatabaseUtils databaseUtils = ((StrawApplication) getApplication()).getDatabaseUtils();
                String password = c_pwd.getText().toString();

                /**
                 * Create a new user and save the profile in the database
                 */
                databaseUtils.createUser(man.getEmail(), password,
                        SharedPreferencesHandler.MANAGER, dialog);

            }else{
                sharedPreferencesHandler.storeCurrentManager(man.toJSONObject());
                JSONObject jo = man.toJSONObjectTrans();
                jsonArray.put(jo.toString());
                sharedPreferencesHandler.storeListManager(jsonArray.toString());
                ProgressDialog dialog = new ProgressDialog(CreateManagerAccountActivity.this,
                        ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.AccountEdition));
                dialog.setCancelable(false);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
                DatabaseUtils databaseUtils = ((StrawApplication) getApplication()).getDatabaseUtils();
                String emailAddress = email.getText().toString();
                String oldpassword = c_pwd.getText().toString();
                String password = cc_pwd.getText().toString();
                databaseUtils.editUser(old_email,emailAddress, oldpassword, password,
                        SharedPreferencesHandler.MANAGER, dialog);

            }

        } else {
            return;
        }
    }
}
