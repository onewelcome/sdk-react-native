package com.onegini.mobile.sdk.reactnative.network.client;


import com.google.gson.JsonObject;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

import java.util.Map;

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
