import Foundation

protocol Event {
    var name: String { get }
    var data: [String: Any]? { get }
}

struct GenericEvent: Event {
    var name: String
    var data: [String: Any]?
}
