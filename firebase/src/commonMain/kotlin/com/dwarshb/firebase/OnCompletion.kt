package com.dwarshb.firebase


interface onCompletion<T> {
    fun onSuccess(t : T)
    fun onError(e : Exception)
}