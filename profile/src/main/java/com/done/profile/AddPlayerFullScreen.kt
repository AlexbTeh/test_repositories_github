package com.done.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.done.designsystem.theme.Palette
import com.done.domain.models.Player
import java.util.UUID

@Composable
fun AddPlayerFullScreen(
    onClose: () -> Unit,
    onAdded: (Player) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Text("X",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .clickable { onClose() }
        )

        Column(
            Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 24.dp, end = 24.dp)
        ) {
            Text("Add New Player", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name Surname*") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAdded(Player(id = UUID.randomUUID().toString(), name = name.trim()))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.Green),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Add", color = Color.White) }
            }

            Spacer(Modifier.height(20.dp))

            // Member ID (optional, number only)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = memberId,
                    onValueChange = { memberId = it.filter(Char::isDigit) },
                    label = { Text("Member ID (optional, number only)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAdded(Player(id = UUID.randomUUID().toString(), name = name.trim()))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.Green),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Add", color = Color.White) }
            }
        }
    }
}
