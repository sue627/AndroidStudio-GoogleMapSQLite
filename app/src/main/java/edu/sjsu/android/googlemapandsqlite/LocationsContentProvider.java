package edu.sjsu.android.googlemapandsqlite;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationsContentProvider extends ContentProvider {
    /* 3a-b Define any variable as needed. */
    static final String PROVIDER_NAME = "edu.sjsu.android.googlemapandsqlite.locations";
    static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations");
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PROVIDER_NAME, "locations", 1);
    }

    private LocationsDB locationsDB;

    @Override
    public boolean onCreate() {
        locationsDB = new LocationsDB(getContext());
        return true;
    }

    /* 4a. A callback method that is invoked when insert operation is requested on this content provider */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = locationsDB.insert(values);
        if (rowID > 0) {
            Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        } else throw new SQLException("Unknown URI " + uri);
    }

    /* 4b. A callback method that is invoked when delete operation is requested on this content provider. */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return locationsDB.deleteAll();
    }

    /* 4c. A callback method that is invoked by default content URI. */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == 1)
            return locationsDB.getAllLocations();
        else
            throw new SQLException("Unknown URI " + uri);
    }

    /* Unused methods, keep as default */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}