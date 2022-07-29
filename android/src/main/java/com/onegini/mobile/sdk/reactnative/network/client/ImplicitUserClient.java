package com.onegini.mobile.sdk.reactnative.network.client;


import com.onegini.mobile.sdk.reactnative.model.ImplicitUserDetails;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface ImplicitUserClient {
    @GET("user-id-decorated")
    Single<ImplicitUserDetails> getImplicitUserDetails();
}
