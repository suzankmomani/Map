package map.android.com.map.Activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;

import map.android.com.map.Fragment.ItemFragment;
import map.android.com.map.Manager.DataManager;
import map.android.com.map.Manager.DbQueryManager;
import map.android.com.map.Objects.Place;
import map.android.com.map.R;
import map.android.com.map.Utils.UiUtils;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, ItemFragment.OnListFragmentInteractionListener {

    public static final String PLACE = "place";
    private final String SEARCH_FRAGMENT = "SEARCH_FRAGMENT";
    private GoogleMap mMap;
    private List<Place> mAllPlaces;
    private SearchView mSearchView;
    private Place mSelectedPlace;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mSelectedPlace = (Place) savedInstanceState.getSerializable(PLACE);
        }
        getData();
        initSearchView();
        initViews();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
             /*go back to main fragment and view selected place, or no match found*/
            List<Place> placeList = DbQueryManager.getPlacesByName(MainActivity.this, query);
            submitSearchAndZoomIfExist(placeList.size() == 0 ? null : placeList.get(0));

        }
    }

    private void getData() {
        mAllPlaces = DataManager.prepareData(this);
    }

    private void initSearchView() {
        /*initialize the searchView widget behavior (click, close button click and all necessary events)*/
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        final TextView searchText = (TextView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        final ImageView closeBtn = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        final View searchBar = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_bar);
        ImageView collapsedSearchIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_button);

        collapsedSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if search view clicked create the fragment to display the search result if not already created*/
                ItemFragment itemFragment = (ItemFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
                if (itemFragment == null) {
                    mSearchView.onActionViewExpanded();
                    startSearchList();
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemFragment itemFragment = (ItemFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);

                /*if at main view clear the text and collapse searchview*/
                if (itemFragment == null) {
                    if (searchText.getText().toString().equals("")) {
                        mSearchView.setOnQueryTextListener(null);
                        UiUtils.hideVirtualKeyboard(MainActivity.this);
                    }
                    collapseSearchView();
                }

                /* if at list result view only clear the text */
                searchText.setText("");
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
               /* if search view is focused and there is text in it
                start the search list result view */
                if (b) {
                    ItemFragment itemFragment = (ItemFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
                    if (itemFragment == null) {
                        if (!searchText.getText().toString().equals("")) {
                            startSearchList();
                        }
                    }
                }
            }
        });

    }

    private void setQuerySearchListners() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*go back to main fragment and view selected place, or no match found*/
                List<Place> placeList = DbQueryManager.getPlacesByName(MainActivity.this, query);
                submitSearchAndZoomIfExist(placeList.size() == 0 ? null : placeList.get(0));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*search for matching places on each text change event*/
                ItemFragment itemFragment = (ItemFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
                if (itemFragment != null) {
                    refreshSearchList(itemFragment, newText);
                }
                return true;
            }
        });

    }


    private void startSearchList() {
        /*add a search list fragment to the stack*/
        if (getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT) == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.replace(R.id.searchResultFragmentContainer, ItemFragment.newInstance(1), SEARCH_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void refreshSearchList(ItemFragment itemFragment, String newText) {
        /*fill data in the list */
        itemFragment.refreshAdapter(newText);
    }

    private void removeSearchListFragment() {
        ItemFragment itemFragment = (ItemFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
        if (itemFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(itemFragment).commitAllowingStateLoss();
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        collapseSearchView();

    }

    private void collapseSearchView() {
        mSearchView.onActionViewCollapsed();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapClick();
            }
        });

        drawMarkers();
    }

    private void drawMarkers() {
        for (final Place place :
                mAllPlaces) {
            // Add a marker for each place fetched from the database
            LatLng loc = new LatLng(Double.valueOf(place.getLocation().getLat()),
                    Double.valueOf(place.getLocation().getLng()));
            mMap.addMarker(new MarkerOptions().position(loc).title(place.getName()));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mSelectedPlace = DbQueryManager.getPlacesByName(MainActivity.this, marker.getTitle()).get(0);
                    fillPlaceDetailView();
                    showSearchView();
                    return false;
                }
            });

        }
        if (mAllPlaces.size() > 0) {
            LatLng loc = new LatLng(Double.valueOf(mAllPlaces.get(0).getLocation().getLat()),
                    Double.valueOf(mAllPlaces.get(0).getLocation().getLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    @Override
    public void onItemSelected(Place place) {
        submitSearchAndZoomIfExist(place);
    }

    @Override
    public void onSelectCompleteItemIconFromList(String newStr) {
        mSearchView.setQuery(newStr, false);
    }

    @Override
    public void fragmentCreated() {
        setQuerySearchListners();
    }


    private void submitSearchAndZoomIfExist(Place place) {
        if (place == null) {
            Toast.makeText(this, getResources().getString(R.string.no_match_found), Toast.LENGTH_LONG).show();
        } else {
            mSelectedPlace = place;
            mSearchView.setQuery(place.getName(), false);
            closeSearchList();
            zoomToPlace(place);
            fillPlaceDetailView();
        }
    }

    private void closeSearchList() {
        mSearchView.clearFocus();
        FrameLayout mainLayout = (FrameLayout) findViewById(R.id.mainView);
        mainLayout.requestFocus();
        UiUtils.hideVirtualKeyboard(this);
        removeSearchListFragment();
    }

    private void zoomToPlace(Place place) {
        LatLng loc = new LatLng(Double.valueOf(place.getLocation().getLat()),
                Double.valueOf(place.getLocation().getLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(loc));

    }


    private void initViews() {

        initLeftDrawerList();

        initLowerDetailPartView();
    }

    private void initLowerDetailPartView() {

        final LinearLayout placeDetailView = (LinearLayout) findViewById(R.id.placeDetailView);
        ImageView driveToImg = (ImageView) findViewById(R.id.driveToImg);

        placeDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelectedPlace != null) {
                    Intent intent = new Intent(MainActivity.this, PlaceDetailLayoutActivity.class);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    intent.putExtra(PLACE, (Serializable) mSelectedPlace);
                    startActivity(intent);
                }
            }
        });

        driveToImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelectedPlace != null) {

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?f=d&daddr=" + mSelectedPlace.getLocation().getLat() + "," +
                                    mSelectedPlace.getLocation().getLng()));
                    intent.setComponent(new ComponentName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity"));
                    startActivity(intent);

                }
            }
        });

        if (mSelectedPlace != null) {
            fillPlaceDetailView();
        }
    }

    private void initLeftDrawerList() {

        final ListView mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, getResources().getStringArray(R.array.drawer)));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(MainActivity.this, BlankActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fillPlaceDetailView() {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        TextView numberOfRatings = (TextView) findViewById(R.id.numberOfRatings);
        TextView placeNameTxt = (TextView) findViewById(R.id.placeNameTxt);
        TextView ratingTxt = (TextView) findViewById(R.id.ratingTxt);


        if (mSelectedPlace != null) {
            ratingBar.setRating(Float.parseFloat(mSelectedPlace.getRating()));
            numberOfRatings.setText("(" + mSelectedPlace.getNoOfRatings() + ")");
            placeNameTxt.setText(mSelectedPlace.getName());
            ratingTxt.setText(mSelectedPlace.getRating());
       }

        showPlaceDetailsView();

    }

    private void showPlaceDetailsView() {
        if (mSelectedPlace != null) {
            LinearLayout placeDetailView = (LinearLayout) findViewById(R.id.placeDetailView);
            ImageView driveToImg = (ImageView) findViewById(R.id.driveToImg);

            placeDetailView.animate().translationY(0);
            driveToImg.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlaceDetailsView() {
        LinearLayout placeDetailView = (LinearLayout) findViewById(R.id.placeDetailView);
        ImageView driveToImg = (ImageView) findViewById(R.id.driveToImg);

        driveToImg.setVisibility(View.GONE);
        placeDetailView.animate().translationY(UiUtils.dpToPx(this, 100));
    }

    private void showSearchView() {
        mSearchView.animate().translationY(0);
    }

    private void hideSearchView() {
        mSearchView.animate().translationY(-UiUtils.dpToPx(this, 100));
    }

    private void mapClick() {
        LinearLayout placeDetailView = (LinearLayout) findViewById(R.id.placeDetailView);
        if (placeDetailView.getTranslationY() == 0 && mSearchView.getTranslationY() == 0) {
            /*if both visible, hide them both*/
            hidePlaceDetailsView();
            hideSearchView();
        } else if ((placeDetailView.getTranslationY() != 0 && mSearchView.getTranslationY() != 0)) {
            /*if both invisible, show them both*/
            showPlaceDetailsView();
            showSearchView();
        } else {
            /*if either is visible show the other*/
            showPlaceDetailsView();
            showSearchView();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PLACE, mSelectedPlace);
    }
}
