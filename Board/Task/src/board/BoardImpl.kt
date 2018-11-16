package board

import board.Direction.*
import java.lang.IllegalArgumentException


private data class CellImpl(override val i: Int, override val j: Int) : Cell

fun Direction.asVector(): Pair<Int, Int> = when (this) {
    UP -> Pair(0, -1)
    DOWN -> Pair(0, 1)
    RIGHT -> Pair(1, 0)
    LEFT -> Pair(-1, 0)
}

fun createSquareBoard(width: Int): SquareBoard =
        object : SquareBoard {
            override val width: Int
                get() = width


            private val cells = Array(width) { i ->
                Array<Cell>(width) { j ->
                    CellImpl(i + 1, j + 1)
                }
            }

            private fun checkBounds(i: Int, j: Int): Boolean =
                    i in 1..width && j in 1..width

            override fun getCellOrNull(i: Int, j: Int): Cell? {
                return if (checkBounds(i, j))
                    cells[i - 1][j - 1]
                else
                    null
            }

            override fun getCell(i: Int, j: Int): Cell {
                if (checkBounds(i, j))
                    return cells[i - 1][j - 1]
                throw IllegalArgumentException("No cell with coordinates i=$i, j=$j")
            }

            override fun getAllCells(): Collection<Cell> {
                return cells.flatten()
            }

            override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
                return jRange.mapNotNull { j -> getCellOrNull(i, j) }
            }

            override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
                return iRange.mapNotNull { i -> getCellOrNull(i, j) }
            }

            override fun Cell.getNeighbour(direction: Direction): Cell? {
                val (xs, ys) = direction.asVector()
                return getCellOrNull(this.i + ys, this.j + xs)
            }

        }

fun <T> createGameBoard(width: Int): GameBoard<T> {
    return object : GameBoard<T>, SquareBoard by createSquareBoard(width) {

        private val cellMap = getAllCells().map { Pair<Cell, T?>(it, null) }.toMap().toMutableMap()

        override fun get(cell: Cell): T? {
            return cellMap[cell]
        }

        override fun set(cell: Cell, value: T?) {
            cellMap[cell] = value
        }

        override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
            return cellMap.filterValues { predicate(it) }.keys
        }

        override fun find(predicate: (T?) -> Boolean): Cell? {
            return cellMap.filterValues { predicate(it) }.keys.firstOrNull()
        }

        override fun any(predicate: (T?) -> Boolean): Boolean {
            return cellMap.values.any(predicate)
        }

        override fun all(predicate: (T?) -> Boolean): Boolean {
            return cellMap.values.all(predicate)
        }

    }
}

