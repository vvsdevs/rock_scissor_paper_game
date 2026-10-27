package com.game.rockscissors

import org.junit.Assert.assertEquals
import org.junit.Test

class GameRulesTest {
    @Test
    fun allNineChoiceCombinationsFollowTheRules() {
        val expected = listOf(
            Triple(Choice.ROCK, Choice.ROCK, 0),
            Triple(Choice.ROCK, Choice.PAPER, -1),
            Triple(Choice.ROCK, Choice.SCISSORS, 1),
            Triple(Choice.PAPER, Choice.ROCK, 1),
            Triple(Choice.PAPER, Choice.PAPER, 0),
            Triple(Choice.PAPER, Choice.SCISSORS, -1),
            Triple(Choice.SCISSORS, Choice.ROCK, -1),
            Triple(Choice.SCISSORS, Choice.PAPER, 1),
            Triple(Choice.SCISSORS, Choice.SCISSORS, 0)
        )
        expected.forEach { (player, computer, winner) ->
            assertEquals("$player vs $computer", winner, roundWinner(player, computer))
        }
    }
}
