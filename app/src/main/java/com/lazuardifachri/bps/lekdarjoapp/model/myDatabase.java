package com.lazuardifachri.bps.lekdarjoapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.lazuardifachri.bps.lekdarjoapp.model.dao.FileModelDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.GraphDataDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.IndicatorDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.InfographicDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.PublicationDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.StatisticalNewsDao;

@Database(entities = {Publication.class, FileModel.class,
        StatisticalNews.class, Indicator.class, Infographic.class,
        GraphData.class}, version = 13, exportSchema = false)
@TypeConverters({GraphConverter.class})
public abstract class myDatabase extends RoomDatabase {

    private static myDatabase instance;

    public static myDatabase getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    myDatabase.class,
                    "database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract PublicationDao publicationDao();

    public abstract FileModelDao fileModelDao();

    public abstract StatisticalNewsDao statisticalNewsDao();

    public abstract IndicatorDao indicatorDao();

    public abstract InfographicDao infographicDao();

    public abstract GraphDataDao graphDataDao();
}
