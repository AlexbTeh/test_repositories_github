package com.done.repositories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.designsystem.theme.darkOutlinedColors
import com.done.domain.models.Scorecard

@Composable
fun ScoreboardRoute(
    roundId: String,
    onBack: () -> Unit,
    vm: ScoreboardViewModel = hiltViewModel()
) {
    val card: Scorecard? by vm.flow(roundId).collectAsStateWithLifecycle(initialValue = null)
    card?.let {
        ScoreboardScreen(
            card = it,
            onBack = onBack,
            onInput = { playerId, hole, strokes -> vm.set(roundId, playerId, hole, strokes) }
        )
    }
}

@Composable
private fun ScoreboardScreen(
    card: Scorecard,
    onBack: () -> Unit,
    onInput: (playerId: String, hole: Int, strokes: Int?) -> Unit
) {
    val scroll = rememberScrollState()
    var editing by remember { mutableStateOf<Triple<String, Int, Int?>?>(null) } // (playerId, hole, current)

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val nameColWidth: Dp = remember(card.players) {
        val maxNamePx = card.players.maxOfOrNull {
            textMeasurer.measure(AnnotatedString(it.name)).size.width
        } ?: 0
        with(density) {
            val w = maxNamePx.toDp() + 24.dp
            max(140.dp, w).coerceAtMost(320.dp)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Scorecard   Date: ${card.round.date}   Start: ${card.round.startTime.toLocalTime()}")
            TextButton(onClick = onBack) { Text("Back") }
        }
        Spacer(Modifier.height(8.dp))

        editing?.let { (pid, hole, current) ->
            var text by remember(current) { mutableStateOf(current?.toString() ?: "") }
            AlertDialog(
                onDismissRequest = { editing = null },
                title = { Text("Enter strokes (hole $hole)") },
                text = {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { txt -> text = txt.filter(Char::isDigit).take(2) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = darkOutlinedColors()
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onInput(pid, hole, text.toIntOrNull()); editing = null }) {
                        Text("Done")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onInput(pid, hole, null); editing = null }) {
                        Text("Clear")
                    }
                }
            )
        }

        Column(Modifier.horizontalScroll(scroll)) {
            Row {
                Cell("Hole", header = true, width = nameColWidth, ellipsize = false)
                (1..card.round.holes).forEach { Cell(it.toString(), header = true) }
            }
            Row {
                Cell("Par", header = true, width = nameColWidth, ellipsize = false)
                repeat(card.round.holes) { Cell(card.par.toString(), header = false) }
            }
            card.players.forEach { p ->
                Row {
                    Cell(p.name, header = true, width = nameColWidth, ellipsize = false)
                    (1..card.round.holes).forEach { h ->
                        val key = p.id to h
                        val v = card.scores[key]
                        Cell(
                            text = v?.toString() ?: "",
                            header = false,
                            editable = true
                        ) { editing = Triple(p.id, h, v) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Cell(
    text: String,
    header: Boolean,
    editable: Boolean = false,
    width: Dp = 56.dp,
    ellipsize: Boolean = true,
    onClick: () -> Unit = {}
) {
    Surface(
        tonalElevation = if (header) 3.dp else 0.dp,
        modifier = Modifier
            .width(width)
            .height(36.dp)
            .padding(1.dp)
            .then(if (editable) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (header) Color(0xFF2F53B5) else Color.White)
                .padding(8.dp)
        ) {
            Text(
                text = text,
                color = if (header) Color.White else Color.Black,
                maxLines = if (ellipsize) 1 else Int.MAX_VALUE,
                overflow = if (ellipsize) TextOverflow.Ellipsis else TextOverflow.Clip
            )
        }
    }
}


