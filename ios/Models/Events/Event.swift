import Foundation

protocol EventNameIterable {
    static var allNames: [String] { get }
}

protocol Event: EventNameIterable {
    var name: String { get }
    var data: [String: Any]? { get }
}

extension Array where Element == Event {
    var allNames: [String] {
        self.map({$0.name})
    }
}
