// WITH_RUNTIME
// WITH_COROUTINES
import helpers.*
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

class Controller {
    suspend fun suspendHere(): String = suspendCoroutineOrReturn { x ->
        x.resume("K")
        COROUTINE_SUSPENDED
    }
}

fun builder(c: suspend Controller.() -> Unit) {
    c.startCoroutine(Controller(), EmptyContinuation)
}

val logger = StringBuilder()

class A(val first: String, val second: String) {
    init {
        logger.append("A.<init>;")
    }

    override fun toString() = "$first$second"

    companion object {
        init {
            logger.append("A.<clinit>;")
        }
    }
}

inline fun <T> logged(message: String, result: () -> T): T {
    logger.append(message)
    return result()
}

fun box(): String {
    var result = "OK"

    builder {
        var local: Any = A(logged("args;") { "O" }, suspendHere())

        if (local.toString() != "OK") {
            result = "fail 1: $local"
            return@builder
        }
    }

    if (logger.toString() != "A.<clinit>;args;A.<init>;") {
        return "Fail: '$logger'"
    }

    return result
}