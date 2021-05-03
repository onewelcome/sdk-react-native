package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.use_cases.GetRedirectUriUseCase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetRedirectUriUseCaseTests : BaseTests() {

    @Test
    fun `returns proper url`() {
        `when`(oneginiClient.configModel).thenReturn(TestData.configModel)

        GetRedirectUriUseCase()(promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals(TestData.configModel.redirectUri, firstValue.getString("redirectUri"))
        }
    }
}
