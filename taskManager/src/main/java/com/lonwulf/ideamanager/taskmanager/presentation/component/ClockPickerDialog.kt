package com.lonwulf.ideamanager.taskmanager.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lonwulf.ideamanager.taskmanager.util.dpToPx
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockPickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }
    var isHourSelection by remember { mutableStateOf(true) }
    var is24HourFormat by remember { mutableStateOf(false) }
    var isAM by remember { mutableStateOf(initialTime.hour < 12) }
    val ctx = LocalContext.current

    val displayHour = remember(selectedHour, is24HourFormat) {
        when {
            is24HourFormat -> selectedHour
            selectedHour == 0 -> 12
            selectedHour > 12 -> selectedHour - 12
            else -> selectedHour
        }
    }

    val hourString = remember(displayHour) {
        displayHour.toString().padStart(2, '0')
    }

    val minuteString = remember(selectedMinute) {
        selectedMinute.toString().padStart(2, '0')
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Select Time",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // Digital time display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = hourString,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isHourSelection) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.clickable { isHourSelection = true }
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Text(
                        text = minuteString,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (!isHourSelection) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.clickable { isHourSelection = false }
                    )

                    if (!is24HourFormat) {
                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isAM) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier
                                    .clickable {
                                        isAM = true
                                        if (!isAM && selectedHour >= 12) {
                                            selectedHour -= 12
                                        } else if (isAM && selectedHour >= 12) {
                                            selectedHour -= 12
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            Text(
                                text = "PM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (!isAM) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier
                                    .clickable {
                                        isAM = false
                                        if (isAM && selectedHour < 12) {
                                            selectedHour += 12
                                        } else if (!isAM && selectedHour < 12) {
                                            selectedHour += 12
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Analog clock face
                Box(
                    modifier = Modifier
                        .size(256.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Clock face
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ) {}

                    Surface(
                        modifier = Modifier
                            .size(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {}

                    // Hour numbers
                    if (isHourSelection) {
                        for (hour in 1..12) {
                            val angleRad = (hour * (2 * PI / 12) - PI / 2).toFloat()
                            val radius = 108.dp
                            val x =
                                cos(angleRad) * dpToPx(radius) / ctx.resources.displayMetrics.density
                            val y =
                                sin(angleRad) * dpToPx(radius) / ctx.resources.displayMetrics.density

                            val isSelected = when {
                                is24HourFormat -> hour == selectedHour || (hour + 12) == selectedHour
                                selectedHour == 0 -> hour == 12
                                selectedHour > 12 -> hour == selectedHour - 12
                                else -> hour == selectedHour
                            }

                            Box(
                                modifier = Modifier
                                    .offset(x = x.dp, y = y.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        selectedHour = when {
                                            is24HourFormat -> hour
                                            !isAM -> if (hour == 12) 12 else hour + 12
                                            else -> if (hour == 12) 0 else hour
                                        }
                                        isHourSelection = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hour.toString(),
                                    fontSize = 16.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        // Clock hand
                        val handAngleRad = when {
                            selectedHour == 0 -> (12 * (2 * PI / 12) - PI / 2).toFloat()
                            selectedHour > 12 -> ((selectedHour - 12) * (2 * PI / 12) - PI / 2).toFloat()
                            else -> (selectedHour * (2 * PI / 12) - PI / 2).toFloat()
                        }

                        val handLength = dpToPx(90.dp)
                        val endX = cos(handAngleRad) * handLength
                        val endY = sin(handAngleRad) * handLength

                        // Draw clock hand
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(2.dp)
                                .rotate(handAngleRad * (180 / PI.toFloat()) + 90)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        // Minute markers
                        for (minute in 0..55 step 5) {
                            val angleRad = (minute * (2 * PI / 60) - PI / 2).toFloat()
                            val radius = dpToPx(108.dp)
                            val x = cos(angleRad) * radius / ctx.resources.displayMetrics.density
                            val y = sin(angleRad) * radius / ctx.resources.displayMetrics.density

//                            val displayMinute = minute
                            val isSelected = minute == selectedMinute

                            Box(
                                modifier = Modifier
                                    .offset(x = x.dp, y = y.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        selectedMinute = minute
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = minute.toString().padStart(2, '0'),
                                    fontSize = 16.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        // Minute hand
                        val handAngleRad = (selectedMinute * (2 * PI / 60) - PI / 2).toFloat()
                        val handLength = 90.dp

                        // Draw minute hand
                        Box(
                            modifier = Modifier
                                .width(handLength)
                                .height(2.dp)
                                .rotate(handAngleRad * (180 / PI.toFloat()) + 90)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val finalHour = if (!is24HourFormat) {
                                when {
                                    !isAM && selectedHour < 12 -> selectedHour + 12
                                    isAM && selectedHour == 12 -> 0
                                    else -> selectedHour
                                }
                            } else {
                                selectedHour
                            }

                            onTimeSelected(LocalTime.of(finalHour, selectedMinute))
                            onDismiss()
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}