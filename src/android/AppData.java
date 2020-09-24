package cordova.plugin.appcheck;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by "Manoj Waghmare" on 24,Jul,2020
 **/

@Entity
public class AppData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "name")
    public String name;
}

