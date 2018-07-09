package pl.socketbyte.thousand.shared.terminal

import com.sun.jmx.remote.util.EnvHelp.getCause
import java.util.concurrent.*

/**
 * Very simple timed input class, allowing to make non-blocking input gathering
 * It timeouts after some time and returns empty input
 * If user enters a valid input before time expires, then the input is returned in [readLine]
 */
class TimedInput(private val timeout: Long, private val unit: TimeUnit) {

    fun readLine(): String {
        val ex = Executors.newSingleThreadExecutor()
        try {
            val result = ex.submit(TimedInputTask())
            try {
                return result.get(timeout, unit)
            } catch (e: ExecutionException) {
                e.cause?.printStackTrace()
            } catch (e: TimeoutException) {
                result.cancel(true)
            }
        } finally {
            ex.shutdownNow()
        }
        return ""
    }
}