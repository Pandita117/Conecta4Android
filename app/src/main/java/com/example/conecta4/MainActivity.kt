package com.example.conecta4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conecta4.dataDao.Jugador
import com.example.conecta4.database.AppDatabase
import com.example.conecta4.ui.theme.Conecta4Theme
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Conecta4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()
                }
            }
        }
    }
}

// Controlador de clickeo
object NavigationGuard {
    private var lastClickTime = 0L
    private const val CLICK_INTERVAL = 800L

    fun canNavigate(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= CLICK_INTERVAL) {
            lastClickTime = currentTime
            return true
        }
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dao = remember { AppDatabase.getDatabase(context).InterjugadorDao() }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "Principal"
    ) {
        composable("Principal") {
            var listaPlayers by rememberSaveable { mutableStateOf<List<Jugador>>(emptyList()) }

            LaunchedEffect(Unit) {
                listaPlayers = dao.getJugadores()
            }

            PantallaPrincipal(
                listaJugadores = listaPlayers,
                onIniciarJuego = { jugador1, jugador2 ->
                    if (NavigationGuard.canNavigate()) {
                        navController.navigate("tablero_conecta")
                    }
                },
                onIrAEstadisticas = {
                    if (NavigationGuard.canNavigate()) {
                        navController.navigate("Estadisticas")
                    }
                }
            )
        }

        composable("tablero_conecta") {
            PantallaTablero(
                onHome = {
                    navController.navigate("Principal") {
                        popUpTo("Principal") { inclusive = true }
                    }
                }
            )
        }

        composable("Estadisticas") {
            PantallaEstadisticas(
                onHome = {
                    navController.navigate("Principal") {
                        popUpTo("Principal") { inclusive = true }
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    listaJugadores: List<Jugador>,
    onIniciarJuego: (Jugador, Jugador) -> Unit,
    onIrAEstadisticas: () -> Unit
) {
    var jugador1Select by rememberSaveable() {mutableStateOf<Jugador?>(null) }
    var jugador2Select by rememberSaveable() { mutableStateOf<Jugador?>(null) }

    var selectExpandido1 by rememberSaveable() { mutableStateOf(false) }
    var selectExpandido2 by rememberSaveable() { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pantalla principal",)

        Spacer(modifier = Modifier.height(32.dp))

        // ese es para el select del jugadoron
        ExposedDropdownMenuBox(
            expanded = selectExpandido1,
            onExpandedChange = {selectExpandido1 = !selectExpandido1 }
        ) {
            OutlinedTextField(
                value = jugador1Select?.gamerTag?:"Selecciona Jugador 1",
                onValueChange = {},
                readOnly = true,

                label = {Text("Jugador 1") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectExpandido1) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = selectExpandido1,
                onDismissRequest = {selectExpandido1 = false }

            ) {
                listaJugadores.forEach { jugador ->
                    DropdownMenuItem(
                        text = {Text(jugador.gamerTag) },
                        onClick = {
                            jugador1Select = jugador
                            selectExpandido1 = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        //menu del player tu
        ExposedDropdownMenuBox(
            expanded = selectExpandido2,
            onExpandedChange = {selectExpandido2 = !selectExpandido2 }
        ) {
            OutlinedTextField(
                value = jugador2Select?.gamerTag?:"Selecciona Jugador 1",
                onValueChange = {},
                readOnly = true,

                label = {Text("Jugador 1") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectExpandido2) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = selectExpandido2,
                onDismissRequest = {selectExpandido2 = false }

            ) {
                listaJugadores.forEach { jugador ->
                    DropdownMenuItem(
                        text = {Text(jugador.gamerTag) },
                        onClick = {
                            jugador2Select = jugador
                            selectExpandido2 = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        //aca para que no anden seleccionando el mismo chaval
        val botonHabilitado = jugador1Select != null && jugador2Select != null && jugador1Select != jugador2Select


        Button(
            onClick = {
                if (jugador1Select != null && jugador2Select != null){
                    //el  !! es po q no puede ser nulo
                    onIniciarJuego(jugador1Select!!,jugador2Select!!)
                }
            },
            enabled =  botonHabilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onIrAEstadisticas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Score")
        }
    }


}


@Composable
fun PantallaEstadisticas(
    listaJugadores: List<Jugador>,
    onHome : () -> Unit,
    ) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).InterjugadorDao() }
    val scope = rememberCoroutineScope()


    var nombre_player by rememberSaveable() { mutableStateOf("") }
    var victorias by rememberSaveable() { mutableStateOf(0)}
    var derrotas by rememberSaveable() { mutableStateOf(0) }
    var empates by rememberSaveable() {mutableStateOf(0) }
    var plays  by rememberSaveable() {mutableStateOf(0) }
    var mensaje_error by rememberSaveable() { mutableStateOf("")}


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registro de jugador",
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre_player,
            onValueChange = {
                //si el deste esta en la desta
                if (!Jugador.gamertagExiste(it,listaJugadores)) {
                    nombre_player = it
                } else{
                    mensaje_error = "El Gamertag ya existe"
                }
            },
            label = {Text("GamerTag") },
            modifier = Modifier.fillMaxWidth()
        )
        //para guardar al player
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                mensaje_error = ""

                if (nombre_player.isBlank()) {
                    mensaje_error = "LLena el nombre"
                    return@Button
                }
                //llamaa database
                scope.launch {
                    val listaActual = dao.getJugadores()

                    val jugadorGuardad = Jugador(
                        gamerTag = nombre_player,
                        wins =victorias,
                        looses = derrotas,
                        draws =  empates,
                        jugadas = plays
                    )

                    dao.insertarOactualizar(jugadorGuardad)
                }
            }
        ) {
           Text(mensaje_error)
        }

        //mostrar los chavales registrados
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

        }
    }
}
