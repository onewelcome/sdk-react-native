//
//  DefaultKeys.swift
//  Pods
//
//  Created by Eelco Eikelboom on 13/04/2022.
//

import Foundation

enum DefaultKeys {
    static let PinLengthKey = "profilePinLength"
}

struct ProfilePinConfig: Codable{
    let profileId: String
    var pinLength: UInt
}

class DefaultKeysUtil {
    
    static func setPinLength(profileId: String, pinLength: UInt){
        var allConfigs = getAllPinConfigs()
        var existingConfig = allConfigs.first(where: { $0.profileId == profileId })
        
        if(existingConfig == nil) {
            let pinConfig = ProfilePinConfig(profileId: profileId, pinLength: pinLength)
            allConfigs.append(pinConfig)
        } else {
            existingConfig?.pinLength = pinLength
        }
        
        do {
            let encoder = JSONEncoder()
            let data = try encoder.encode(allConfigs)
            
            UserDefaults.standard.set(data, forKey: DefaultKeys.PinLengthKey)
        } catch {
            //TODO do something with error
            print("Error")
        }
    }
    
    static func getAllPinConfigs() -> [ProfilePinConfig] {
        if let data = UserDefaults.standard.data(forKey: DefaultKeys.PinLengthKey) {
            do {
                let decoder = JSONDecoder()
                return try decoder.decode([ProfilePinConfig].self, from: data)
            } catch {
                return []
            }
        }
        return []
    }
    
    static func getPinConfig(profileId: String) -> ProfilePinConfig? {
        let allConfigs = getAllPinConfigs()
        return allConfigs.first(where: {$0.profileId == profileId})
    }
}
