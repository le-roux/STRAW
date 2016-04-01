package straw.polito.it.straw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CreateAccount extends AppCompatActivity {

    ListView data_ListView;
    ImageView photo;
     Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initialize();
        setDataList();
        setPhoto();

    }

    private void setPhoto() {
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        photo.setImageBitmap(bitmap);
    }

    private void setDataList() {
        List<String> data = new ArrayList<>();
        data.add(getString(R.string.user_n));
        data.add(getString(R.string.email));
        data.add(getString(R.string.c_pwd));
        data.add(getString(R.string.cc_pwd));
        data.add(getString(R.string.addr));
        data.add(getString(R.string.tel));
        data.add(getString(R.string.r_name));
        data.add(getString(R.string.r_type));
        data.add(getString(R.string.seats));

        Adapter adapter= new CreateAccountAdapter(getBaseContext(),data);
        data_ListView.setAdapter((ListAdapter) adapter);
    }

    private void initialize() {
        data_ListView=(ListView)findViewById(R.id.data_listView);
        photo=(ImageView)findViewById(R.id.photo_imageView);
    }
}
