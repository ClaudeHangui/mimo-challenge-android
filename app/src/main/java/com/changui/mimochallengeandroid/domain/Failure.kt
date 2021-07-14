package com.changui.mimochallengeandroid.domain

sealed class Failure {
    object NetworkError : Failure()
    object ServerError : Failure()
    object BadRequestError: Failure()
    object GatewayError: Failure()
    object UnknownError : Failure()
}
