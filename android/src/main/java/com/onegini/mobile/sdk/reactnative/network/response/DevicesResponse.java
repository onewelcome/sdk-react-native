package com.onegini.mobile.sdk.reactnative.network.response;

import com.google.gson.annotations.SerializedName;
import com.onegini.mobile.sdk.reactnative.model.Device;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DevicesResponse {

  @SerializedName("devices")
  final private List<Device> devices = new ArrayList<>();

  public List<Device> getDevices() {
    return devices;
  }
}