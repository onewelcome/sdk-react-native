require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "local-react-native-sdk-beta"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = "https://www.npmjs.com/package/react-native-sdk-beta"

  s.license      = package['license']
  s.authors      = { "Develocraft" => "valeriy.rumak@develocraft.com" }
  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "file://" + __dir__ }

  s.swift_version   = "5.0"
  s.source_files    = "ios/**/*.{h,c,m,swift}"
  s.requires_arc    = true

  s.dependency "React"
  s.dependency "OneginiSDKiOS", "9.0.0"
end

