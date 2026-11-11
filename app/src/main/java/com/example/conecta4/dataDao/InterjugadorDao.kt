package com.example.conecta4.dataDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao

interface InterjugadorDao {

    @Query("SELECT * FROM jugadores ORDER BY wins ASC")
    suspend fun getJugadores() : List<Jugador>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOactualizar(jugador: Jugador)

    @Query("DELETE FROM jugadores WHERE gamerTag = :gamerTag")
    suspend fun eliminarPorgamerTag(gamerTag: String)

    @Query("SELECT * FROM jugadores WHERE gamerTag= :gamerTag LIMIT 1")
    suspend fun buscarPorGamerTag(gamerTag: String): Jugador?

}