package com.onegini.mobile.mapers

import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter

object AuthenticationAttemptCounterMapper{

    fun toErrorString(attempt: AuthenticationAttemptCounter):String{
        return "Error. failedAttempts: ${attempt.failedAttempts} remainingAttempts: ${attempt.remainingAttempts} maxAttempts: ${attempt.maxAttempts}"
    }
}