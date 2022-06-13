package com.onegini.mobile.sdk.reactnative.network.client;


import com.onegini.mobile.sdk.reactnative.model.ApplicationDetails;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface AnonymousClient {

  @GET("application-details")
  Single<ApplicationDetails> getApplicationDetails();
}
