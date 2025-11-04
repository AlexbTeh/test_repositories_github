package com.done.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.designsystem.theme.Palette
import com.done.domain.models.Player
import com.done.domain.models.TeeColour

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeeDropdownBox(
    label: String,
    value: TeeColour?,
    onSelect: (TeeColour) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, color = Palette.White)
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value?.displayName() ?: "",
                onValueChange = {},
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Palette.Red,
                    unfocusedContainerColor = Palette.Red,
                    focusedTextColor = Palette.White,
                    unfocusedTextColor = Palette.White,
                    focusedBorderColor = Palette.Red,
                    unfocusedBorderColor = Palette.Red,
                    cursorColor = Palette.White
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                TeeColour.entries.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.displayName()) },
                        onClick = { onSelect(c); expanded = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleDropdownBox(
    label: String,
    items: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, color = Palette.White)
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Palette.White,
                    unfocusedContainerColor = Palette.White,
                    focusedTextColor = Palette.Black,
                    unfocusedTextColor = Palette.Black
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.toString()) },
                        onClick = { onSelect(item); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun AddPlayerButton(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Box(
            Modifier.size(18.dp).clip(CircleShape).background(Palette.Green),
            contentAlignment = Alignment.Center
        ) { Text("+", color = Palette.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(8.dp))
        Text("Add player", color = Palette.Green, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PlayersGrid(
    items: List<Player>,
    onRemove: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 200.dp)
    ) {
        items(items.size) { index ->
            val player = items[index]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Palette.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(player.name, color = Color.Black)
                Spacer(Modifier.width(10.dp))
                Text(
                    "X",
                    color = Palette.GreenX,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable { onRemove(player.id) }
                )
            }
        }
    }
}


@Composable
private fun StartButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Palette.Green,
            contentColor = Palette.White,
            disabledContainerColor = Palette.Green.copy(alpha = 0.4f)
        ),
        modifier = Modifier.height(56.dp)
    ) {
        Text("Start New Round")
    }
}
private fun TeeColour.displayName(): String = name.lowercase().replace('_',' ').replaceFirstChar { it.uppercase() }
