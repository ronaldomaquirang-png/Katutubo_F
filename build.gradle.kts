plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

tasks.register("signingReport") {
    group = "help"
    description = "Displays the signing info for the app module"
    dependsOn(":app:signingReport")
}

tasks.register("sha-1") {
    group = "help"
    description = "Alias for signingReport to get SHA-1 fingerprint"
    dependsOn(":app:signingReport")
}

tasks.register("signin") {
    group = "help"
    description = "Alias for signingReport"
    dependsOn(":app:signingReport")
}

tasks.register("Report") {
    group = "help"
    description = "Alias for signingReport"
    dependsOn(":app:signingReport")
}
