class SdkError {
    var title: String
    var errorDescription: String
    var recoverySuggestion: String

    init(title: String = "Error", errorDescription: String, recoverySuggestion: String = "Please try again.") {
        self.title = title
        self.errorDescription = errorDescription
        self.recoverySuggestion = recoverySuggestion
    }
}
