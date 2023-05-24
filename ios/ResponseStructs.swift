// We store any structs here that we use to send information from native -> ReactNative that are complete objects contained in the native sdk
struct RegistrationResponse {
  var userProfile: UserProfile
  var customInfo: CustomInfo?
    var toList: [String: Any?] {
    return [
        "userProfile": userProfile.toList,
        "customInfo": customInfo?.toList
    ]
  }
}
