import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import org.openrndr.math.Vector2
import org.openrndr.math.mod
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class ShipData : GameObject() {
    override var size: Double = 40.0
    var visualAngle: Double = 0.0

    private val radius = size / 2

    fun fire(game: Game) {
        val ship = this
        game.gameObjects.add(
            BulletData(ship.speed * 4.0, ship.visualAngle, ship.position)
        )
    }

    fun fire(game: Game, mouseClickPosition: Offset) {
        val ship = this
        val xDiff = mouseClickPosition.x - ship.position.x
        val yDiff = mouseClickPosition.y - ship.position.y
        val radiansTheta = atan2(yDiff, xDiff)
        val positionInFrontOfShip = Vector2(
            x = ship.position.x + radius * cos(radiansTheta),
            y = ship.position.y + radius * sin(radiansTheta)
        )
        game.gameObjects.add(
            BulletData(
                speed = (ship.speed + .5) * 4.0,
                angle = ship.visualAngle,
                position = positionInFrontOfShip
            )
        )
    }
}

class AsteroidData(
    speed: Double = 0.0,
    angle: Double = 0.0,
    position: Vector2 = Vector2.ZERO
) : GameObject(speed, angle, position) {
    override var size: Double = 120.0
}

class BulletData(
    speed: Double = 0.0,
    angle: Double = 0.0,
    position: Vector2 = Vector2.ZERO
) : GameObject(speed, angle, position) {
    override val size: Double = 4.0
}

sealed class GameObject(
    speed: Double = 0.0,
    angle: Double = 0.0,
    position: Vector2 = Vector2.ZERO
) {
    var speed by mutableStateOf(speed)
    var angle by mutableStateOf(angle)
    var position by mutableStateOf(position)
    var movementVector
        get() = (Vector2.UNIT_X * speed).rotate(angle)
        set(value) {
            speed = value.length
            angle = value.angle()
        }
    abstract val size: Double // Diameter

    fun update(realDelta: Float, game: Game) {
        val obj = this
        val velocity = movementVector * realDelta.toDouble()
        obj.position += velocity
        obj.position = obj.position.mod(
            Vector2(game.width.value.toDouble(), game.height.value.toDouble())
        )
    }

    // Overlap means the center of the game objects are closer together than the sum of their radiuses
    fun overlapsWith(other: GameObject) =
        this.position.distanceTo(other.position) < (this.size / 2 + other.size / 2)

}

