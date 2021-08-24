package edu.sjsu.android.googlemapandsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /* 5a. Complete onMayReady() method. */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
        LoaderManager.getInstance(this).initLoader(0, null, this);

        map.setOnMapClickListener(this::insert);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                delete();
            }
        });
    }

    /* 5b. Complete LocationInsertTask() method. */
    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {

        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            // Setting up values to insert the clicked location into SQLite database
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    /* 5c. Complete LocationInsertTask() method. */
    private class LocationDeleteTask extends AsyncTask<ContentValues, Void, Void> {

        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            // Deleting all the locations stored in SQLite database
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    /* 5d. Complete onCreateLoader() method. */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /* 5e. Complete onLoadFinished() method. */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        int locationCount;
        double lat = 0;
        double lng = 0;
        float zoom = 10; //0

        // Number of locations available in the SQLite database table
        if (arg1 != null) {
            locationCount = arg1.getCount();
            // Move the current record pointer to the first row of the table
            arg1.moveToFirst();
        } else locationCount = 0;

        for (int i = 0; i < locationCount; i++) {

            // Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.LAT_COLUMN));

            // Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.LNG_COLUMN));

            // Get the zoom level
            zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.ZOOM_LEVEL_COLUMN));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            map.addMarker(new MarkerOptions().position(location));

            // Traverse the pointer to the next row
            arg1.moveToNext();
        }

        if (locationCount > 0) {
            // Moving CameraPosition to last clicked position
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));

            // Setting the zoom level in the map on last position  is clicked
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    /**
     * Private helper method to insert a location (part of 5a).
     *
     * @param point The location
     */
    private void insert(LatLng point) {
        // Add a maker to the map
        map.addMarker(new MarkerOptions().position(point));

        // Creating an instance of LocationInsertTask
        LocationInsertTask insertTask = new LocationInsertTask();

        // Storing the latitude, longitude and zoom level to SQList database
        ContentValues newValues = new ContentValues();
        newValues.put(LocationsDB.LAT_COLUMN, point.latitude);
        newValues.put(LocationsDB.LNG_COLUMN, point.longitude);
        newValues.put(LocationsDB.ZOOM_LEVEL_COLUMN, map.getCameraPosition().zoom);
        insertTask.execute(newValues);

        // Display "Maker is added to the Map" message
        Toast.makeText(getApplicationContext(), "Marker is added to the map",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to delete all locations (part of 5a).
     */
    private void delete() {
        // Removing all markers from the Google Map
        map.clear();

        // Creating an instance of LocationDeleteTask
        LocationDeleteTask deleteTask = new LocationDeleteTask();

        // Deleting all the rows from SQLite database table
        deleteTask.execute();

        // Display "All makers are removed" message
        Toast.makeText(getApplicationContext(), "All makers are removed",
                Toast.LENGTH_SHORT).show();
    }

    /* Code from lab example number 2 */
    public void onClick_CS(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18);
        map.animateCamera(update);
    }

    public void onClick_Univ(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14);
        map.animateCamera(update);
    }

    public void onClick_City(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10);
        map.animateCamera(update);
    }

}