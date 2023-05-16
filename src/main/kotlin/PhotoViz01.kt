import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.openrndr.application
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.extra.camera.Camera2D
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.bounds
import org.openrndr.shape.map
import java.io.File

fun main() {
    application {
        configure {
            width = 720
            height = 720
        }
        program {
            val images = mutableListOf<ColorBuffer>()
            val tsne = mutableListOf<Vector2>()
            csvReader().readAllWithHeader(File("data/csv/image-tsne.csv")).forEach {
                val filename = it.getValue("filename")
                val x = it.getValue("0").toDouble()
                val y = it.getValue("1").toDouble()
                tsne.add(Vector2(x, y))
                images.add(loadImage("data/images/${filename}"))
            }
            val bounds = tsne.bounds
            val mapped = tsne.map { it.map(bounds, drawer.bounds) }
            extend(Camera2D())
            extend {
                drawer.circles(mapped, 10.0)
                for (i in 0 until images.size) {
                    val position = mapped[i]
                    val image = images[i]
                    val target = Rectangle.fromCenter(position, image.width*0.1, image.height*0.1)
                    drawer.image(image, image.bounds, target)
                }
            }
        }
    }
}