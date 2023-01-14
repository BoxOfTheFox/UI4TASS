package ui.detail

import Navigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, onClick: (Navigation) -> Unit) {
    val profile by viewModel.selectedProfile.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val similarSofifaProfiles by viewModel.similarSofifaProfiles.collectAsState()
    val similarInstaProfiles by viewModel.similarInstaProfiles.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = profile?.Name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onDestroy(); onClick(Navigation.Main) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Return")
                    }
                }
            )
        },
        content = {
            Row(modifier = Modifier.padding(it).padding(16.dp).onClick { focusManager.clearFocus() }) {
                AtributesComponent(modifier = Modifier.weight(1f), profile)
                CommentsComponent(modifier = Modifier.weight(1f), comments)
                MostSimilarPlayersComponent(
                    modifier = Modifier.weight(1f),
                    similarSofifaProfiles,
                    similarInstaProfiles
                ) {
                    viewModel.selectSimilarSofifa(it)
                    viewModel.selectSimilarInsta(it)
                }
            }
        }
    )
}

