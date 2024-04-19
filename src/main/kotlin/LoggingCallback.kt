import com.github.dockerjava.api.async.ResultCallbackTemplate

/**
 * A callback to process the output of generic docker commands.
 *
 * @property T The type of the response.
 */
open class GenericCallback<T> : ResultCallbackTemplate<GenericCallback<T>, T>() {

    var error: Throwable? = null

    /**
     * Called whenever a message is received.
     */
    override fun onNext(response: T?) {
    }

    /**
     * Called whenever an error occurs.
     */
    override fun onError(throwable: Throwable?) {

        error = throwable

        onComplete()
    }
}
