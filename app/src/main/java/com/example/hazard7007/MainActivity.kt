package com.example.hazard7007

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF050505)
private val Panel = Color(0xFF242020)
private val PanelInner = Color(0xFF171515)
private val Red = Color(0xFFB91C1C)
private val Orange = Color(0xFFF97316)
private val GoldText = Color(0xFFE2B65C)
private val Muted = Color(0xFF9B9292)

class MainActivity : ComponentActivity() {
    private var tone: ToneGenerator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 35)
        setContent {
            HazardApp(
                onDemoTone = { mode ->
                    val t = tone ?: return@HazardApp
                    when (mode) {
                        "HILO" -> t.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                        "YELP" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 160)
                        "WAIL" -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 220)
                        "HORN" -> t.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                        "PA" -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                        else -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    }
                },
                onStop = { tone?.stopTone() }
            )
        }
    }
    override fun onDestroy() {
        tone?.release()
        tone = null
        super.onDestroy()
    }
}

@Composable
fun HazardApp(onDemoTone: (String) -> Unit, onStop: () -> Unit) {
    val modes = listOf("HILO", "YELP", "WAIL", "STANDBY")
    var selected by remember { mutableStateOf("STANDBY") }
    var volume by remember { mutableFloatStateOf(35f) }
    var power by remember { mutableStateOf(true) }
    var manual by remember { mutableStateOf(false) }
    var radio by remember { mutableStateOf(false) }
    var pa by remember { mutableStateOf(false) }
    val running = power && selected != "STANDBY"

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Red,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Box(
            Modifier.fillMaxSize().background(Bg).padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("HAZARD 7007", color = Color.White, fontSize = 21.sp,
                            fontWeight = FontWeight.Black, letterSpacing = 2.5.sp)
                        Text("PREMIER CONTROL • ANDROID", color = Red,
                            fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(CircleShape)
                            .background(if (running) Orange else Muted))
                        Spacer(Modifier.width(6.dp))
                        Text(if (!power) "OFF" else if (running) "ACTIVE" else "READY",
                            color = GoldText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Column(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF302A2A), Panel, Color(0xFF1A1717))))
                        .border(2.dp, Red.copy(alpha = .8f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 10.dp, vertical = 13.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().height(116.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RotaryDial(
                                rotation = -35f + (volume / 100f * 70f),
                                modifier = Modifier.size(72.dp),
                                onClick = { volume = if (volume >= 90f) 10f else volume + 10f }
                            )
                            Spacer(Modifier.height(5.dp))
                            Text("OFF/VOL", color = GoldText, fontSize = 11.sp,
                                fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                        }

                        Column(
                            Modifier.weight(1.45f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            modes.forEach { mode ->
                                Row(
                                    Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            if (power) {
                                                selected = mode
                                                manual = false
                                                if (mode == "STANDBY") onStop() else onDemoTone(mode)
                                            }
                                        }.padding(vertical = 3.dp, horizontal = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(mode, modifier = Modifier.weight(1f),
                                        color = if (selected == mode && power) Color.White else GoldText,
                                        fontSize = 10.sp, fontWeight = FontWeight.Black,
                                        letterSpacing = .7.sp)
                                    Box(Modifier.size(7.dp).clip(CircleShape)
                                        .background(if (selected == mode && power) Red else Color(0xFF66543A)))
                                }
                            }
                        }

                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RotaryDial(
                                rotation = modes.indexOf(selected).coerceAtLeast(0) * 28f - 42f,
                                modifier = Modifier.size(82.dp),
                                onClick = {
                                    if (power) {
                                        val next = modes[(modes.indexOf(selected) + 1) % modes.size]
                                        selected = next
                                        manual = false
                                        if (next == "STANDBY") onStop() else onDemoTone(next)
                                    }
                                }
                            )
                            Spacer(Modifier.height(5.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Text("PA", color = if (pa) Orange else GoldText,
                                    fontSize = 10.sp, fontWeight = FontWeight.Black,
                                    modifier = Modifier.clickable { pa = !pa; if (pa && power) onDemoTone("PA") })
                                Text("RADIO", color = if (radio) Orange else GoldText,
                                    fontSize = 10.sp, fontWeight = FontWeight.Black,
                                    modifier = Modifier.clickable { radio = !radio })
                            }
                        }
                    }

                    Spacer(Modifier.height(9.dp))
                    Row(
                        Modifier.fillMaxWidth().height(54.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.weight(1f).fillMaxHeight().padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0A0909))
                                .border(1.dp, Red.copy(alpha = .8f), RoundedCornerShape(8.dp))
                                .clickable {
                                    power = !power
                                    if (!power) {
                                        selected = "STANDBY"; manual = false
                                        onStop()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (power) "Ⅱ Ⅱ Ⅱ" else "○", color = if (power) Orange else Muted,
                                    fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text(if (power) "POWER ON" else "POWER OFF", color = Muted, fontSize = 7.sp)
                            }
                        }

                        Column(
                            Modifier.weight(1.15f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("PREMIER", color = GoldText, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text("HAZARD", color = GoldText, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text("7007", color = GoldText, fontSize = 13.sp, fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp)
                        }

                        PhysicalButton(
                            "MAN", manual, Modifier.weight(.72f).fillMaxHeight().padding(start = 4.dp)
                        ) {
                            if (power) {
                                manual = !manual
                                if (manual) { selected = "MAN"; onDemoTone("MAN") }
                                else { selected = "STANDBY"; onStop() }
                            }
                        }
                        PhysicalButton(
                            "HORN", false, Modifier.weight(.72f).fillMaxHeight().padding(start = 6.dp)
                        ) { if (power) onDemoTone("HORN") }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("DEMO ONLY • NO EXTERNAL HARDWARE", color = Muted,
                    fontSize = 9.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun RotaryDial(rotation: Float, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF454040), Color(0xFF171515), Color(0xFF080707))))
            .border(3.dp, Color(0xFF090808), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize().padding(3.dp)) {
            val r = size.minDimension / 2f
            for (i in 0 until 36) {
                val angle = Math.toRadians((i * 10.0) - 90.0)
                val inner = r * .82f
                val outer = r * .97f
                drawLine(
                    color = Color(0xFF777070),
                    start = androidx.compose.ui.geometry.Offset(
                        center.x + kotlin.math.cos(angle).toFloat() * inner,
                        center.y + kotlin.math.sin(angle).toFloat() * inner
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        center.x + kotlin.math.cos(angle).toFloat() * outer,
                        center.y + kotlin.math.sin(angle).toFloat() * outer
                    ),
                    strokeWidth = 1.4.dp.toPx()
                )
            }
        }
        Box(
            Modifier.fillMaxWidth(.12f).fillMaxHeight(.72f)
                .rotate(rotation)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF090808))
        )
        Box(
            Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
                .size(7.dp).clip(CircleShape).background(Color.White)
        )
    }
}

@Composable
fun PhysicalButton(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(7.dp))
            .background(Color(0xFF100E0E))
            .border(1.dp, if (active) Orange else Red.copy(alpha = .8f), RoundedCornerShape(7.dp))
            .clickable { onClick() }.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(label, color = GoldText, fontSize = 9.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(4.dp))
                .background(if (active) Brush.verticalGradient(listOf(Orange, Red))
                    else Brush.verticalGradient(listOf(Color(0xFFFFF5D9), Color(0xFFC9BFA9))))
                .border(1.dp, Color(0xFF090808), RoundedCornerShape(4.dp))
        )
    }
}
