package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameSaveDao {
    @Query("SELECT * FROM game_save WHERE id = 1 LIMIT 1")
    suspend fun getSavedGame(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(save: GameSaveEntity)

    @Query("DELETE FROM game_save WHERE id = 1")
    suspend fun clearSave()
}
