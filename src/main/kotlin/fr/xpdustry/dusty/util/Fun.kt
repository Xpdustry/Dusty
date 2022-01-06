package fr.xpdustry.dusty.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.dv8tion.jda.api.entities.Message.MentionType
import java.io.Reader
import java.net.URL
import kotlin.random.Random


typealias Matrix = Array<IntArray>


object Fun {
    const val LENNY = "( ͡° ͜ʖ ͡°)"

    @JvmStatic
    fun minesweeper(x: Int, y: Int, n: Int): Matrix {
        val grid = Array(y) { IntArray(x) }

        if (n > x * y)
            throw IllegalArgumentException("The number of mines is higher than the number of available cells.")

        for (k in (1..n)) {
            var nx: Int
            var ny: Int

            do {
                nx = Random.nextInt(x)
                ny = Random.nextInt(y)
            } while (grid[ny][nx] < 0)

            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0)
                        grid[ny][nx] = -1
                    else {
                        val cx = nx + dx
                        val cy = ny + dy
                        if (cx in 0 until x && cy in 0 until y && grid[cy][cx] >= 0) grid[cy][cx]++
                    }
                }
            }
        }

        return grid
    }

    @JvmStatic
    fun containsMentions(text: String, vararg types: MentionType): Boolean {
        return types.any { it.pattern.matcher(text).matches() }
    }
}

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(reader: Reader): T =
    fromJson(reader, object : TypeToken<T>() {}.type)

fun URL.get(): String =
    openStream().bufferedReader().use { readText() }


