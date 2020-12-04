@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import kotlin.math.*

// Урок 8: простые классы
// Максимальное количество баллов = 40 (без очень трудных задач = 11)

fun main() {
    println(
        minContainingCircle(
            Point(-632.0, 0.07964169667639653),
            Point(0.7426127343124546, -632.0),
            Point(0.0, 0.23077394487950365),
            Point(0.3091219162588529, 2.220446049250313e-16),
            Point(0.8798469438471225, 0.7015378163440219),
            Point(-632.0, 0.32011590124267175),
            Point(0.8336176346946331, 0.8591058530996949),
            Point(0.0, 0.9285164919143433),
            Point(0.4675325143339947, -632.0),
            Point(0.1883696590293894, 0.0),
            Point(-5e-324, -2.220446049250313e-16)
        )
    )
}

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая (2 балла)
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double =
        if (center.distance(other.center) - radius - other.radius > 0) center.distance(other.center) - radius - other.radius
        else 0.0

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя (3 балла)
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment = TODO()

/**
 * Простая (2 балла)
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle = Circle(
    Point((diameter.end.x + diameter.begin.x) / 2, (diameter.end.y + diameter.begin.y) / 2),
    diameter.begin.distance(diameter.end) / 2
)

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя (3 балла)
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        if (angle == PI / 2) return Point(-b, -b * tan(other.angle) + other.b / cos(other.angle))
        if (other.angle == PI / 2) return Point(-other.b, -other.b * tan(angle) + b / cos(angle))
        val x = (b / cos(angle) - other.b / cos(other.angle)) / (tan(other.angle) - tan(angle))
        val y = x * tan(angle) + b / cos(angle)
        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line {
    var angle = atan((s.begin.y - s.end.y) / (s.begin.x - s.end.x))
    if (angle < 0) angle += PI
    if (angle == PI) angle = 0.0
    return Line(s.begin, angle)
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    var angle = atan((a.y - b.y) / (a.x - b.x))
    if (angle < 0) angle += PI
    if (angle == PI) angle = 0.0
    return Line(a, angle)
}

/**
 * Сложная (5 баллов)
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val middle = Point((a.x + b.x) / 2.0, (a.y + b.y) / 2.0)
    val line = lineByPoints(a, b)
    val angle = if (line.angle + PI / 2.0 >= PI) line.angle + PI / 2.0 - PI
    else line.angle + PI / 2.0
    return Line(middle, angle)
}

/**
 * Средняя (3 балла)
 *
 * Задан список из n окружностей на плоскости.
 * Найти пару наименее удалённых из них; расстояние между окружностями
 * рассчитывать так, как указано в Circle.distance.
 *
 * При наличии нескольких наименее удалённых пар,
 * вернуть первую из них по порядку в списке circles.
 *
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Сложная (5 баллов)
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    if (a == b) return circleByDiameter(Segment(a, b))
    if (a == c) return circleByDiameter(Segment(a, c))
    if (c == b) return circleByDiameter(Segment(c, b))
    val line1 = bisectorByPoints(a, b)
    val line2 = bisectorByPoints(b, c)
    val centralPoint = line1.crossPoint(line2)
    return Circle(centralPoint, centralPoint.distance(a))
}

/**
 * Очень сложная (10 баллов)
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle = TODO()/*{
    if (points.isEmpty()) throw java.lang.IllegalArgumentException()
    if (points.size == 1) return Circle(points[0], 0.0)
    var maxDistance = 0.0
    var diameter = Segment(points[0], points[1])
    for (i in 0..points.size - 2) {
        for (j in i + 1 until points.size) {
            if (points[i].distance(points[j]) > maxDistance) {
                maxDistance = points[i].distance(points[j])
                diameter = Segment(points[i], points[j])
            }
        }
    }
    val circle = circleByDiameter(diameter)
    val notContainingPoints = mutableSetOf<Point>()
    var minRadius = Double.POSITIVE_INFINITY
    var bestCircle = circle
    for (point in points)
        if (!circle.contains(point))
            notContainingPoints += point
    if (notContainingPoints.isEmpty()) return circle
    else {
        for (point in notContainingPoints) {
            val newCircle = circleByThreePoints(diameter.begin, diameter.end, point)
            for (checkingPoint in notContainingPoints)
                if (newCircle.contains(checkingPoint)) {
                    if (notContainingPoints.indexOf(checkingPoint) == notContainingPoints.size - 1)
                        if (newCircle.radius < minRadius) {
                            minRadius = newCircle.radius
                            bestCircle = newCircle
                        }
                } else break
        }
    }
    return bestCircle
}*/