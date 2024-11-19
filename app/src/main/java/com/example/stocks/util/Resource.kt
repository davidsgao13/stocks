package com.example.stocks.util


/**
 * This class is used to distinguish between success and error cases for our network requests,
 * with T as a generic that is being retrieved. It creates multiple subclasses inside the sealed
 * class Resource.
 *
 * Sealed classes can only be inherited by a fixed set of related subclasses.
 * All subclasses inheriting from the sealed class Resource must be written in the same file
 * as the sealed class. Note that sealed classes provide compile-time exhaustiveness (i.e. for
 * when clauses, we don't need to specify an else statement since we have all the potential
 * result values as subclasses of Resource). Sealed classes cannot be instantiated directly.
 */

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}