require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = package["name"]
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = "https://www.npmjs.com/package/onegini-react-native-sdk"

  s.license      = package['license']
  s.authors      = { "Develocraft" => "valeriy.rumak@develocraft.com" }
  s.platforms    = { :ios => "13.0" }
  s.source       = { :git => "git@gitlab.com:develocraft/onegini-react-native-sdk.git", :tag => "v#{s.version}" }

  s.swift_version   = "5.0"
  s.source_files    = "ios/**/*.{h,c,m,swift}"
  s.requires_arc    = true

  s.dependency "React"
  s.dependency "OneginiSDKiOS", "~> 11.0.0"
end

