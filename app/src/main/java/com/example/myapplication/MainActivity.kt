package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleMemoryGame()
        }
    }
}

data class Card(val id: Int, val image: Int, var isFlipped: Boolean = false, var isMatched: Boolean = false)

@Composable
fun SimpleMemoryGame() {
    // Function to generate shuffled cards list
    fun generateCards(): List<Card> {
        return listOf(
            R.drawable.cat,
            R.drawable.dog,
            R.drawable.mouse,
            R.drawable.cat,
            R.drawable.dog,
            R.drawable.mouse
        ).shuffled().mapIndexed { index, image ->
            Card(id = index, image = image)
        }
    }

    var cards by remember { mutableStateOf(generateCards()) }
    var flipped by remember { mutableStateOf(listOf<Card>()) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cards grid
        for (row in 0 until 2) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val card = cards[index]

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                            .background(if (card.isFlipped || card.isMatched) Color.White else Color.Gray)
                            .clickable {
                                if (!card.isFlipped && flipped.size < 2) {
                                    val updated = cards.toMutableList()
                                    updated[index] = card.copy(isFlipped = true)
                                    cards = updated
                                    flipped = flipped + updated[index]
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (card.isFlipped || card.isMatched) {
                            Image(
                                painter = painterResource(id = card.image),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Shuffle button
        Button(onClick = {
            cards = generateCards()
            flipped = listOf()
        }) {
            Text("Shuffle")
        }

        // Check for matches and flip cards back if needed
        if (flipped.size == 2) {
            LaunchedEffect(flipped) {
                delay(1000)
                val match = flipped[0].image == flipped[1].image
                val updated = cards.toMutableList()
                flipped.forEach {
                    val idx = updated.indexOfFirst { c -> c.id == it.id }
                    updated[idx] = it.copy(
                        isFlipped = match,
                        isMatched = match
                    )
                }
                if (!match) {
                    updated.forEachIndexed { i, c ->
                        if (!c.isMatched) updated[i] = c.copy(isFlipped = false)
                    }
                }
                cards = updated
                flipped = listOf()
            }
        }
    }
}
