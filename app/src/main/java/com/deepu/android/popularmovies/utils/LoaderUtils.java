package com.deepu.android.popularmovies.utils;

import android.database.Cursor;

public final class LoaderUtils {

    public static final int FAVORITE_MOVIES_LOADER = 60;

    public static String getStringFromCursor(Cursor cursor, String colName) {
        return cursor.getString(cursor.getColumnIndex(colName));
    }

    public static byte[] getBlob(Cursor cursor, String colName) {
        return cursor.getBlob(cursor.getColumnIndex(colName));
    }
}
