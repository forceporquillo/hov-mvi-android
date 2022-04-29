package dev.forcecodes.hov.core

interface EntityMapper<in T, in D, R> {
    operator fun invoke(data: T, map: D): R
}