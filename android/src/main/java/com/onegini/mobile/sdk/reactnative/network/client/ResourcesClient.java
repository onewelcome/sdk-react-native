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

package com.onegini.mobile.sdk.reactnative.network.client;



import com.google.gson.JsonObject;


import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface ResourcesClient {

  @GET
  Single<JsonObject> getResourcesDetails(@Url String url, @HeaderMap Map<String, String> headers);

  @POST
  Single<JsonObject> postResourcesDetails(@Url String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);

  @PUT
  Single<JsonObject> putResourcesDetails(@Url String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);

  @DELETE
  Single<JsonObject> deleteResourcesDetails(@Url String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);
}
