package com.lazuardifachri.bps.lekdarjoapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lazuardifachri.bps.lekdarjoapp.model.dao.FileModelDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.IndicatorDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.PublicationDao;
import com.lazuardifachri.bps.lekdarjoapp.model.dao.StatisticalNewsDao;

@Database(entities = {Publication.class, FileModel.class,
        StatisticalNews.class, Indicator.class}, version = 9, exportSchema = false)
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

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
//                    + "`name` TEXT, PRIMARY KEY(`id`))");
//        }
//    };

    public abstract PublicationDao publicationDao();

    public abstract FileModelDao fileModelDao();

    public abstract StatisticalNewsDao statisticalNewsDao();

    public abstract IndicatorDao indicatorDao();
}
