package com.done.repositories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.domain.models.Scorecard
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* -------------------- Public entry -------------------- */

@Composable
fun ScoreboardRoute(
    roundId: String,
    onBack: () -> Unit,
    vm: ScoreboardViewModel = hiltViewModel()
) {
    val card: Scorecard? by vm.flow(roundId).collectAsStateWithLifecycle(initialValue = null)

    // Ничего не ломаем: показываем экран только когда есть данные
    card?.let {
        ScoreboardScreen(
            card = it,
            onBack = onBack,
            onInput = { playerId, hole, strokes -> vm.set(roundId, playerId, hole, strokes) }
        )
    }
}

/* -------------------- Colors & metrics -------------------- */

private val ClHeaderBlue = Color(0xFF2338B8)  // синий хедер
private val ClParGray    = Color(0xFFF1F2F6)  // серая строка Par
private val ClGrid       = Color(0xFFE5E7EB)  // тонкая сетка
private val ClCellText   = Color(0xFF111827)  // тёмный текст
private val ClHeaderText = Color.White
private val ClFocusBlue  = Color(0xFF2F53B5)  // рамка выбранной клетки

private const val HEADER_H = 48
private const val ROW_H    = 44
private const val CELL_W   = 64

/* -------------------- Screen -------------------- */

@Composable
private fun ScoreboardScreen(
    card: Scorecard,
    onBack: () -> Unit,
    onInput: (playerId: String, hole: Int, strokes: Int?) -> Unit
) {
    val scroll = rememberScrollState()
    var editing by remember { mutableStateOf<Triple<String, Int, Int?>?>(null) } // (playerId, hole, current)

    // Авто-ширина для колонки имени
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val nameColWidth: Dp = remember(card.players) {
        val maxNamePx = card.players.maxOfOrNull {
            textMeasurer.measure(AnnotatedString(it.name)).size.width
        } ?: 0
        with(density) {
            val w = maxNamePx.toDp() + 48.dp
            max(160.dp, w).coerceAtMost(360.dp)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0B))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Scorecard   Date: ${card.round.date}   Start: ${card.round.startTime.toLocalTime()}",
                color = Color.White
            )
            TextButton(onClick = onBack) { Text("Back") }
        }
        Spacer(Modifier.height(8.dp))

        // Таблица
        Column(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(0.02f))
                .horizontalScroll(scroll)
        ) {
            // Header (синий)
            Row {
                HeaderCell("Hole", width = nameColWidth)
                (1..card.round.holes).forEach { h ->
                    HeaderCell(h.toString())
                }
            }
            // Строка Par (серая)
            Row {
                ParHeaderCell("Par", width = nameColWidth)
                repeat(card.round.holes) {
                    ParCell(card.par.toString())
                }
            }
            // Игроки
            card.players.forEach { p ->
                Row {
                    NameCell(p.name, width = nameColWidth)
                    (1..card.round.holes).forEach { h ->
                        val key = p.id to h
                        val v = card.scores[key]
                        val isSelected = editing?.let { it.first == p.id && it.second == h } == true
                        BodyCell(
                            text = v?.toString() ?: "",
                            selected = isSelected,
                            onClick = { editing = Triple(p.id, h, v) }
                        )
                    }
                }
                HorizontalDivider(color = ClGrid)
            }
        }
    }

    // Диалог ввода удара
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
}

/* -------------------- Cells -------------------- */

@Composable
private fun HeaderCell(text: String, width: Dp = CELL_W.dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(HEADER_H.dp)
            .drawGridBorder()
            .background(ClHeaderBlue)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = ClHeaderText, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ParHeaderCell(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(ROW_H.dp)
            .drawGridBorder()
            .background(ClParGray)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text, color = ClCellText, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ParCell(text: String) {
    Box(
        modifier = Modifier
            .width(CELL_W.dp)
            .height(ROW_H.dp)
            .drawGridBorder()
            .background(ClParGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = ClCellText)
    }
}

@Composable
private fun NameCell(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(ROW_H.dp)
            .drawGridBorder()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text, color = ClCellText, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun BodyCell(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(CELL_W.dp)
            .height(ROW_H.dp)
            .drawGridBorder()
            .background(Color.White)
            .clickable { onClick() }
            .then(if (selected) Modifier.border(2.dp, ClFocusBlue, RoundedCornerShape(2.dp)) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = ClCellText)
    }
}

/* ---- helper: тонкая сетка справа и снизу для каждой ячейки ---- */
/* ---- helper: сетка как в тетрадке ---- */
private fun Modifier.drawGridBorder(): Modifier =
    this.drawBehind {
        val stroke = 1f
        val c = Color(0xFF9CA3AF) // насыщенный серо-графитовый цвет линий (как на дизайне)

        // левая граница
        drawLine(
            c,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = stroke
        )
        // правая граница
        drawLine(
            c,
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = stroke
        )
        // верхняя граница
        drawLine(
            c,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = stroke
        )
        // нижняя граница
        drawLine(
            c,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = stroke
        )
    }



