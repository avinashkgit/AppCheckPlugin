package cordova.plugin.appcheck;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by "Manoj Waghmare" on 24,Jul,2020
 **/

    @Database(entities = {AppData.class}, version = 1, exportSchema = false)
    abstract class AppDatabase extends RoomDatabase {
        public abstract AppDao appDao();

        private static AppDatabase INSTANCE;

        public static AppDatabase getAppDatabase(Context context) {
            if (INSTANCE == null) {
                //Allowing the room database to run on MainThread
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database").allowMainThreadQueries().build();

            }
            return INSTANCE;
        }

        public static void destroyInstance() {
            INSTANCE = null;
        }
    }

