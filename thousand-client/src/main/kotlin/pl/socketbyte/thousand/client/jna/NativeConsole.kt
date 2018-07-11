package pl.socketbyte.thousand.client.jna

import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
import com.sun.jna.Pointer
import com.sun.jna.win32.StdCallLibrary
import pl.socketbyte.thousand.shared.terminal.OutputThread

/**
 * JNA utility class to allow sending messages to console
 * using native kernel write method
 *
 * It is because of bad unicode support of [System.out] console output
 * This allows to send unicode characters to the output
 */
object NativeConsole {
    private val kernel: Kernel32?

    interface Kernel32 : StdCallLibrary {
        fun WriteConsoleW(hConsoleOutput: Pointer, lpBuffer: CharArray,
                          nNumberOfCharsToWrite: Int,
                          lpNumberOfCharsWritten: IntByReference, lpReserved: Pointer?): Boolean
        fun GetStdHandle(nStdHandle: Int): Pointer
    }

    /**
     * Check if user is using Windows OS, else don't initialize kernel
     */
    init {
        val os = System.getProperty("os.name").toLowerCase()
        kernel = if (os.startsWith("win")) {
            Native.loadLibrary("kernel32", Kernel32::class.java)
        }
        else null
    }

    fun writeNativeText(outputThread: OutputThread, message: String) {
        if (kernel == null) {
            // Linux detected, so no need to use JNA
            // It has built-in support for Java's Unicode characters
            // (if terminal supports it of course, but most Linux terminals do)
            println(message)
            return
        }

        val buffer = message.toCharArray()
        val lp = IntByReference()
        val handle = kernel.GetStdHandle(-11)

        kernel.WriteConsoleW(handle, buffer, buffer.size,
                lp, null)

        println()
    }
}