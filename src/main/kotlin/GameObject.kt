import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.openrndr.math.Vector2
import org.openrndr.math.mod

class ShipData : GameObject() {
    override var size: Double = 40.0

    fun fire(game: Game) {
        val ship = this
        game.gameObjects.add(BulletData(ship.speed * 4.0, ship.angle, ship.position))
    }
}

class Asteroid(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) :
    GameObject(speed, angle, position) {
    override var size: Double = 120.0
}

class BulletData(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) :
    GameObject(speed, angle, position) {
    override val size: Double = 4.0
}

sealed class GameObject(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) {
    var speed by mutableStateOf(speed)
    var angle by mutableStateOf(angle)
    var position by mutableStateOf(position)
    abstract val size: Double // Diameter

    fun update(realDelta: Float, game: Game) {
        val gmobj = this
        val velocity = Vector2(gmobj.speed * realDelta, 0.0).rotate(gmobj.angle)

        gmobj.position += velocity

        gmobj.position = gmobj.position.mod(Vector2(game.width.value.toDouble(), game.height.value.toDouble()))
    }

    fun overlapsWith(other: GameObject): Boolean {
        // Overlap means the the center of the game objects are closer together than the sum of their radiuses
        return this.position.distanceTo(other.position) < (this.size / 2 + other.size / 2)
    }
}
