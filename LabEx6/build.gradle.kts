// Top-level build file: declares plugin versions once; the :app module applies them.
// AGP 9.4 + Gradle 9.7 match current Android Studio, which ships Java 25 (Gradle 8.x can't run on it).
// Bump AGP, Gradle and the Kotlin Compose plugin together, not one at a time.
plugins {
    id("com.android.application") version "9.4.0" apply false
    // Compose compiler plugin. AGP 9 compiles Kotlin itself ("built-in Kotlin"),
    // so there is no org.jetbrains.kotlin.android plugin anymore.
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}
