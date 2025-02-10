package io.github.junkfood.heal.util

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import java.io.Closeable
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class SafeCoroutineScopeImpl(
    val context: CoroutineContext,
    private val errorHandler: ((Throwable) -> Unit)? = null
) : CoroutineScope, Closeable {

    override val coroutineContext: CoroutineContext
        get() = context + UncaughtCoroutineExceptionHandler(errorHandler)

    override fun close() {
        context.cancelChildren()
    }
}

// Global coroutine exception handler
private class UncaughtCoroutineExceptionHandler(private val errorHandler: ((Throwable) -> Unit)? = null) :
    CoroutineExceptionHandler, AbstractCoroutineContextElement(
    CoroutineExceptionHandler.Key
) {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        exception.printStackTrace()

        errorHandler?.let { it(exception) }
    }
}


@Suppress("FunctionName")
fun SafeCoroutineScope(context: CoroutineContext, errorHandler: ((Throwable) -> Unit)? = null) =
    SafeCoroutineScopeImpl(context, errorHandler)

