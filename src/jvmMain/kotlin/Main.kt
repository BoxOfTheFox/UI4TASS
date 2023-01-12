import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.GraphRequest
import kotlinx.coroutines.MainScope
import ui.Graph
import ui.MainCard
import ui.SearchComponent
import kotlin.random.Random

private val scope = MainScope()
private val viewModel = MainViewModel()

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    var navigationTarget by rememberSaveable { mutableStateOf(Navigation.Main) }
    var chosenPlayer by rememberSaveable { mutableStateOf<Map<String, String>?>(null) }

    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    MaterialTheme {
        Box(modifier = Modifier.onClick {
            expanded = false
            focusManager.clearFocus()
        }) {
            SearchComponent(
                expanded,
                { expanded = it },
                viewModel.profiles,
                viewModel.graphState.collectAsState().value == null
            ) {
                viewModel.buildGraph(GraphRequest(it))
            }
            Graph(viewModel)
        }
//        when(navigationTarget){
//            Navigation.Main -> MainScreen { navigation, row ->
//                navigationTarget = navigation
//                chosenPlayer = row
//            }
//            Navigation.Detail -> DetailScreen(chosenPlayer!!) { navigation, row ->
//                navigationTarget = navigation
//                chosenPlayer = row
//            }
//        }
    }
}

@Composable
fun MainScreen(onClick: (Navigation, Map<String, String>) -> Unit) {
    val sofifaState by viewModel.sofifaState.collectAsState()
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sofifaState) {
            MainCard(modifier = Modifier.fillMaxWidth(), row=it) { onClick(Navigation.Detail, it) }
        }
    }
}

@Composable
fun DetailScreen(row: Map<String, String>, onClick: (Navigation, Map<String, String>?) -> Unit) {
    val testState by viewModel.testState.collectAsState()
    val randomSofifaRows = viewModel.sofifaState.value.shuffled().take(Random.nextInt(7) + 3)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
    Main, Detail
}
