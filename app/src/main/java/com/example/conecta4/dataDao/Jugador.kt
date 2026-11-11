package com.example.conecta4.dataDao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugadores")
data class Jugador(@PrimaryKey val gamerTag: String,
                   var wins : Int,
                   var looses: Int,
                   var draws : Int,
                   var jugadas : Int
) {

    companion object {
        fun gamertagExiste(gamerTag: String, lista: List<Jugador>): Boolean {
            return lista.any() { it.gamerTag == gamerTag }
        }
    }
}