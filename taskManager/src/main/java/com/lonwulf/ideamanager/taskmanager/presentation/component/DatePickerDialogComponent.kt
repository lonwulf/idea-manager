package com.lonwulf.ideamanager.taskmanager.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    showDatePicker: Boolean,
    onDismiss: () -> Unit,
    onError: (String?) -> Unit,
    datePickerState: DatePickerState
) {
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val selectedLocalDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    selectedLocalDate?.let {
                        if (it.isBefore(LocalDate.now())) {
                            onError("You cannot select a past date.")
                        } else {
                            onError(null)
                            onDismiss()
                        }
                    } ?: run {
                        onError("Please select a valid date.")
                    }
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}