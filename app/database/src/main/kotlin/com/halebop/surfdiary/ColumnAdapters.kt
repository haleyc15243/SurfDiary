package com.halebop.surfdiary

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

val kotlinxInstantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String) = Instant.parse(databaseValue)
    override fun encode(value: Instant) = value.toString()
}

inline fun <reified T : Any, reified U : Any> inlineValue(
    crossinline toDb: (T) -> U,
    crossinline fromDb: (U) -> T
): ColumnAdapter<T, U> {
    return object : ColumnAdapter<T, U> {
        override fun decode(databaseValue: U) = fromDb(databaseValue)
        override fun encode(value: T) = toDb(value)
    }
}