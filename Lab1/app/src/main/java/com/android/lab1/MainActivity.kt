package com.android.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.android.lab1.ui.theme.Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TrainSelectionScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TrainSelectionScreen(modifier: Modifier = Modifier) {
    var departure by remember { mutableStateOf("") }
    var arrival by remember { mutableStateOf("") }
    val timeOptions = listOf("Ранок (06:00–12:00)", "День (12:00–18:00)", "Вечір (18:00–00:00)", "Ніч (00:00–06:00)")
    var selectedTime by remember { mutableStateOf(timeOptions[0]) }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Вибір поїзда",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = departure,
            onValueChange = { departure = it },
            label = { Text("Пункт відправлення") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = arrival,
            onValueChange = { arrival = it },
            label = { Text("Пункт прибуття") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Час відправлення:",
            style = MaterialTheme.typography.titleMedium
        )

        Column(modifier = Modifier.selectableGroup()) {
            timeOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (option == selectedTime),
                            onClick = { selectedTime = option },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedTime),
                        onClick = null
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                result = "Маршрут: $departure - $arrival\nЧас відправлення: $selectedTime"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OK")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = result,
            onValueChange = {},
            label = { Text("Результат") },
            readOnly = true,
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
