package com.example.stocks.data.csv

import java.io.InputStream


/**
 * Creating this abstraction to be used by a class implementation (CompanyListingsParser).
 *
 * It parses a stream and returns a list of whatever a generic type. Because a generic is not
 * a concrete or predefined type, we need to write <T> in order to introduce the generic to the
 * interface as a new generic type parameter that the caller can replace with any T. The compiler
 * doesn't know what T is unless we explicitly declare it. It's basically stating:
 * "This function will work with a generic type T, and I will define how to use it." This pattern
 * is only true for functions working with generics. You can either choose to define <T> after
 * the interface declaration, or prior to the function declaration
 */

interface CSVParser<T> {

    suspend fun parse(stream: InputStream) : List<T>
}