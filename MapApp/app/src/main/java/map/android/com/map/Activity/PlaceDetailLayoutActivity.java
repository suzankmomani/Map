package map.android.com.map.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import map.android.com.map.Manager.ImageManager;
import map.android.com.map.Objects.Place;
import map.android.com.map.R;

public class PlaceDetailLayoutActivity extends AppCompatActivity {

    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_details_layout);

        initAppBar();
        getData();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    private void getData() {
        mPlace = (Place) getIntent().getExtras().getSerializable(MainActivity.PLACE);
    }

    private void initViews() {
        final ImageView image = (ImageView) findViewById(R.id.image);
        final TextView name = (TextView) findViewById(R.id.name);
        final TextView desc = (TextView) findViewById(R.id.desc);
        final RatingBar ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        final TextView numberOfRatings = (TextView) findViewById(R.id.numberOfRatings);
        TextView ratingTxt = (TextView) findViewById(R.id.ratingTxt);
        TextView longDescTxt = (TextView) findViewById(R.id.longDescTxt);

        ImageManager.loadImg(
                PlaceDetailLayoutActivity.this,
                mPlace.getImage(), image);

        name.setText(mPlace.getName());
        desc.setText(mPlace.getDesc());
        ratingbar.setRating(Float.parseFloat(mPlace.getRating()));
        numberOfRatings.setText("(" + mPlace.getNoOfRatings() + ")");
        ratingTxt.setText(mPlace.getRating());
        longDescTxt.setText(mPlace.getLongDesc());

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
