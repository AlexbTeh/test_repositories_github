package com.done.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.designsystem.theme.FilledBtn
import com.done.profile.components.IntDropdown
import com.done.profile.components.TeeDropdown
import com.done.profile.components.TypeDropdown
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoundScreen(
    onClose: () -> Unit,
    onStart: (String) -> Unit,
    vm: CreateRoundViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val canStart = ui.tee != null && ui.holes != null && ui.type != null && ui.players.isNotEmpty()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create New Round Report") },
                actions = {
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
            )
        }
    ) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("General Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Course: Cronulla Golf club")
                    Text("Date: ${LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yy"))}")

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TeeDropdown(
                            label = "Tees",
                            selected = ui.tee,
                            onSelect = vm::setTee
                        )
                        IntDropdown(
                            label = "Holes",
                            options = listOf(9, 18, 27, 36),
                            selected = ui.holes,
                            onSelect = vm::setHoles
                        )
                        TypeDropdown(
                            label = "Round Type",
                            selected = ui.type,
                            onSelect = vm::setType
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Players", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    PlayersRow(
                        players = ui.players,
                        onRemove = vm::removePlayer,
                        onAdd = vm::addPlayerDialog
                    )

                    Spacer(Modifier.height(16.dp))
                    FilledBtn("Start New Round", enabled = canStart) {
                        vm.start { roundId ->
                            // офлайн-создание → переход на скоркард
                            onStart(roundId)
                        }
                    }
                }
            }
        }
    }
}



