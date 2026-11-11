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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conecta4.dataDao.Jugador
import com.example.conecta4.database.AppDatabase
import com.example.conecta4.ui.theme.Conecta4Theme
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border

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
                        navController.navigate("tablero_conecta/${jugador1.gamerTag}/${jugador2.gamerTag}")
                    }
                },
                onIrAEstadisticas = {
                    if (NavigationGuard.canNavigate()) {
                        navController.navigate("Estadisticas")
                    }
                }
            )
        }


            composable("tablero_conecta/{j1}/{j2}") { backStackEntry ->
                val player1 = backStackEntry.arguments?.getString("j1") ?: "Jugador 1"
                val player2 = backStackEntry.arguments?.getString("j2") ?: "Jugador 2"
                PantallaTablero(
                    nombreJ1 = player1,
                    nombreJ2 = player2,
                    dao = dao,
                    onHome = {
                    navController.navigate("Principal") {
                        popUpTo("Principal") { inclusive = true }
                    }
                }
            )
        }

        composable("Estadisticas") {
            var listaPlayers by rememberSaveable { mutableStateOf<List<Jugador>>(emptyList()) }
            LaunchedEffect(Unit){
                listaPlayers = dao.getJugadores()
            }
            PantallaEstadisticas(
                listaJugadores = listaPlayers,
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
    var jugador1Select by remember {mutableStateOf<Jugador?>(null) }
    var jugador2Select by remember  { mutableStateOf<Jugador?>(null) }

    var selectExpandido1 by remember  { mutableStateOf(false) }
    var selectExpandido2 by remember  { mutableStateOf(false) }



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
                value = jugador2Select?.gamerTag?:"Selecciona Jugador 2",
                onValueChange = {},
                readOnly = true,

                label = {Text("Jugador 2") },
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
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).InterjugadorDao() }
    val scope = rememberCoroutineScope()

    var listaActual by remember { mutableStateOf(listaJugadores) }
    var nombre_player by rememberSaveable { mutableStateOf("") }
    var mensaje_error by rememberSaveable { mutableStateOf("") }

    // Actualizar la lista local al cargar
    LaunchedEffect(Unit) {
        listaActual = dao.getJugadores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registro de jugador",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre_player,
            onValueChange = { nombre_player = it.trim() },
            label = { Text("GamerTag") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                mensaje_error = ""

                if (nombre_player.isBlank()) {
                    mensaje_error = "Llena el nombre"
                    return@Button
                }

                scope.launch {
                    val actuales = dao.getJugadores()
                    if (Jugador.gamertagExiste(nombre_player, actuales)) {
                        mensaje_error = "El GamerTag '$nombre_player' ya existe"
                    } else {
                        val nuevoJugador = Jugador(
                            gamerTag = nombre_player,
                            wins = 0,
                            looses = 0,
                            draws = 0,
                            jugadas = 0
                        )
                        dao.insertarOactualizar(nuevoJugador)
                        nombre_player = ""
                        //pa que el deste haga refresco de coca cola
                        listaActual = dao.getJugadores()
                    }
                }
            }
        ) {
            Text("Guardar Jugador")
        }

        // mensaje wrong
        if (mensaje_error.isNotEmpty()) {
            Text(
                text = mensaje_error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Jugador", modifier = Modifier.weight(2f))
            Text(text = "W", modifier = Modifier.weight(1f))
            Text(text = "L", modifier = Modifier.weight(1f))
            Text(text = "P", modifier = Modifier.weight(1f))
            Text(text = "E", modifier = Modifier.weight(1f))
        }

       //lista de chavales
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            //mostar destes por name
            items(listaActual, key = { it.gamerTag }) { jugador ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = jugador.gamerTag, modifier = Modifier.weight(2f))
                    Text(text = "${jugador.wins}", modifier = Modifier.weight(1f))
                    Text(text = "${jugador.looses}", modifier = Modifier.weight(1f))
                    Text(text = "${jugador.jugadas}", modifier = Modifier.weight(1f))
                    Text(text = "${jugador.draws}", modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // backeo
        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}



@Composable
fun PantallaTablero(
    nombreJ1: String,
    nombreJ2: String,
    dao: com.example.conecta4.dataDao.InterjugadorDao,
    onHome: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var tablero by remember { mutableStateOf(Array(6) { IntArray(6) { 0 } }) }
    var turnoJ1 by remember { mutableStateOf(true) }
    var juegoEnCurso by remember { mutableStateOf(true) }
    var ganador by remember { mutableStateOf<String?>(null) }

        //aca hace el registro en la base data
    fun registrarResultadoBD(ganadorNombre: String?, esEmpate: Boolean) {
        scope.launch {
            val lista = dao.getJugadores()
            val p1 = lista.find { it.gamerTag == nombreJ1 }
            val p2 = lista.find { it.gamerTag == nombreJ2 }

            if (p1 != null && p2 != null) {
                //empatar
                if (esEmpate) {
                    dao.insertarOactualizar(p1.copy(draws = p1.draws + 1, jugadas = p1.jugadas + 1))
                    dao.insertarOactualizar(p2.copy(draws = p2.draws + 1, jugadas = p2.jugadas + 1))
                    //win del plei 1
                } else if (ganadorNombre == nombreJ1) {
                    dao.insertarOactualizar(p1.copy(wins = p1.wins + 1, jugadas = p1.jugadas + 1))
                    dao.insertarOactualizar(p2.copy(looses = p2.looses + 1, jugadas = p2.jugadas + 1))
                    //win plei 2
                } else if (ganadorNombre == nombreJ2) {
                    dao.insertarOactualizar(p1.copy(looses = p1.looses + 1, jugadas = p1.jugadas + 1))
                    dao.insertarOactualizar(p2.copy(wins = p2.wins + 1, jugadas = p2.jugadas + 1))
                }
            }
        }
    }

    //basicamente solo checamos si tiene espacio pues no empate si se lleno empate
    fun comprobarEmpate(tab: Array<IntArray>): Boolean {
        for (col in 0..5) {
            if (tab[0][col] == 0) return false
        }
        return true
    }

    fun comprobarVictoria(tab: Array<IntArray>, fila: Int, col: Int, jugador: Int): Boolean {
        fun contar(dFila: Int, dCol: Int): Int {
            var count = 0
            var row = fila + dFila
            var c = col + dCol
            while (row in 0..5 && c in 0..5 && tab[row][c] == jugador) {
                count++
                row += dFila
                c += dCol
            }
            return count
        }

        val horizontal = 1 + contar(0, 1) + contar(0, -1)
        val vertical = 1 + contar(1, 0) + contar(-1, 0)
        val diag1 = 1 + contar(1, 1) + contar(-1, -1)
        val diag2 = 1 + contar(1, -1) + contar(-1, 1)

        return horizontal >= 4 || vertical >= 4 || diag1 >= 4 || diag2 >= 4
    }

    fun tirarEnColumna(columna: Int) {
        //si no hay un geim
        if (!juegoEnCurso) return
        //el down to es basicamente un pusheo a la pila que va hasta la primera que tenga espacio vacia
        for (fila in 5 downTo 0) {
            if (tablero[fila][columna] == 0) {
                //hace tablero niu clonando
                val nuevoTablero = Array(6) { f -> tablero[f].clone() }
                val jugadorActual = if (turnoJ1) 1 else 2

                nuevoTablero[fila][columna] = jugadorActual
                tablero = nuevoTablero
                if (comprobarVictoria(nuevoTablero, fila, columna, jugadorActual)) {
                    val ganadorActual = if (turnoJ1) nombreJ1 else nombreJ2
                    ganador = ganadorActual
                    juegoEnCurso = false
                    registrarResultadoBD(ganadorNombre = ganadorActual, esEmpate = false)
                } else if (comprobarEmpate(nuevoTablero)) {
                    ganador = null
                    juegoEnCurso = false
                    registrarResultadoBD(ganadorNombre = null, esEmpate = true)
                } else {
                    turnoJ1 = !turnoJ1
                }
                break
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (juegoEnCurso) {
                val nombreTurno = if (turnoJ1) nombreJ1 else nombreJ2
                Text(
                    text = "Turno de: $nombreTurno",
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Text(
                    text = if (ganador != null) "¡Ganador: $ganador!" else "Empate",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (col in 0..5) {
                Button(
                    onClick = { tirarEnColumna(col) },
                    enabled = juegoEnCurso && tablero[0][col] == 0,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("▼")
                }
            }
        }

        Column(
            modifier = Modifier
                .background(Color.Blue)
                .padding(8.dp)
        ) {
            for (fila in 0..5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..5) {
                        val colorFicha = when (tablero[fila][col]) {
                            1 -> Color.Red
                            2 -> Color.Yellow
                            else -> Color.White
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .background(colorFicha, shape = CircleShape)
                                .border(1.dp, Color.Black, CircleShape)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //de
            Button(
                onClick = {
                    tablero = Array(6) { IntArray(6) { 0 } }
                    turnoJ1 = true
                    juegoEnCurso = true
                    ganador = null
                },
                enabled = !juegoEnCurso
            ) {
                Text("Iniciar")
            }

            Button(
                onClick = {
                    if (juegoEnCurso) {
                        val ganadorRendicion = if (turnoJ1) nombreJ2 else nombreJ1
                        ganador = ganadorRendicion
                        juegoEnCurso = false
                        registrarResultadoBD(ganadorNombre = ganadorRendicion, esEmpate = false)
                    }
                },
                enabled = juegoEnCurso
            ) {
                Text("Rendirse")
            }

            Button(
                onClick = onHome,
                enabled = !juegoEnCurso
            ) {
                Text("Cambiar")
            }
        }
    }
}