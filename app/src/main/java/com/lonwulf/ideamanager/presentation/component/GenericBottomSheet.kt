package com.lonwulf.ideamanager.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericBottomSheet(
    sourceStrings: List<String>,
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onclick: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var clickState by remember { mutableStateOf(false) }
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = { scope.launch { sheetState.hide() } },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier =
                modifier
                    .padding(bottom = 30.dp),
        ) {
            var count = 1
            LazyColumn(
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items = sourceStrings) { name ->
                    ConstraintLayout(
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(10.dp)
                                .clickable {
                                    clickState = true
                                    scope.launch { sheetState.hide() }
                                    onclick(name)
                                },
                    ) {
                        val (txt, icon) = createRefs()
                        Text(
                            text = count.toString().plus(". ").plus(name.uppercase()),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleMedium,
                            modifier =
                                modifier
                                    .constrainAs(txt) {
                                        top.linkTo(parent.top, margin = 20.dp)
                                        start.linkTo(parent.start, margin = 20.dp)
                                    },
                        )
                    }
                    count++
                }
            }
        }
    }
}