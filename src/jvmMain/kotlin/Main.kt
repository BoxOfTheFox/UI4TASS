import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.MainScope
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private val scope = MainScope()
private val viewModel = MainViewModel()
private val playerProfiles = mapOf(
    "H. Kane" to "harrykane",
    "T. Alexander-Arnold" to "trentarnold66",
    "R. Sterling" to "sterling7",
    "K. Walker" to "kylewalker2",
    "P. Foden" to "philfoden",
    "R. James" to "reecejames",
    "J. Grealish" to "jackgrealish",
    "K. Trippier" to "ktrippier2",
    "D. Rice" to "declanrice",
    "J. Maddison" to "madders",
    "B. Saka" to "bukayosaka87",
    "M. Mount" to "masonmount",
    "A. Robertson" to "andyrobertson94",
    "J. Stones" to "johnstonesofficial",
    "J. Vardy" to "vardy7",
    "A. Ramsdale" to "aaronramsdale",
    "B. Chilwell" to "benchilwell",
    "J. Pickford" to "jpickford1",
    "J. Henderson" to "jhenderson",
    "J. Gomez" to "joegomez5",
    "K. Phillips" to "kalvinphillips",
    "M. Rashford" to "marcusrashford",
    "A. Wan-Bissaka" to "awbissaka",
)
private val client = OkHttpClient()

@Composable
@Preview
fun App() {
    var navigationTarget by rememberSaveable { mutableStateOf(Navigation.Main) }
    var chosenPlayer by rememberSaveable { mutableStateOf<Row?>(null) }

    MaterialTheme {
        when (navigationTarget) {
            Navigation.Main -> MainScreen { navigation, row ->
                navigationTarget = navigation
                chosenPlayer = row
            }

            Navigation.Detail -> DetailScreen(chosenPlayer!!) { navigation, row ->
                navigationTarget = navigation
                chosenPlayer = row
            }
        }
    }
}

@Composable
fun MainScreen(onClick: (Navigation, Row) -> Unit) {
    val sofifaState by viewModel.sofifaState.collectAsState()
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sofifaState.toRows()) {
            MainCard(modifier = Modifier.fillMaxWidth(), row = it) { onClick(Navigation.Detail, it) }
        }
    }
}

@Composable
fun DetailScreen(row: Row, onClick: (Navigation, Row?) -> Unit) {
    val request = Request.Builder()
        .url("http://localhost:5000/similar/${playerProfiles[row.name]}")
        .build()

    val call: Call = client.newCall(request)
    val response: Response = call.execute()
    val names = if (response.code() == 200) {
        val similarProfiles = response.body()?.string()?.split(",")
        playerProfiles.filter { (_, value) -> similarProfiles!!.contains(value) }
    } else {
        emptyMap()
    }

    val testState by viewModel.testState.collectAsState()
    val randomSofifaRows = viewModel.sofifaState.value.toRows().filter { r -> names.contains(r.name) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { viewModel.clearTest(); onClick(Navigation.Main, null) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, "Home")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            MainCard(modifier = Modifier.weight(3f), row = row) {}
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { viewModel.test("file.py", "u", "a") },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, "Home")
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 32.dp)) {
            items(randomSofifaRows) {
                Row {
                    MainCard(modifier = Modifier.weight(2f).padding(end = 24.dp), row = it) {
                        viewModel.clearTest()
                        onClick(Navigation.Detail, it)
                    }
                    Text(modifier = Modifier.weight(1f).padding(8.dp), text = testState)
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = { viewModel.onDestroy(); exitApplication() }) {
        App()
    }
}

enum class Navigation {
    Main,
    Detail,
}
