package com.done.profile.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.done.domain.models.RoundType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeDropdown(
    label: String,
    selected: RoundType?,
    onSelect: (RoundType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected?.name?.lowercase()?.replaceFirstChar(Char::uppercase) ?: ""
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.menuAnchor().width(200.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RoundType.entries.forEach { t ->
                val text = t.name.lowercase().replaceFirstChar(Char::uppercase)
                DropdownMenuItem(text = { Text(text) }, onClick = { onSelect(t); expanded = false })
            }
        }
    }
}

