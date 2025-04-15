package com.lonwulf.ideamanager.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lonwulf.ideamanager.domain.model.FilterItem

@Composable
fun FilterRowComponent(
    modifier: Modifier = Modifier,
    rowItems: List<FilterItem>,
    onClick: (String) -> Unit
) {
    var filterItemState by remember { mutableStateOf<FilterItem?>(null) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = modifier.padding(start = 10.dp),
            text = "Sort:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyRow(
            modifier = modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 10.dp)
        ) {
            items(items = rowItems) { item ->
                val isClicked = item == filterItemState
                Box(
                    modifier = modifier
                        .wrapContentWidth()
                        .background(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondary
                        )
                        .clickable {
                            onClick(item.name)
                            filterItemState = if (isClicked) null else item
                        }) {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isClicked) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}