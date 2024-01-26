package firebase

interface onCompletion<T> {
    fun onSuccess(T : T)
    fun onError(e : Exception)
}