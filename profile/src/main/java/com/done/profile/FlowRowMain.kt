package com.done.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.done.domain.models.Player

@Composable
fun PlayersRow(
    players: List<Player>,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(players, key = { it.id }) { p ->
            Row(
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(p.name, modifier = Modifier.padding(horizontal = 4.dp))
                Spacer(Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 24.dp)
                        .background(Color(0x142AA845), RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button) { onRemove(p.id) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("✕", color = Color(0xFF2AA845))
                }
            }
        }
        item {
            TextButton(onClick = onAdd) {
                Text("+ Add player", color = Color(0xFF2AA845))
            }
        }
    }
}

