package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    row: Map<String, String>,
    onClick: () -> Unit
) {
    Card(
        elevation = 1.dp,
        modifier = modifier
            .defaultMinSize(minHeight = 80.dp),
        onClick = { onClick() },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = row["Name"] ?: "",
                style = MaterialTheme.typography.h6,
                maxLines = 1
            )
            Text(
                text = row["Club"] ?: "",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.paddingFromBaseline(20.dp),
                maxLines = 1
            )
            Text(
                text = row["Country"] ?: "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.paddingFromBaseline(14.dp),
                maxLines = 1
            )
        }
    }
}