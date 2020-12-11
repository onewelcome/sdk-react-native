/*
 * Copyright (c) 2016-2018 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onegini.mobile.network

import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.model.ImplicitUserDetails
import com.onegini.mobile.network.client.ImplicitUserClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ImplicitUserService private constructor() {
    private val applicationDetailsRetrofitClient: ImplicitUserClient = OneginiComponets.secureResourceClient.prepareSecuredImplicitUserRetrofitClient(ImplicitUserClient::class.java)
    val implicitUserDetails: Single<ImplicitUserDetails>
        get() = applicationDetailsRetrofitClient.implicitUserDetails
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    companion object {
        private var INSTANCE: ImplicitUserService? = null
        val instance: ImplicitUserService
            get() {
                if (INSTANCE == null) {
                    INSTANCE = ImplicitUserService()
                }
                return INSTANCE!!
            }
    }

}