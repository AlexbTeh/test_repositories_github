package com.done.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.done.designsystem.theme.darkOutlinedColors
import com.done.domain.models.TeeColour

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeeDropdown(
    label: String,
    selected: TeeColour?,
    onSelect: (TeeColour) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected?.name?.replace('_', ' ') ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.Black) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().width(180.dp),
            colors = darkOutlinedColors(),
            leadingIcon = {
                val color = when (selected) {
                    TeeColour.RED          -> Color(0xFFEA1F3D)
                    TeeColour.YELLOW       -> Color(0xFFFFD300)
                    TeeColour.REFLEX_BLUE  -> Color(0xFF1F57A4)
                    TeeColour.DARK_GREEN   -> Color(0xFF007A3D)
                    else                   -> Color(0xFFCCCCCC)
                }
                Box(
                    Modifier
                        .size(12.dp)
                        .background(color, shape = MaterialTheme.shapes.extraSmall)
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) {
            TeeColour.entries.forEach { c ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Text(c.name.replace('_', ' '), color = Color.Black)
                        }
                    },
                    onClick = {
                        onSelect(c)
                        expanded = false
                    }
                )
            }
        }
    }
}

