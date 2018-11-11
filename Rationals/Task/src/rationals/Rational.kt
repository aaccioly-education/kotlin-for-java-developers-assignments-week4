package rationals

import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

class Rational(n: BigInteger, d: BigInteger = ONE) : Comparable<Rational> {

    companion object {

        private fun lcm(a: BigInteger, b: BigInteger) = a * b / a.gcd(b)

        private fun normalize(n: BigInteger, d: BigInteger = ONE): Pair<BigInteger, BigInteger> {

            fun normalizeSign(): Pair<BigInteger, BigInteger> {
                return if (d < ZERO) {
                    Pair(-n, -d)
                } else {
                    Pair(n, d)
                }
            }

            val (num, dem) = normalizeSign()
            val gcd = num.gcd(dem)

            return Pair(num / gcd, dem / gcd)
        }
    }

    val numerator: BigInteger
    val denominator: BigInteger

    init {
        if (d == ZERO)
            throw IllegalArgumentException("denominator can't be zero")

        val (num, dem) = normalize(n, d)
        numerator = num
        denominator = dem
    }

    operator fun unaryMinus() = Rational(-numerator, denominator)

    operator fun plus(other: Rational): Rational {
        return if (denominator == other.denominator)
            Rational(numerator + other.numerator, denominator)
        else {
            val lcm = lcm(denominator, other.denominator)
            Rational(numerator * (lcm / denominator) + other.numerator * (lcm / other.denominator), lcm)
        }
    }

    operator fun minus(other: Rational) = this + (-other)

    operator fun times(other: Rational) = Rational(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Rational) = this * other.inv()

    override fun compareTo(other: Rational) = (this - other).numerator.compareTo(ZERO)

    override fun toString(): String {
        return if (denominator == ONE)
            numerator.toString()
        else
            "$numerator/$denominator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }

    private fun inv() = Rational(denominator, numerator)
}

fun String.toRational(): Rational {
    val components = split('/')
    return if (components.size == 2) {
        Rational(components[0].toBigInteger(), components[1].toBigInteger())
    } else {
        Rational(toBigInteger())
    }
}

infix fun Int.divBy(n: Int) = Rational(toBigInteger(), n.toBigInteger())

infix fun Long.divBy(n: Long) = Rational(toBigInteger(), n.toBigInteger())

infix fun BigInteger.divBy(n: BigInteger) = Rational(this, n)

fun main(args: Array<String>) {
    val r1 = 1 divBy 2
    val r2 = 2000000000L divBy 4000000000L
    println(r1 == r2)

    println((2 divBy 1).toString() == "2")

    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    println("1/2".toRational() - "1/3".toRational() == "1/6".toRational())
    println("1/2".toRational() + "1/3".toRational() == "5/6".toRational())

    println(-(1 divBy 2) == (-1 divBy 2))

    println((1 divBy 2) * (1 divBy 3) == "1/6".toRational())
    println((1 divBy 2) / (1 divBy 4) == "2".toRational())

    println((1 divBy 2) < (2 divBy 3))
    println((1 divBy 2) in (1 divBy 3)..(2 divBy 3))

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}