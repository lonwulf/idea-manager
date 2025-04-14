package com.lonwulf.ideamanager.taskmanager.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.lonwulf.ideamanager.taskmanager.R
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary

@Composable
fun ErrorAlertDialog(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, errMsg: String
) {
    AlertDialog(
        titleContentColor = Color.Red, textContentColor = Color.Black,
        containerColor = MaterialTheme.colorScheme.background,
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "",
                tint = Color.Red
            )
        },
        title = {
            Text(text = stringResource(R.string.error))
        },
        text = {
            Text(text = errMsg)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmation()
            }) {
                Text(stringResource(R.string.retry), color = BluePrimary)
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false),
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(stringResource(R.string.dismiss))
            }
        })
}
@Composable
fun SuccessAlertDialog(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, successMsg: String
) {
    AlertDialog(
        titleContentColor = BluePrimary,
        textContentColor = Color.Black,
        containerColor = MaterialTheme.colorScheme.background,
        icon = {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "",
                tint = BluePrimary
            )
        },
        title = {
            Text(text = stringResource(R.string.success))
        },
        text = {
            Text(text = successMsg)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmation()
            }) {
                Text(stringResource(R.string.dismiss), color = BluePrimary)
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    )
}