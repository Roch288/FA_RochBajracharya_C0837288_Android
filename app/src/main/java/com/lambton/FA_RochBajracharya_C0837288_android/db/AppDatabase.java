package com.lambton.FA_RochBajracharya_C0837288_android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lambton.FA_RochBajracharya_C0837288_android.model.Place;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}
