package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.TimeContainer;
import straw.polito.it.straw.TimeDisplayer;
import straw.polito.it.straw.adapter.ReservationAdapterManager;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.fragments.TimePickerFragment;
import straw.polito.it.straw.services.RegistrationIntentService;
import straw.polito.it.straw.utils.Area;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.TimerDisplay;

public class CreateUserAccountActivity extends AppCompatActivity implements TimeContainer {

    private ImageView photo;
    private EditText c_pwd;
    private EditText cc_pwd;
    private EditText email;
    private Spinner areaSpinner;
    private Spinner u_d;
    private Spinner u_t;
    private TimerDisplay prefTime;
    private Button c_acc_button;
    private TextView n_pwd;
    private TextView o_pwd;
    private PopupWindow popUp;

    private Bitmap bitmap;
    private Area[] areas;

    private Uri photo_uri;

    private List<String> u_t_list;
    private List<String> u_d_list;
    private List<String> p_t_list;
    private SharedPreferencesHandler sharedPreferencesHandler;
    private static final int IMAGE_REQ = 1;
    private static final int CAMERA_REQ = 2;
    private User user;
    private boolean sw;
    private boolean onEdit;
    private String old_email;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Create_User_Account));
            setSupportActionBar(toolbar);
        }

        sharedPreferencesHandler = ((StrawApplication)getApplication()).getSharedPreferencesHandler();

        StrawApplication application = (StrawApplication)getApplication();
        SharedPreferencesHandler sharedPreferencesHandler = application.getSharedPreferencesHandler();
        this.areas = sharedPreferencesHandler.getAreaList();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(getIntent().hasExtra("user")){
            user=new User(getIntent().getExtras().getString("user"));
            onEdit=true;
            old_email=user.getEmail();
            initialize();
            setListeners();
            loadPrevInfo(user);

        }else{
            user=new User();
            onEdit=false;
            initialize();
            setListeners();
            setPhoto();
        }
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

    }

    private void loadPrevInfo(User user) {
        ImageManager.setImage(getApplicationContext(), photo, user.getImage());
        email.setText(user.getEmail());
        for (int i = 0; i < areas.length; i++) {
            if (areas[i].getName().equals(user.getUniversity())) {
                areaSpinner.setSelection(i);
                break;
            }
        }
        u_d.setSelection(u_d_list.indexOf(user.getDiet()));
        u_t.setSelection(u_t_list.indexOf(user.getDiet()));
        c_acc_button.setText(getString(R.string.save));
    }

    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        c_pwd=(EditText)findViewById(R.id.pwd_editText);
        cc_pwd=(EditText)findViewById(R.id.cc_pwd_editText);
        areaSpinner = (Spinner)findViewById(R.id.areaSpinner);

        u_t=(Spinner)findViewById(R.id.u_t_spinner);
        u_d=(Spinner)findViewById(R.id.u_d_spinner);
        prefTime = (TimerDisplay)findViewById(R.id.prefTime);
        email=(EditText)findViewById(R.id.email_editText);
        c_acc_button=(Button)findViewById(R.id.create_button);
        setUpPopUpWindow();
        o_pwd= (TextView) findViewById(R.id.pwd_textView);
        n_pwd= (TextView) findViewById(R.id.c_pwd_textView);
        if(onEdit){
            o_pwd.setText(getString(R.string.o_pwd));
            o_pwd.setText(getString(R.string.o_pwd));
            n_pwd.setText(getString(R.string.n_pwd));
        }
        u_t_list=new ArrayList<>();
        u_d_list=new ArrayList<>();
        p_t_list=new ArrayList<>();

        u_t_list.add(getString(R.string.student));
        u_t_list.add(getString(R.string.professor));
        u_t_list.add(getString(R.string.worker));

        u_d_list.add(getString(R.string.Vegan));
        u_d_list.add(getString(R.string.Gluten_free));
        u_d_list.add(getString(R.string.Nothing));

        for(int i=0;i<5;i++){
            p_t_list.add((11+i)+":00");
            p_t_list.add((11+i)+":30");
        }
        u_t.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, u_t_list));
        u_d.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, u_d_list));
        areaSpinner.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, areas));
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean("tokenSW", false);
                if (sentToken) {
                   user.setTokenGCM(sharedPreferences.getString("tokenGCM","Error"));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("complete"));
    }
    private void setListeners() {

        this.prefTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putBoolean(ReservationAdapterManager.ADAPTER, false);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "TimePicker");
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.showAsDropDown(v, 0, 0);
            }
        });

        c_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sw = false;
                if (!email.getText().toString().equals("")) {
                    user.setEmail(email.getText().toString());
                } else {
                    showAlert(getString(R.string.m_email), getString(R.string.error), false);
                    sw = true;
                }
                if(!onEdit) {
                    if (c_pwd.getText().toString().equals("") || !c_pwd.getText().toString().equals(cc_pwd.getText().toString())) {
                        showAlert(getString(R.string.m_pwd), getString(R.string.error), false);
                        sw = true;
                    }
                }
                user.setUniversity(areas[areaSpinner.getSelectedItemPosition()].getName());
                user.setDiet(u_d_list.get(u_d.getSelectedItemPosition()));
                user.setType(u_t_list.get(u_t.getSelectedItemPosition()));
                user.setPrefTimeHour(prefTime.getHourOfDay());
                user.setPrefTimeMinutes(prefTime.getMinutes());
                if (photo_uri != null)
                    user.setImage(ImageManager.getImageFromUri(getApplicationContext(), photo_uri));

                if (!sw) {
                    if(!onEdit) {
                        /**
                         * Save the new profile as the current user
                         */
                        sharedPreferencesHandler.storeCurrentUser(user.toString());
                        /**
                         * Create the new user account in the database, store the profile, log in and
                         * launch the proper activity.
                         */
                        ProgressDialog dialog = new ProgressDialog(CreateUserAccountActivity.this, ProgressDialog.STYLE_SPINNER);
                        dialog.setIndeterminate(true);
                        dialog.setMessage(getResources().getString(R.string.AccountCreation));
                        dialog.setCancelable(false);
                        dialog.show();
                        DatabaseUtils databaseUtils = ((StrawApplication) getApplication()).getDatabaseUtils();
                        String emailAddress = email.getText().toString();
                        String password = c_pwd.getText().toString();
                        databaseUtils.createUser(emailAddress, password, SharedPreferencesHandler.USER, dialog);
                    }else{
                        /**
                         * Save the new profile as the current user
                         */

                        sharedPreferencesHandler.storeCurrentUser(user.toString());
                        ProgressDialog dialog = new ProgressDialog(CreateUserAccountActivity.this, ProgressDialog.STYLE_SPINNER);
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
                        databaseUtils.editUser(old_email,emailAddress, oldpassword,password, SharedPreferencesHandler.USER, dialog);

                    }
                } else {
                    return;
                }
            }
        });


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void showAlert(String message, String title, final boolean ex){
        AlertDialog alertDialog = new AlertDialog.Builder(CreateUserAccountActivity.this).create();
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
        bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        photo.setImageBitmap(bitmap);
        photo_uri=Uri.parse("android.resource://straw.polito.it.straw/drawable/no_image");
    }
    private void setUpPopUpWindow() {
        popUp = new PopupWindow(getBaseContext());
        final List<String> listOpt=new ArrayList<>();
        listOpt.add(getString(R.string.Choose_photo));
        listOpt.add(getString(R.string.Take_photo));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateUserAccountActivity.this, android.R.layout.simple_list_item_1, listOpt);
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
                    photo_uri = uriSavedImage;
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
        if(data != null && resultCode == RESULT_OK && requestCode == IMAGE_REQ){
            sw = false;
            Uri image = data.getData();
            photo_uri = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                bitmap = Bitmap.createScaledBitmap(bitmap,photo.getHeight(),photo.getWidth(),true);
                photo.setImageBitmap(bitmap);
            }catch(IOException e){
                Logger.d("Error on Activity result! " + e.getMessage());
            }
        }
        if( requestCode== CAMERA_REQ){
            sw = true;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photo_uri);
                bitmap=Bitmap.createScaledBitmap(bitmap, photo.getHeight(), photo.getWidth(), true);
                photo.setImageBitmap(bitmap);
            } catch(Exception e){
                Logger.d("Error on Activity result! " + e.getMessage());
            }
        }
    }

    @Override
    public TimeDisplayer getTimeDisplayer() {
        return this.prefTime;
    }
}
