package com.gu.galarm.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


/**
 * Dao to interact with database at the lowest level
 */
@Dao
public interface AlarmDao {
    @Query("select * from alarms")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarms WHERE alarm_id=:id")
    Alarm getById(int id);

    @Insert(onConflict = IGNORE)
    void insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("UPDATE alarms set alarm_active=:active where alarm_id=:id")
    void updateActive(int id, boolean active);

    @Insert(onConflict = REPLACE)
    void insertOrReplaceAlarm(Alarm alarm);

    @Query("DELETE FROM alarms")
    void deleteAll();
}
