import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import dependencies.Dependencies
import dependencies.TestDependencies
import extensions.applyRequiredInjectDeps

plugins {
    id(Plugins.android_library)
    kotlin(Plugins.android_kotlin)
    kotlin(Plugins.kapt)
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33

        consumerProguardFiles("consumer-rules.pro")

        val properties = gradleLocalProperties(rootDir)
        buildConfigField("String", "USERNAME", properties.getProperty("USERNAME", ""))
        buildConfigField("String", "TOKEN", properties.getProperty("TOKEN", ""))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(mapOf("path" to ":core")))

    implementation(Dependencies.hilt_android)
    kapt(Dependencies.hilt_compiler)

    applyRequiredInjectDeps()
    kapt(Dependencies.room_compiler)
    kapt(Dependencies.moshi_code_gen)

    testImplementation(TestDependencies.junit)
    testImplementation(TestDependencies.test_ext)
    testImplementation(TestDependencies.mockito_kotlin)
}