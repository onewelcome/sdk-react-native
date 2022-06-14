package com.onegini.mobile.sdk.reactnative.network.client;

import com.onegini.mobile.sdk.reactnative.network.response.DevicesResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface UserClient {

  @GET("devices")
  Single<DevicesResponse> getDevices();
}
