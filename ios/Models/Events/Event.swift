import Foundation

protocol Event {
    var name: String { get }
    var data: [String: Any]? { get }
}
