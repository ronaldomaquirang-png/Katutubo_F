// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.2.1" apply false
    id("com.android.library") version "9.2.1" apply false // Match this version too!
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Custom tasks to easily get your SHA-1 fingerprint for Firebase
tasks.register("signingReport") {
    group = "help"
    description = "Displays the signing info for the app module"
    dependsOn(":app:signingReport")
}

// Aliases (Shortcuts) for the signingReport task
tasks.register("sha-1") { dependsOn("signingReport") }
tasks.register("signin") { dependsOn("signingReport") }
tasks.register("Report") { dependsOn("signingReport") }
