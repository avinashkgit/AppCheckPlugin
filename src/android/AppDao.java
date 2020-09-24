package cordova.plugin.appcheck;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by "Manoj Waghmare" on 24,Jul,2020
 **/

    @Dao
    public interface AppDao {
        @Insert
        Long insert(AppData appData);

        @Query("SELECT * FROM `AppData`")
        List<AppData> getAllApps();

        @Query("SELECT * FROM `AppData` WHERE `name` =:app_name")
        AppData getAppByName(String app_name);

        @Update
        void update(AppData appData);

        @Delete
        void delete(AppData appData);
    }

