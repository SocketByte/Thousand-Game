package pl.socketbyte.thousand.shared

import java.io.IOException

/**
 * It currently works only under Windows machines :(
 */
fun clearScreen() {
    if (System.getProperty("os.name").contains("Windows"))
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
    else
        Runtime.getRuntime().exec("clear")
}