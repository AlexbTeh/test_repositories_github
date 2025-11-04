package com.done.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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

    val canStart = ui.players.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(12.dp)
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
                fontSize = 28.sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF2E2E2E))
            Spacer(Modifier.height(16.dp))

            Text(
                "General Info",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(
                    buildAnnotatedString {
                        append("Course: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(ui.course) }
                    },
                    color = Color.White
                )
                Text(
                    buildAnnotatedString {
                        append("Date: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${ui.date}") }
                    },
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TeesDropdown(
                    label = "Tees",
                    selected = ui.tees ?: TeeColour.RED,
                    onSelect = vm::setTees,
                    modifier = Modifier.weight(1f)
                )

                SimpleDropdown(
                    label = "Holes",
                    items = listOf("9", "18", "27", "36"),
                    selected = (ui.holes ?: 18).toString(),
                    onSelect = { it.toIntOrNull()?.let(vm::setHoles) },
                    modifier = Modifier.weight(1f)
                )

                val types = RoundType.entries
                SimpleDropdown(
                    label = "Round Type",
                    items = types.map { it.name.uppercase() },
                    selected = (ui.roundType ?: types.first()).name.uppercase(),
                    onSelect = { v ->
                        types.firstOrNull { it.name.equals(v, true) }?.let(vm::setType)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text("Players", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(12.dp))

            if (ui.players.isEmpty()) {
                Text("No players added yet", color = Color(0xFFBDBDBD))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ui.players.forEach { p ->
                        PlayerChip(
                            name = p.name,
                            onRemove = { vm.removePlayer(p.id) } // если метод принимает Player — поменяй на vm.removePlayer(p)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAddPlayer() }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add player",
                    tint = Color(0xFF48B000)
                )
                Spacer(Modifier.width(8.dp))
                Text("Add player", color = Color(0xFF48B000), fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(28.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { vm.createRound(onStartRound) },
                    enabled = canStart,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) { Text("Start New Round", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            }
        }
    }
}

/* ======================== COMPONENTS ======================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeesDropdown(
    label: String,
    selected: TeeColour,
    onSelect: (TeeColour) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(label, color = Color.White)
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(teeColourToColor(selected))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val darkText = selected in setOf(TeeColour.WHITE, TeeColour.SILVER, TeeColour.GOLD)
                Text(
                    text = teeLabel(selected),
                    color = if (darkText) Color.Black else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand tees",
                    tint = if (darkText) Color.Black else Color.White
                )
            }

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                TeeColour.entries.forEach { t ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(teeColourToColor(t))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(teeLabel(t))
                            }
                        },
                        onClick = { expanded = false; onSelect(t) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(label, color = Color.White)
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selected, color = Color.Black, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand $label",
                    tint = Color.Black
                )
            }

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = { expanded = false; onSelect(value) })
                }
            }
        }
    }
}

@Composable
private fun PlayerChip(
    name: String,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(name, color = Color.Black)
        Spacer(Modifier.width(10.dp))
        Text(
            "X",
            color = Color(0xFF14B800),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRemove() }
        )
    }
}

/* ======================== HELPERS ======================== */

private fun teeLabel(c: TeeColour): String =
    c.name.lowercase().replace('_', ' ').split(' ')
        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

@Composable
private fun teeColourToColor(c: TeeColour): Color = when (c) {
    TeeColour.WHITE -> Color(0xFFFFFFFF)
    TeeColour.BLACK -> Color(0xFF111111)
    TeeColour.SILVER -> Color(0xFFC0C0C0)
    TeeColour.RED -> Color(0xFFE53935)
    TeeColour.BURGUNDY -> Color(0xFF800020)
    TeeColour.YELLOW -> Color(0xFFFDD835)
    TeeColour.ORANGE -> Color(0xFFFB8C00)
    TeeColour.LIGHT_GREEN -> Color(0xFF8BC34A)
    TeeColour.DARK_GREEN -> Color(0xFF2E7D32)
    TeeColour.PURPLE -> Color(0xFF6A1B9A)
    TeeColour.COPPER -> Color(0xFFB87333)
    TeeColour.GOLD -> Color(0xFFFFD700)
    TeeColour.REFLEX_BLUE -> Color(0xFF001489)
    TeeColour.DARK_BLUE -> Color(0xFF0D47A1)
}
