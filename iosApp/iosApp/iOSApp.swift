import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        InitKoinIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
