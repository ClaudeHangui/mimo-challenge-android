package com.changui.mimochallengeandroid.domain

/**
 * Mapper interface which converts data model (input I) to UI model (output O)
 * @param <I>
 * @param <O>
 */

interface Mapper<I, O> {
    fun mapToUI(input: I): O
}