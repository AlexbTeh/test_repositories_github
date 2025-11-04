package com.done.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.domain.models.RoundType
import com.done.domain.models.TeeColour

@Composable
fun CreateRoundScreen(
    onAddPlayer: () -> Unit,
    onStartRound: (String) -> Unit,
    vm: CreateRoundViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (ui.tees == null) vm.setTees(TeeColour.RED)
        if (ui.holes == null) vm.setHoles(18)
        if (ui.roundType == null) vm.setType(RoundType.entries.first())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF4A4A4A))
                .padding(24.dp)
        ) {
            Text(
                text = "Create New Round Report",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            Text("Course: ${ui.course}", color = Color.White)
            Text("Date: ${ui.date}", color = Color.White)
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SimpleDropdownBox(
                    label = "Tees",
                    items = listOf("Red", "Yellow", "Blue", "Green"),
                    selected = ui.tees?.name?.lowercase()
                        ?.replaceFirstChar { it.uppercase() } ?: "Red",
                    onSelect = { selected ->
                        TeeColour.entries
                            .firstOrNull { it.name.equals(selected, ignoreCase = true) }
                            ?.let(vm::setTees)
                    },
                    modifier = Modifier.weight(1f)
                )

                SimpleDropdownBox(
                    label = "Holes",
                    items = listOf("9", "18", "27", "36"),
                    selected = (ui.holes ?: 18).toString(),
                    onSelect = { value -> value.toIntOrNull()?.let(vm::setHoles) },
                    modifier = Modifier.weight(1f)
                )


                SimpleDropdownBox(
                    label = "Round Type",
                    items = RoundType.entries.map { it.name },
                    selected = ui.roundType?.name ?: RoundType.entries.first().name, // ✅ дефолт
                    onSelect = { value ->
                        RoundType.entries
                            .firstOrNull { it.name.equals(value, ignoreCase = true) }
                            ?.let(vm::setType)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))
            Text("Players", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            if (ui.players.isEmpty()) {
                Text("No players added yet", color = Color.LightGray, fontSize = 14.sp)
            } else {
                PlayersGrid(items = ui.players, onRemove = { vm.removePlayer(it) })
            }

            Spacer(Modifier.height(16.dp))
            AddPlayerButton(onClick = onAddPlayer)
            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { vm.createRound(onStartRound) },
                    enabled = ui.isValid,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF48B000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF48B000).copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Start New Round", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}







