package com.example.hazard7007

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Bg = Color(0xFF151515)
private val Panel = Color(0xFF202020)
private val Cyan = Color(0xFFB91C1C)
private val Muted = Color(0xFFA3A3A3)

class MainActivity : ComponentActivity() {
    private var tone: ToneGenerator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 35)
        setContent { HazardApp(
            onDemoTone = { mode ->
                val t = tone ?: return@HazardApp
                when (mode) {
                    "HILO" -> t.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                    "YELP" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 160)
                    "WAIL" -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 220)
                    "HORN" -> t.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                    else -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                }
            },
            onStop = { tone?.stopTone() }
        ) }
    }
    override fun onDestroy() { tone?.release(); tone = null; super.onDestroy() }
}

@Composable
fun HazardApp(onDemoTone: (String) -> Unit, onStop: () -> Unit) {
    var selected by remember { mutableStateOf("STANDBY") }
    var volume by remember { mutableFloatStateOf(35f) }
    var manual by remember { mutableStateOf(false) }
    var radio by remember { mutableStateOf(false) }
    var running by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Cyan, onBackground = Color.White,
        onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF1C1C1C), Bg, Color(0xFF0D0D0D)))
        )) {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("HAZARD", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                        Text("7007  •  SOUND CONTROL DEMO", color = Cyan, fontSize = 10.sp, letterSpacing = 1.5.sp)
                    }
                    Surface(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(50)) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(if (running) Color(0xFFF97316) else Muted))
                            Spacer(Modifier.width(7.dp))
                            Text(if (running) "ACTIVE" else "READY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Panel),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF3A3A3A), RoundedCornerShape(28.dp))
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SOUND MODES", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text("DEMO ONLY", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(14.dp))
                        listOf("HILO", "YELP", "WAIL", "STANDBY").forEach { mode ->
                            ModeRow(mode, selected == mode) {
                                selected = mode
                                running = mode != "STANDBY"
                                if (running) onDemoTone(mode) else onStop()
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("OUTPUT LEVEL  •  ${volume.toInt()}%", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f)
                        Text("The phone's media volume controls actual playback level.", color = Muted, fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionTile("MAN", "Manual demo", manual, Modifier.weight(1f)) {
                        manual = !manual
                        if (manual) { selected = "MAN"; running = true; onDemoTone("MAN") }
                        else { selected = "STANDBY"; running = false; onStop() }
                    }
                    ActionTile("HORN", "Test beep", false, Modifier.weight(1f)) { onDemoTone("HORN") }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionTile("PA", "Local demo", false, Modifier.weight(1f)) { onDemoTone("PA") }
                    ActionTile("RADIO", "UI preview", radio, Modifier.weight(1f)) { radio = !radio }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Cyan, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("No emergency siren, radio transmission or external hardware.", color = Muted, fontSize = 10.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text("HAZARD 7007  •  ANDROID CONCEPT 1.0", color = Color(0xFF777777), fontSize = 9.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun ModeRow(label: String, active: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp))
            .background(if (active) Color(0xFF3B1717) else Color(0xFF181818))
            .border(1.dp, if (active) Cyan.copy(alpha = .8f) else Color(0xFF3A3A3A), RoundedCornerShape(15.dp))
            .clickable { onClick() }.padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(9.dp).clip(RoundedCornerShape(50)).background(if (active) Cyan else Color(0xFF52617B)))
        Spacer(Modifier.width(14.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        if (active) Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Cyan)
        else Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
    }
}

@Composable
fun ActionTile(title: String, subtitle: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(18.dp))
            .background(if (active) Color(0xFF3B1717) else Panel)
            .border(1.dp, if (active) Cyan else Color(0xFF3A3A3A), RoundedCornerShape(18.dp))
            .clickable { onClick() }.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = if (active) Cyan else Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = Muted, fontSize = 10.sp)
    }
}
