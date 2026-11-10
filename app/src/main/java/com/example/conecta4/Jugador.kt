package com.example.conecta4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Players")
data class Jugador(@PrimaryKey val gamerTag: String,
    var wins : Int,
    var looses: Int,
    var draws : Int,
    var jugadas : Int
) {

    companion object {
        fun gamertagExiste(gamerTag: String, lista: List<Jugador>): {
            return lista.any() { it.gamerTag == gamerTag }
        }
    }
}

