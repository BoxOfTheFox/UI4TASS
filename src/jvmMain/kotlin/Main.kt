import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.*
import ui.detail.DetailScreen
import ui.detail.DetailViewModel
import ui.main.MainScreen
import ui.main.MainViewModel

@Composable
@Preview
fun App() {
    var navigationTarget by rememberSaveable { mutableStateOf(Navigation.Main) }
    var selectedName by remember { mutableStateOf("") }
    val api = ApiService.create()

    MaterialTheme {
        when(navigationTarget){
            Navigation.Main -> MainScreen(MainViewModel(api)) { navigation, name ->
                selectedName = name
                navigationTarget = navigation
            }
            Navigation.Detail -> DetailScreen(DetailViewModel(selectedName, api)) { navigation ->
                navigationTarget = navigation
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = { exitApplication() }) {
        App()
    }
}

enum class Navigation {
    Main, Detail
}
