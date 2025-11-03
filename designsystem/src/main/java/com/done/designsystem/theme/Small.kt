package com.done.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilledBtn(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
        modifier = Modifier.height(48.dp)
    ) { Text(text) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledDropDown(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    colorSwatch: Color? = null
) {
    Column(Modifier.padding(top = 12.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                readOnly = true,
                value = selected ?: "",
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(160.dp)
                    .clickable { expanded = true },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                leadingIcon = {
                    if (colorSwatch != null) Box(
                        Modifier.size(16.dp).background(colorSwatch)
                    )
                }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = { onSelect(item); expanded = false }
                    )
                }
            }
        }
    }
}
