package straw.polito.it.straw.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.ImageManager;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView menu;
    private TextView book;
    private TextView preorder;
    private TextView add_rev;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        Manager man =new Manager(getIntent().getExtras().getString("res"));
        initialize();
        name.setText(man.getRes_name());
        price.setText("SET PRICE");
        ImageManager.setImage(this, img, Uri.parse(man.getImage()));
        setListeners();
    }

    private void setListeners() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),CreateMenuActivity.class);
                i.putExtra("active",false);
                startActivity(i);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),BookTableActivity.class);
                startActivity(i);
            }
        });
        preorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),PreOrderFoodActivity.class);
                startActivity(i);
            }
        });
    }

    private void initialize() {
        img=(ImageView)findViewById(R.id.img);
        name=(TextView)findViewById(R.id.name);
        price=(TextView)findViewById(R.id.price);
        menu=(TextView)findViewById(R.id.menu);
        book=(TextView)findViewById(R.id.booking);
        preorder=(TextView)findViewById(R.id.preorder);
        add_rev=(TextView)findViewById(R.id.add_review);
    }
}
