import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.random.Random

val path = System.getProperty("user.dir")
val target = "$path/src/jvmMain/resources/raw/sofifa_processed.csv"
val data = readCsv(target)

@Composable
@Preview
fun App() {
    var navigationTarget by rememberSaveable { mutableStateOf(Navigation.Main) }
    var chosenPlayer by rememberSaveable { mutableStateOf<Row?>(null) }

    MaterialTheme {
        when(navigationTarget){
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
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) {
            MainCard(modifier = Modifier.fillMaxWidth(), row=it) { onClick(Navigation.Detail, it) }
        }
    }
}

@Composable
fun DetailScreen(row: Row, onClick: (Navigation, Row?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { onClick(Navigation.Main, null) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, "Home")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            MainCard(modifier = Modifier.weight(3f), row = row) {}
            Spacer(modifier = Modifier.weight(2f))
        }

        LazyColumn(modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 32.dp)) {
            items(data.shuffled().take(Random.nextInt(7) + 3)) {
                Row {
                    MainCard(modifier = Modifier.weight(2f).padding(end = 24.dp), row = it) {
                        onClick(Navigation.Detail, it)
                    }
                    Text(modifier = Modifier.weight(1f).padding(8.dp), text = "No fajny był")
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

enum class Navigation {
    Main, Detail
}
