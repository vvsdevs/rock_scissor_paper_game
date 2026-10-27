package com.game.rockscissors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.rockscissors.ui.theme.RockScissorsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            RockScissorsTheme(darkTheme = true, dynamicColor = false) {
                RockPaperScissorsGame()
            }
        }
    }
}

enum class Choice(val emoji: String, val title: String, private val c1: Long, private val c2: Long) {
    ROCK("🪨", "Rock", 0xFFFF9A62, 0xFFFF4E7A),
    PAPER("📄", "Paper", 0xFF4FD1FF, 0xFF5B6CFF),
    SCISSORS("✂️", "Scissors", 0xFFE36BFF, 0xFF8A4DFF);

    val colors: List<Color> get() = listOf(Color(c1), Color(c2))
}

// 1 = player wins, -1 = computer wins, 0 = draw.
internal fun roundWinner(player: Choice, computer: Choice): Int = when {
    player == computer -> 0
    player == Choice.ROCK && computer == Choice.SCISSORS -> 1
    player == Choice.SCISSORS && computer == Choice.PAPER -> 1
    player == Choice.PAPER && computer == Choice.ROCK -> 1
    else -> -1
}

private const val WIN_SCORE = 5
private val Ink = Color(0xFF14102A)
private val Glass = Color(0x1FFFFFFF)
private val GlassBorder = Color(0x33FFFFFF)
private val SoftText = Color(0xFFC9C3F0)
private val WinColors = listOf(Color(0xFF3CF0A0), Color(0xFF1FD1E0))
private val LoseColors = listOf(Color(0xFFFF5E7E), Color(0xFFFF7A45))
private val DrawColors = listOf(Color(0xFFFFE066), Color(0xFFFFA94D))

private enum class Phase { IDLE, SHAKING, REVEAL }

@Composable
fun RockPaperScissorsGame() {
    var playerScore by rememberSaveable { mutableIntStateOf(0) }
    var computerScore by rememberSaveable { mutableIntStateOf(0) }
    var playerChoice by rememberSaveable { mutableStateOf<Choice?>(null) }
    var computerChoice by rememberSaveable { mutableStateOf<Choice?>(null) }
    var outcome by rememberSaveable { mutableStateOf<Int?>(null) }
    var rounds by rememberSaveable { mutableIntStateOf(0) }
    var streak by rememberSaveable { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf(Phase.IDLE) }
    val matchOver = playerScore >= WIN_SCORE || computerScore >= WIN_SCORE
    val scope = rememberCoroutineScope()

    fun resetGame() {
        playerScore = 0; computerScore = 0
        playerChoice = null; computerChoice = null
        outcome = null; rounds = 0; streak = 0
        phase = Phase.IDLE
    }

    fun play(choice: Choice) {
        if (matchOver || phase == Phase.SHAKING) return
        scope.launch {
            phase = Phase.SHAKING
            delay(950)
            val computer = Choice.entries.random()
            val winner = roundWinner(choice, computer)
            playerChoice = choice
            computerChoice = computer
            outcome = winner
            when (winner) {
                1 -> { playerScore++; streak++ }
                -1 -> { computerScore++; streak = 0 }
            }
            rounds++
            phase = Phase.REVEAL
        }
    }

    Box(Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Title()
            ScoreBoard(playerScore, computerScore, streak)
            Arena(
                phase = phase,
                playerChoice = playerChoice,
                computerChoice = computerChoice,
                outcome = outcome,
                rounds = rounds
            )
            ResultBanner(phase, outcome, rounds)

            Text(
                if (phase == Phase.SHAKING) "Get ready…" else "Pick your move",
                color = SoftText, fontSize = 15.sp, fontWeight = FontWeight.Medium
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Choice.entries.forEachIndexed { index, choice ->
                    ChoiceButton(
                        choice = choice,
                        index = index,
                        enabled = !matchOver && phase != Phase.SHAKING,
                        selected = phase == Phase.REVEAL && playerChoice == choice,
                        onClick = { play(choice) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            PillButton(
                text = "↺  Reset",
                colors = listOf(Glass, Glass),
                textColor = Color.White,
                onClick = { resetGame() },
                modifier = Modifier.border(1.dp, GlassBorder, CircleShape)
            )
        }

        MatchOverOverlay(
            visible = matchOver && phase != Phase.SHAKING,
            playerWon = playerScore >= WIN_SCORE,
            playerScore = playerScore,
            computerScore = computerScore,
            onPlayAgain = { resetGame() }
        )
    }
}

/* ---------- Background ---------- */

@Composable
private fun AnimatedBackground() {
    val t = rememberInfiniteTransition(label = "bg")
    val angle by t.animateFloat(
        0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(14000, easing = LinearEasing)), label = "angle"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            Brush.verticalGradient(
                listOf(Color(0xFF1B1140), Color(0xFF2A1060), Color(0xFF0E2A55))
            )
        )
        val blobs = listOf(
            Triple(Color(0xFFFF4E9B), 0.0f, 0.55f),
            Triple(Color(0xFF4FD1FF), 2.1f, 0.6f),
            Triple(Color(0xFF9B5BFF), 4.2f, 0.7f),
            Triple(Color(0xFFFFB347), 1.0f, 0.4f)
        )
        blobs.forEachIndexed { i, (color, offset, radiusFactor) ->
            val a = angle * (if (i % 2 == 0) 1f else -1f) + offset
            val center = Offset(
                size.width * (0.5f + 0.35f * cos(a)),
                size.height * (0.45f + 0.3f * sin(a * 1.3f))
            )
            val radius = size.minDimension * radiusFactor
            drawCircle(
                Brush.radialGradient(
                    listOf(color.copy(alpha = 0.35f), Color.Transparent),
                    center = center, radius = radius
                ),
                radius = radius, center = center
            )
        }
    }
}

/* ---------- Header & scores ---------- */

@Composable
private fun Title() {
    val t = rememberInfiniteTransition(label = "title")
    val shift by t.animateFloat(
        0f, 1000f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "shift"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Rock · Paper · Scissors",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFF9A62), Color(0xFFFF4E9B), Color(0xFF8A4DFF), Color(0xFF4FD1FF)),
                    start = Offset(shift - 500f, 0f),
                    end = Offset(shift + 500f, 0f)
                )
            )
        )
        Text("First to $WIN_SCORE wins the match", color = SoftText, fontSize = 15.sp)
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Glass)
            .border(1.dp, GlassBorder, RoundedCornerShape(26.dp))
            .padding(18.dp),
        content = content
    )
}

@Composable
private fun ScoreBoard(player: Int, computer: Int, streak: Int) {
    GlassCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ScoreSide("😎", "You", player, WinColors, Modifier.weight(1f))
                Text("VS", color = Color.White.copy(alpha = 0.5f), fontSize = 18.sp, fontWeight = FontWeight.Black)
                ScoreSide("🤖", "CPU", computer, LoseColors, Modifier.weight(1f))
            }
            AnimatedVisibility(
                visible = streak >= 2,
                enter = fadeIn() + expandVertically() + scaleIn(spring(0.4f)),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    "🔥 $streak win streak!",
                    color = Color(0xFFFFC56B), fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ScoreSide(avatar: String, label: String, score: Int, colors: List<Color>, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$avatar  $label", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        AnimatedContent(
            targetState = score,
            transitionSpec = {
                (slideInVertically { it } + fadeIn() + scaleIn(initialScale = 1.6f))
                    .togetherWith(slideOutVertically { -it } + fadeOut())
            },
            label = "score"
        ) { value ->
            Text(
                "$value",
                style = TextStyle(
                    fontSize = 40.sp, fontWeight = FontWeight.Black,
                    brush = Brush.verticalGradient(colors)
                )
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(WIN_SCORE) { i ->
                val filled = i < score
                val pipScale by animateFloatAsState(
                    if (filled) 1f else 0.7f, spring(dampingRatio = 0.35f, stiffness = 400f), label = "pip"
                )
                Box(
                    Modifier
                        .size(12.dp)
                        .scale(pipScale)
                        .clip(CircleShape)
                        .background(
                            if (filled) Brush.linearGradient(colors)
                            else Brush.linearGradient(listOf(GlassBorder, GlassBorder))
                        )
                )
            }
        }
    }
}

/* ---------- Arena ---------- */

@Composable
private fun Arena(
    phase: Phase,
    playerChoice: Choice?,
    computerChoice: Choice?,
    outcome: Int?,
    rounds: Int
) {
    val t = rememberInfiniteTransition(label = "shake")
    val bounce by t.animateFloat(
        0f, 1f, infiniteRepeatable(tween(160, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bounce"
    )
    val shakeOffset = if (phase == Phase.SHAKING) -26f * bounce else 0f

    var countdown by remember { mutableStateOf("") }
    LaunchedEffect(phase) {
        if (phase == Phase.SHAKING) {
            for (word in listOf("Rock…", "Paper…", "Scissors…")) {
                countdown = word
                delay(316)
            }
        }
    }

    GlassCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HandCard(
                label = "You",
                choice = if (phase == Phase.SHAKING) null else playerChoice,
                placeholder = if (phase == Phase.SHAKING) "✊" else "❔",
                isWinner = phase == Phase.REVEAL && outcome == 1,
                isLoser = phase == Phase.REVEAL && outcome == -1,
                shakeOffset = shakeOffset,
                rounds = rounds,
                mirror = false
            )
            Box(Modifier.width(72.dp), contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = if (phase == Phase.SHAKING) countdown else "⚡",
                    transitionSpec = { (scaleIn(spring(0.4f)) + fadeIn()).togetherWith(fadeOut()) },
                    label = "countdown"
                ) { text ->
                    Text(
                        text, color = Color.White, fontWeight = FontWeight.Bold,
                        fontSize = if (text == "⚡") 30.sp else 13.sp, textAlign = TextAlign.Center
                    )
                }
            }
            HandCard(
                label = "CPU",
                choice = if (phase == Phase.SHAKING) null else computerChoice,
                placeholder = if (phase == Phase.SHAKING) "✊" else "🤖",
                isWinner = phase == Phase.REVEAL && outcome == -1,
                isLoser = phase == Phase.REVEAL && outcome == 1,
                shakeOffset = shakeOffset,
                rounds = rounds,
                mirror = true
            )
        }
    }
}

@Composable
private fun HandCard(
    label: String,
    choice: Choice?,
    placeholder: String,
    isWinner: Boolean,
    isLoser: Boolean,
    shakeOffset: Float,
    rounds: Int,
    mirror: Boolean
) {
    val glow = rememberInfiniteTransition(label = "glow")
    val pulse by glow.animateFloat(
        1f, 1.18f, infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val fade by animateFloatAsState(if (isLoser) 0.45f else 1f, tween(400), label = "fade")
    val colors = choice?.colors ?: listOf(Color(0x40FFFFFF), Color(0x14FFFFFF))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(fade)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(124.dp)) {
            if (isWinner) {
                Box(
                    Modifier
                        .size(110.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFFFFE066).copy(alpha = 0.7f), Color.Transparent))
                        )
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer { translationY = shakeOffset.dp.toPx() }
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors))
                    .border(2.dp, Color.White.copy(alpha = 0.35f), CircleShape)
            ) {
                AnimatedContent(
                    targetState = Triple(choice, placeholder, rounds),
                    transitionSpec = {
                        (scaleIn(spring(dampingRatio = 0.4f, stiffness = 300f), initialScale = 0.2f) + fadeIn())
                            .togetherWith(scaleOut(targetScale = 0.4f) + fadeOut())
                    },
                    label = "hand"
                ) { (c, ph, _) ->
                    Text(
                        c?.emoji ?: ph,
                        fontSize = 50.sp,
                        modifier = Modifier.graphicsLayer { scaleX = if (mirror) -1f else 1f }
                    )
                }
            }
        }
        Text(
            choice?.title ?: label,
            color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ResultBanner(phase: Phase, outcome: Int?, rounds: Int) {
    val (text, colors) = when {
        phase == Phase.SHAKING -> "Shoot!" to listOf(Glass, Glass)
        outcome == 1 -> "🎉 You win the round!" to WinColors
        outcome == -1 -> "💥 CPU takes it!" to LoseColors
        outcome == 0 -> "🤝 It's a draw!" to DrawColors
        else -> "✨ Ready? Let's play!" to listOf(Glass, Glass)
    }
    val onBright = outcome != null && phase == Phase.REVEAL
    AnimatedContent(
        targetState = Triple(text, colors, rounds),
        transitionSpec = {
            (scaleIn(spring(dampingRatio = 0.45f, stiffness = 350f), initialScale = 0.6f) + fadeIn())
                .togetherWith(fadeOut(tween(120)))
        },
        label = "banner"
    ) { (msg, cols, _) ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(cols))
                .border(1.dp, GlassBorder, RoundedCornerShape(50))
                .padding(vertical = 14.dp, horizontal = 20.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Text(
                msg,
                color = if (onBright) Ink else Color.White,
                fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center
            )
        }
    }
}

/* ---------- Buttons ---------- */

@Composable
private fun ChoiceButton(
    choice: Choice,
    index: Int,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        when {
            pressed -> 0.88f
            selected -> 1.06f
            else -> 1f
        },
        spring(dampingRatio = 0.4f, stiffness = 500f), label = "press"
    )
    val bobTransition = rememberInfiniteTransition(label = "bob")
    val bob by bobTransition.animateFloat(
        -4f, 4f,
        infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse,
            initialStartOffset = StartOffset(index * 300)
        ),
        label = "bobY"
    )
    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f, label = "enabled")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale; scaleY = pressScale
                translationY = if (enabled) bob.dp.toPx() else 0f
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(choice.colors))
            .border(
                if (selected) 3.dp else 1.dp,
                Color.White.copy(alpha = if (selected) 0.9f else 0.3f),
                RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClickLabel = "Play ${choice.title}",
                onClick = onClick
            )
            .padding(vertical = 18.dp)
    ) {
        Text(choice.emoji, fontSize = 40.sp)
        Spacer(Modifier.height(6.dp))
        Text(choice.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PillButton(
    text: String,
    colors: List<Color>,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, spring(0.4f), label = "pill")
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .scale(scale)
            .clip(CircleShape)
            .then(modifier)
            .background(Brush.horizontalGradient(colors))
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
    ) {
        Text(text, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

/* ---------- Match over ---------- */

@Composable
private fun MatchOverOverlay(
    visible: Boolean,
    playerWon: Boolean,
    playerScore: Int,
    computerScore: Int,
    onPlayAgain: () -> Unit
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(tween(300)), exit = fadeOut()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xCC0B0820))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
            contentAlignment = Alignment.Center
        ) {
            if (playerWon) Confetti()

            val trophy = rememberInfiniteTransition(label = "trophy")
            val tilt by trophy.animateFloat(
                -10f, 10f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "tilt"
            )
            var appeared by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { appeared = true }
            val pop by animateFloatAsState(
                if (appeared) 1f else 0.3f, spring(dampingRatio = 0.45f, stiffness = 250f), label = "pop"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(28.dp)
                    .scale(pop)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF3A1F7A), Color(0xFF1C1446))))
                    .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
                    .padding(28.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                Text(
                    if (playerWon) "🏆" else "🤖",
                    fontSize = 84.sp,
                    modifier = Modifier.graphicsLayer { rotationZ = tilt }
                )
                Text(
                    if (playerWon) "You win the match!" else "CPU wins the match!",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 28.sp, fontWeight = FontWeight.Black,
                        brush = Brush.horizontalGradient(if (playerWon) WinColors else LoseColors)
                    )
                )
                Text(
                    "Final score  $playerScore – $computerScore",
                    color = SoftText, fontSize = 17.sp, fontWeight = FontWeight.Medium
                )
                Text(
                    if (playerWon) "Absolute legend. 🌟" else "So close! Want a rematch?",
                    color = Color.White, fontSize = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                PillButton(
                    text = "Play Again",
                    colors = listOf(Color(0xFFFF9A62), Color(0xFFFF4E9B), Color(0xFF8A4DFF)),
                    textColor = Color.White,
                    onClick = onPlayAgain
                )
            }
        }
    }
}

private class ConfettiPiece(
    val x: Float, val startY: Float, val speed: Float, val drift: Float,
    val size: Float, val spin: Float, val color: Color
)

@Composable
private fun Confetti() {
    val palette = listOf(
        Color(0xFFFF4E9B), Color(0xFF4FD1FF), Color(0xFFFFE066),
        Color(0xFF3CF0A0), Color(0xFF8A4DFF), Color(0xFFFF9A62)
    )
    val pieces = remember {
        List(90) {
            ConfettiPiece(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.8f,
                drift = Random.nextFloat() * 6f,
                size = 6f + Random.nextFloat() * 10f,
                spin = 180f + Random.nextFloat() * 540f,
                color = palette.random()
            )
        }
    }
    val t = rememberInfiniteTransition(label = "confetti")
    val progress by t.animateFloat(
        0f, 1f, infiniteRepeatable(tween(3500, easing = LinearEasing)), label = "fall"
    )
    Canvas(Modifier.fillMaxSize()) {
        pieces.forEach { p ->
            val y = ((p.startY + progress * p.speed) % 1.1f - 0.05f) * size.height
            val x = (p.x + 0.03f * sin(progress * 2 * PI.toFloat() * 2 + p.drift)) * size.width
            val s = p.size.dp.toPx() / 2
            rotate(progress * p.spin + p.drift * 60f, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(x - s, y - s / 2),
                    size = Size(s * 2, s)
                )
            }
        }
    }
}
