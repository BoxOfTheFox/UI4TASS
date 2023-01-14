package ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.CommentsResponse
import data.Profile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import ui.ScrollableLazyRow

@Composable
fun MostSimilarPlayersComponent(
    modifier: Modifier,
    similarSofifaProfiles: List<String>,
    similarInstaProfiles: List<String>,
    onClick: (Int) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text(text = "Najbardziej podobni piłkarze:")
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(bottom = 8.dp),
            value = input,
            onValueChange = { input = it.filter(Char::isDigit) },
            singleLine = true,
            label = { Text(text = "Podaj ilość") },
            trailingIcon = {
                IconButton(onClick = { onClick(input.toInt()) }) {
                    Icon(imageVector = Icons.Default.Search, "Search")
                }
            }
        )
        Text(modifier = Modifier.padding(4.dp), text = "W Sofifa:")
        Surface(
            modifier = Modifier.weight(1f).padding(bottom = 4.dp).fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(4.dp)) {
                items(similarSofifaProfiles) {
                    Text(it)
                }
            }
        }
        Text(modifier = Modifier.padding(4.dp), text = "Na Instagramie:")
        Surface(
            modifier = Modifier.weight(1f).padding(bottom = 4.dp).fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(4.dp)) {
                items(similarInstaProfiles) {
                    Text(it)
                }
            }
        }
        Text(modifier = Modifier.padding(4.dp), text = "W obu:")
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(4.dp)) {
                items(similarSofifaProfiles.intersect(similarInstaProfiles.toSet()).toList()) {
                    Text(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentsComponent(modifier: Modifier, commentsResponse: CommentsResponse?) {
    Column(modifier = modifier.padding(end = 16.dp)) {
        Text(text = "Komentarze:")
        if (commentsResponse == null)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        else
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(commentsResponse.comments.filter(String::isNotEmpty)) { comment ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        elevation = 4.dp,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(modifier = Modifier.padding(bottom = 4.dp), text = comment.trim())
                            ScrollableLazyRow(commentsResponse.keywords.filter(comment::contains)) {
                                Chip(onClick = {}) { Text(it) }
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun AtributesComponent(modifier: Modifier, profile: Profile?) {
    val JSON = Json { encodeDefaults = true }

    Column(
        modifier = modifier.padding(end = 16.dp).fillMaxHeight()
    ) {
        Text(modifier = Modifier.padding(bottom = 4.dp), text = "Atrybuty:")

        if (profile == null)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        else
            Surface(
                elevation = 4.dp,
                shape = RoundedCornerShape(4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(JSON.encodeToJsonElement(profile).jsonObject.toMap().toList()) {
                        Row {
                            Text(
                                modifier = Modifier.weight(3f).padding(end = 4.dp),
                                text = "${it.first.replace('_', ' ')}:"
                            )
                            Text(modifier = Modifier.weight(2f), text = "${it.second}")
                        }
                    }
                }
            }
    }
}