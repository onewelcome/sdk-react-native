func mapCustomInfo(_ info: CustomInfo?) -> [String: Any]? {
    return info.map({ ["status": $0.status, "data": $0.data] })
}

func mapUserProfile(_ profile: UserProfile?) -> [String: String]? {
    return profile.map({ ["id": $0.profileId] })
}

func mapToAuthData(profile: UserProfile?, info: CustomInfo?) -> [String: Any?] {
    return ["userProfile": mapUserProfile(profile), "customInfo": mapCustomInfo(info)]
}
