package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.MathVerificationEngine
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInkDeep
import com.example.ui.theme.MathMonoTextStyle
import com.example.ui.theme.SaffronGold
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Interactive Mathematics Blackboard:
 * Hands-on Canvas demonstrations for Coordinate Linear Pairs, Quadratic Parabolas,
 * Thales BPT Ratio Invariance, and Circle External Tangents.
 */
@Composable
fun InteractiveMathBlackboardCard(modifier: Modifier = Modifier) {
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0=Linear Graph, 1=Parabola, 2=Thales BPT, 3=Circle Tangents

    // Linear pair parameters: y = m1*x + c1 and y = m2*x + c2
    var m1 by rememberSaveable { mutableFloatStateOf(1f) }
    var b1 by rememberSaveable { mutableFloatStateOf(1f) }
    var m2 by rememberSaveable { mutableFloatStateOf(-1f) }
    var b2 by rememberSaveable { mutableFloatStateOf(3f) }

    // Quadratic parameters: a*x^2 + b*x + c
    var qa by rememberSaveable { mutableFloatStateOf(1f) }
    var qb by rememberSaveable { mutableFloatStateOf(-2f) }
    var qc by rememberSaveable { mutableFloatStateOf(-3f) }

    // Thales BPT ratio slider (AD / AB)
    var bptFrac by rememberSaveable { mutableFloatStateOf(0.45f) }

    // Circle external point distance OP (radius fixed at 3.0)
    var opDistance by rememberSaveable { mutableFloatStateOf(5.0f) }

    // Constructions step slider (1..4)
    var constructionStep by rememberSaveable { mutableIntStateOf(4) }

    // Trig Elevation slider (base distance & angle)
    var elevDistance by rememberSaveable { mutableFloatStateOf(30f) }
    var elevAngleDeg by rememberSaveable { mutableIntStateOf(30) }

    // Frustum slider (bottom radius r1, top radius r2, height h)
    var frustumR1 by rememberSaveable { mutableFloatStateOf(4f) }
    var frustumR2 by rememberSaveable { mutableFloatStateOf(2f) }
    var frustumH by rememberSaveable { mutableFloatStateOf(6f) }

    Card(
        colors = CardDefaults.cardColors(containerColor = IndigoInkDeep),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_math_blackboard")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Interactive Mathematics Blackboard",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "Drag the sliders to manipulate live SSLC graphs, parabolas, BPT ratios, circle tangents, constructions, trig heights & frustum solids.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                listOf(
                    "Linear Graph (4M)",
                    "Quadratic Parabola",
                    "Thales BPT",
                    "Circle Tangents",
                    "Constructions (3M)",
                    "Trig Elevation",
                    "Frustum Solid"
                ).forEachIndexed { idx, label ->
                    FilterChip(
                        selected = mode == idx,
                        onClick = { mode = idx },
                        label = { Text(label) }
                    )
                }
            }

            when (mode) {
                0 -> {
                    val linSol = MathVerificationEngine.solveLinearPair(
                        m1.toDouble(), -1.0, b1.toDouble(),
                        m2.toDouble(), -1.0, b2.toDouble()
                    )
                    LinearGraphCanvas(m1 = m1, b1 = b1, m2 = m2, b2 = b2)
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Line 1: y = ${fmt1(m1)}x + (${fmt1(b1)})   |   Line 2: y = ${fmt1(m2)}x + (${fmt1(b2)})",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = "Result: ${linSol.finalAnswer} (${linSol.summary})",
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Line 1 Slope (m₁)", m1, -3f..3f) { m1 = snapHalf(it) }
                    ChalkboardSlider("Line 1 Intercept (c₁)", b1, -4f..4f) { b1 = snapHalf(it) }
                    ChalkboardSlider("Line 2 Slope (m₂)", m2, -3f..3f) { m2 = snapHalf(it) }
                    ChalkboardSlider("Line 2 Intercept (c₂)", b2, -4f..4f) { b2 = snapHalf(it) }
                }

                1 -> {
                    val safeA = if (kotlin.math.abs(qa) < 0.4f) 0.5f else qa
                    val quadSol = MathVerificationEngine.solveQuadratic(
                        safeA.toDouble(), qb.toDouble(), qc.toDouble()
                    )
                    ParabolaCanvas(a = safeA, b = qb, c = qc)
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "y = ${fmt1(safeA)}x² + (${fmt1(qb)})x + (${fmt1(qc)})",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = "${quadSol.summary} -> ${quadSol.finalAnswer}",
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Coefficient a (≠0)", qa, -2f..2f) { qa = snapHalf(it) }
                    ChalkboardSlider("Coefficient b", qb, -5f..5f) { qb = snapHalf(it) }
                    ChalkboardSlider("Constant c", qc, -5f..5f) { qc = snapHalf(it) }
                }

                2 -> {
                    ThalesBptCanvas(fraction = bptFrac)
                    val ad = bptFrac * 10f
                    val db = 10f - ad
                    val ae = bptFrac * 8f
                    val ec = 8f - ae
                    val ratio = if (db > 0.01f) ad / db else 0f
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "DE || BC  •  AD=${fmt1(ad)}, DB=${fmt1(db)}  |  AE=${fmt1(ae)}, EC=${fmt1(ec)}",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = "AD / DB = ${fmt2(ratio)}  ==  AE / EC = ${fmt2(ratio)} (Thales Invariance)",
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Slide Parallel Line DE", bptFrac, 0.2f..0.8f) { bptFrac = it }
                }

                3 -> {
                    val r = 3.0f
                    val tangentLen = sqrt((opDistance * opDistance - r * r).coerceAtLeast(0f))
                    CircleTangentCanvas(opRatio = opDistance / r)
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Radius r = 3.0 cm  •  OP = ${fmt1(opDistance)} cm  •  ∠OQP = ∠ORP = 90°",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = "PQ = PR = √(OP² - r²) = ${fmt2(tangentLen)} cm (Theorem 4.2)",
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Distance OP from Centre (cm)", opDistance, 3.5f..8.0f) {
                        opDistance = snapHalf(it)
                    }
                }

                4 -> {
                    LineDivisionConstructionCanvas(step = constructionStep)
                    val stepDescription = when (constructionStep) {
                        1 -> "Step 1: Draw segment AB and acute ray AX below AB."
                        2 -> "Step 2: Mark m + n = 3 + 2 = 5 equal compass arcs A₁..A₅ along ray AX."
                        3 -> "Step 3: Join the 5th arc A₅ to endpoint B."
                        else -> "Step 4: From A₃ draw A₃P || A₅B using equal corresponding arcs ⇒ AP : PB = 3 : 2."
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Ch 6 Line Segment Division (Ratio 3 : 2) • Compass Arcs Shown",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = stepDescription,
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Construction Step (1 to 4)", constructionStep.toFloat(), 1f..4f) {
                        constructionStep = it.roundToInt().coerceIn(1, 4)
                    }
                }

                5 -> {
                    val trigSol = MathVerificationEngine.solveElevationHeight(elevDistance.toDouble(), elevAngleDeg)
                    TrigElevationCanvas(angleDeg = elevAngleDeg)
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Base BC = ${fmt1(elevDistance)} m  •  Elevation θ = $elevAngleDeg°  •  tan($elevAngleDeg°) = h / BC",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = "${trigSol.summary} m (Right Triangle Line-of-Sight Diagram)",
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30, 45, 60).forEach { ang ->
                            FilterChip(
                                selected = elevAngleDeg == ang,
                                onClick = { elevAngleDeg = ang },
                                label = { Text("${ang}°") }
                            )
                        }
                    }
                    ChalkboardSlider("Horizontal Distance BC (m)", elevDistance, 10f..60f) {
                        elevDistance = (it / 5f).roundToInt() * 5f
                    }
                }

                else -> {
                    val safeR1 = maxOf(frustumR1, frustumR2 + 0.5f)
                    val fruSol = MathVerificationEngine.solveMensurationFrustum(safeR1.toDouble(), frustumR2.toDouble(), frustumH.toDouble())
                    FrustumSolidCanvas(r1 = safeR1, r2 = frustumR2, h = frustumH)
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "r₁ = ${fmt1(safeR1)} cm, r₂ = ${fmt1(frustumR2)} cm, h = ${fmt1(frustumH)} cm",
                                style = MathMonoTextStyle,
                                color = Color.White
                            )
                            Text(
                                text = fruSol.summary,
                                style = MaterialTheme.typography.labelLarge,
                                color = SaffronGold
                            )
                        }
                    }
                    ChalkboardSlider("Bottom Radius r₁ (cm)", frustumR1, 3f..7f) { frustumR1 = snapHalf(it) }
                    ChalkboardSlider("Top Radius r₂ (cm)", frustumR2, 1f..2.5f) { frustumR2 = snapHalf(it) }
                    ChalkboardSlider("Height h (cm)", frustumH, 3f..12f) { frustumH = snapHalf(it) }
                }
            }
        }
    }
}

@Composable
private fun ChalkboardSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(
            text = "$label: ${fmt1(value)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFE2E8F0)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = SaffronGold,
                activeTrackColor = SaffronGold
            )
        )
    }
}

@Composable
private fun LinearGraphCanvas(m1: Float, b1: Float, m2: Float, b2: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = (minOf(size.width, size.height) / 14f)

        fun toScreen(x: Float, y: Float): Offset = Offset(cx + x * scale, cy - y * scale)

        // Grid
        for (i in -6..6) {
            val gx = cx + i * scale
            val gy = cy - i * scale
            drawLine(Color(0xFF1E293B), Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
            drawLine(Color(0xFF1E293B), Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
        }
        // Axes
        drawLine(Color(0xFF94A3B8), Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2.5f)
        drawLine(Color(0xFF94A3B8), Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2.5f)

        // Line 1
        drawLine(
            color = Color(0xFF38BDF8),
            start = toScreen(-7f, m1 * -7f + b1),
            end = toScreen(7f, m1 * 7f + b1),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )
        // Line 2
        drawLine(
            color = EmeraldChalk,
            start = toScreen(-7f, m2 * -7f + b2),
            end = toScreen(7f, m2 * 7f + b2),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )

        // Intersection point if unique
        val denom = m1 - m2
        if (kotlin.math.abs(denom) > 0.05f) {
            val ix = (b2 - b1) / denom
            val iy = m1 * ix + b1
            val pt = toScreen(ix, iy)
            drawCircle(color = SaffronGold, radius = 9f, center = pt)
        }
    }
}

@Composable
private fun ParabolaCanvas(a: Float, b: Float, c: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = (minOf(size.width, size.height) / 14f)
        fun toScreen(x: Float, y: Float): Offset = Offset(cx + x * scale, cy - y * scale)

        for (i in -6..6) {
            val gx = cx + i * scale
            val gy = cy - i * scale
            drawLine(Color(0xFF1E293B), Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
            drawLine(Color(0xFF1E293B), Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
        }
        drawLine(Color(0xFF94A3B8), Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2.5f)
        drawLine(Color(0xFF94A3B8), Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2.5f)

        val path = Path()
        var started = false
        for (step in -60..60) {
            val x = step / 10f
            val y = a * x * x + b * x + c
            val p = toScreen(x, y)
            if (!started) {
                path.moveTo(p.x, p.y)
                started = true
            } else {
                path.lineTo(p.x, p.y)
            }
        }
        drawPath(path = path, color = Color(0xFF38BDF8), style = Stroke(width = 5f, cap = StrokeCap.Round))

        // Plot real roots on x-axis
        val disc = b * b - 4f * a * c
        if (disc >= 0f) {
            val r1 = (-b + sqrt(disc)) / (2f * a)
            val r2 = (-b - sqrt(disc)) / (2f * a)
            drawCircle(color = SaffronGold, radius = 9f, center = toScreen(r1, 0f))
            drawCircle(color = SaffronGold, radius = 9f, center = toScreen(r2, 0f))
        }
    }
}

@Composable
private fun ThalesBptCanvas(fraction: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val ax = size.width * 0.48f
        val ay = size.height * 0.14f
        val bx = size.width * 0.16f
        val by = size.height * 0.86f
        val cx = size.width * 0.84f
        val cy = size.height * 0.86f

        val dx = ax + (bx - ax) * fraction
        val dy = ay + (by - ay) * fraction
        val ex = ax + (cx - ax) * fraction
        val ey = ay + (cy - ay) * fraction

        val triPath = Path().apply {
            moveTo(ax, ay)
            lineTo(bx, by)
            lineTo(cx, cy)
            close()
        }
        drawPath(triPath, color = Color(0xFF38BDF8), style = Stroke(width = 5f))

        // Construction diagonals BE and CD
        drawLine(Color(0xFF64748B), Offset(bx, by), Offset(ex, ey), strokeWidth = 2.5f)
        drawLine(Color(0xFF64748B), Offset(cx, cy), Offset(dx, dy), strokeWidth = 2.5f)

        // Parallel segment DE
        drawLine(SaffronGold, Offset(dx, dy), Offset(ex, ey), strokeWidth = 6f, cap = StrokeCap.Round)
        drawCircle(Color.White, radius = 7f, center = Offset(ax, ay))
        drawCircle(Color.White, radius = 7f, center = Offset(bx, by))
        drawCircle(Color.White, radius = 7f, center = Offset(cx, cy))
        drawCircle(SaffronGold, radius = 8f, center = Offset(dx, dy))
        drawCircle(SaffronGold, radius = 8f, center = Offset(ex, ey))
    }
}

@Composable
private fun CircleTangentCanvas(opRatio: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val radius = minOf(size.width, size.height) * 0.25f
        val ox = size.width * 0.30f
        val oy = size.height * 0.50f
        val px = (ox + radius * opRatio).coerceAtMost(size.width * 0.90f)
        val py = oy

        val d = (px - ox).coerceAtLeast(radius * 1.05f)
        val cosAlpha = radius / d
        val sinAlpha = sqrt((1f - cosAlpha * cosAlpha).coerceAtLeast(0f))

        val qx = ox + radius * cosAlpha
        val qy = oy - radius * sinAlpha
        val rx = ox + radius * cosAlpha
        val ry = oy + radius * sinAlpha

        // Circle
        drawCircle(
            color = Color(0xFF38BDF8),
            radius = radius,
            center = Offset(ox, oy),
            style = Stroke(width = 5f)
        )
        // Radii OQ & OR + line OP
        drawLine(Color(0xFF94A3B8), Offset(ox, oy), Offset(px, py), strokeWidth = 2.5f)
        drawLine(EmeraldChalk, Offset(ox, oy), Offset(qx, qy), strokeWidth = 4f)
        drawLine(EmeraldChalk, Offset(ox, oy), Offset(rx, ry), strokeWidth = 4f)
        // Tangents PQ & PR
        drawLine(SaffronGold, Offset(px, py), Offset(qx, qy), strokeWidth = 6f, cap = StrokeCap.Round)
        drawLine(SaffronGold, Offset(px, py), Offset(rx, ry), strokeWidth = 6f, cap = StrokeCap.Round)

        drawCircle(Color.White, radius = 7f, center = Offset(ox, oy))
        drawCircle(SaffronGold, radius = 8f, center = Offset(px, py))
        drawCircle(EmeraldChalk, radius = 8f, center = Offset(qx, qy))
        drawCircle(EmeraldChalk, radius = 8f, center = Offset(rx, ry))
    }
}

@Composable
private fun LineDivisionConstructionCanvas(step: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val ax = size.width * 0.14f
        val ay = size.height * 0.25f
        val bx = size.width * 0.86f
        val by = ay

        // Step 1: Segment AB and acute ray AX
        drawLine(Color(0xFF38BDF8), Offset(ax, ay), Offset(bx, by), strokeWidth = 6f, cap = StrokeCap.Round)
        drawCircle(Color.White, radius = 7f, center = Offset(ax, ay))
        drawCircle(Color.White, radius = 7f, center = Offset(bx, by))

        val rayEndX = size.width * 0.72f
        val rayEndY = size.height * 0.88f
        drawLine(Color(0xFF94A3B8), Offset(ax, ay), Offset(rayEndX, rayEndY), strokeWidth = 3f)

        // Step 2: 5 equal arcs A1..A5 along ray AX
        if (step >= 2) {
            for (i in 1..5) {
                val t = i / 5.5f
                val px = ax + (rayEndX - ax) * t
                val py = ay + (rayEndY - ay) * t
                drawCircle(
                    color = if (i == 3 || i == 5) SaffronGold else Color(0xFF64748B),
                    radius = if (i == 3 || i == 5) 7f else 5f,
                    center = Offset(px, py)
                )
            }
        }

        // Step 3: Join A5 to B
        val t5 = 5 / 5.5f
        val a5x = ax + (rayEndX - ax) * t5
        val a5y = ay + (rayEndY - ay) * t5
        if (step >= 3) {
            drawLine(EmeraldChalk, Offset(a5x, a5y), Offset(bx, by), strokeWidth = 4f)
        }

        // Step 4: Draw A3P || A5B dividing AB in 3:2
        if (step >= 4) {
            val t3 = 3 / 5.5f
            val a3x = ax + (rayEndX - ax) * t3
            val a3y = ay + (rayEndY - ay) * t3
            val px = ax + (bx - ax) * (3f / 5f)
            val py = ay
            drawLine(SaffronGold, Offset(a3x, a3y), Offset(px, py), strokeWidth = 5f, cap = StrokeCap.Round)
            drawCircle(SaffronGold, radius = 9f, center = Offset(px, py))
        }
    }
}

@Composable
private fun TrigElevationCanvas(angleDeg: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val bx = size.width * 0.78f
        val by = size.height * 0.84f
        val cx = size.width * 0.18f
        val cy = by
        val tanFactor = when (angleDeg) {
            60 -> 0.72f
            45 -> 0.56f
            else -> 0.36f
        }
        val ay = (by - size.height * tanFactor).coerceAtLeast(size.height * 0.12f)

        // Tower AB, Base BC, Line of sight AC
        drawLine(EmeraldChalk, Offset(bx, by), Offset(bx, ay), strokeWidth = 6f, cap = StrokeCap.Round)
        drawLine(Color(0xFF38BDF8), Offset(cx, cy), Offset(bx, by), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(SaffronGold, Offset(cx, cy), Offset(bx, ay), strokeWidth = 5f, cap = StrokeCap.Round)

        // Right angle box at B
        val box = 16f
        drawLine(Color.White, Offset(bx - box, by), Offset(bx - box, by - box), strokeWidth = 2f)
        drawLine(Color.White, Offset(bx - box, by - box), Offset(bx, by - box), strokeWidth = 2f)

        drawCircle(SaffronGold, radius = 8f, center = Offset(bx, ay))
        drawCircle(Color.White, radius = 7f, center = Offset(bx, by))
        drawCircle(Color(0xFF38BDF8), radius = 8f, center = Offset(cx, cy))
    }
}

@Composable
private fun FrustumSolidCanvas(r1: Float, r2: Float, h: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Color(0xFF0B1528), RoundedCornerShape(14.dp))
    ) {
        val cx = size.width * 0.5f
        val bottomY = size.height * 0.80f
        val topY = (bottomY - (h / 12f) * size.height * 0.56f).coerceAtLeast(size.height * 0.16f)
        val rx1 = (r1 / 7f) * size.width * 0.34f
        val rx2 = (r2 / 7f) * size.width * 0.34f

        // Slant sides
        drawLine(Color(0xFF38BDF8), Offset(cx - rx1, bottomY), Offset(cx - rx2, topY), strokeWidth = 5f)
        drawLine(Color(0xFF38BDF8), Offset(cx + rx1, bottomY), Offset(cx + rx2, topY), strokeWidth = 5f)
        // Height axis
        drawLine(SaffronGold, Offset(cx, topY), Offset(cx, bottomY), strokeWidth = 3f)
        // Top & bottom rims
        drawOval(
            color = EmeraldChalk,
            topLeft = Offset(cx - rx2, topY - 14f),
            size = androidx.compose.ui.geometry.Size(rx2 * 2f, 28f),
            style = Stroke(width = 4f)
        )
        drawOval(
            color = Color(0xFF38BDF8),
            topLeft = Offset(cx - rx1, bottomY - 18f),
            size = androidx.compose.ui.geometry.Size(rx1 * 2f, 36f),
            style = Stroke(width = 4f)
        )
    }
}

private fun snapHalf(v: Float): Float = (v * 2f).roundToInt() / 2f
private fun fmt1(v: Float): String = ((v * 10f).roundToInt() / 10f).toString()
private fun fmt2(v: Float): String = ((v * 100f).roundToInt() / 100f).toString()
