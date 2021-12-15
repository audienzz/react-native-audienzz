require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-audienzz"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/audienzzag/react-native-audienzz.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm}"

  s.dependency "React-Core"
  s.dependency "AppNexusSDK", "7.17.0"

  s.prepare_command = <<-CMD
    sed -i'.original' -e 's/mraid.util.nativeCall(\\"mraid:\\/\\/audioVolumeChange\\/\\");/\\/\\/ mraid.util.nativeCall(\\"mraid:\\/\\/audioVolumeChange\\/\\");/g' ../../ios/Pods/AppNexusSDK/sdk/sourcefiles/Resources/ANMRAID.bundle/mraid.js
  CMD
end
