import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(800.dp, 900.dp)),
        title = "Asteroids for Desktop"
    ) {
        val game = remember { Game() }
        val density = LocalDensity.current
        LaunchedEffect(Unit) {
            while (true) {
                withFrameNanos {
                    game.update(it)
                }
            }
        }

        Column(modifier = Modifier.background(Color(51, 153, 255)).fillMaxHeight()) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button({
                    game.startGame()
                }) {
                    Text("Play")
                }
                Text(
                    game.gameStatus,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp),
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .aspectRatio(1.0f)
                    .background(Color(0, 0, 30))
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clipToBounds()
                    .onPointerEvent(PointerEventType.Move) {
                        val offset = it.changes.first().position
                        with(density) {
                            game.targetLocation = offset
                        }
                    }
                    // compare .clickable / .pointerInput
                    .pointerInput(Unit) {
                        detectTapGestures {
                            game.ship.fire(game, it)
                        }
                    }
                    .onSizeChanged {
                        with(density) {
                            game.width = it.width.toDp()
                            game.height = it.height.toDp()
                        }
                    }) {
                    game.gameObjects.forEach {
                        when (it) {
                            is ShipData -> Ship(it)
                            is BulletData -> Bullet(it)
                            is AsteroidData -> Asteroid(it)
                        }
                    }
                }
            }
        }
    }
}