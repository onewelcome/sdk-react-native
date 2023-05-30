extension UserProfile {
    var toList: [String: String] {
        return ["id": self.profileId]
    }
}

extension CustomInfo {
    var toList: [String: Any] {
        ["status": self.status, "data": self.data]
    }
}
