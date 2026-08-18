plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
    id("com.google.devtools.ksp") version "2.3.7"

    id("com.android.application")
}

android {
    compileSdk { version = release(37) }

    defaultConfig {
        applicationId = "com.home.reader"
        minSdk { version = release(34) }
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE.md",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE.md",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }

    namespace = "com.home.reader"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }

    sourceSets {
        getByName("main") {
            java.directories.addAll(setOf("src/main/kotlin", "src/main/java"))
        }
    }

}

dependencies {
    val lifecycleVersion = "2.11.0"
    val jwtVersion = "0.13.0"
    val roomVersion = "2.8.4"
    val composeVersion = "1.12.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.github.junrar:junrar:8.1.0")

    implementation("androidx.work:work-runtime-ktx:2.11.2")

    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")

    implementation("org.apache.commons:commons-text:1.15.0")

    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jwtVersion")

    ksp("androidx.room:room-compiler:$roomVersion")

    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")

    implementation("com.google.mlkit:text-recognition:16.0.1")

    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:1.7.8")

    //Paging
    implementation("androidx.paging:paging-compose:3.5.1")
    implementation("androidx.paging:paging-runtime-ktx:3.5.1")

    //Image loader
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    //For REST api
    implementation("com.squareup.okhttp3:okhttp:5.4.0")
    implementation("com.google.code.gson:gson:2.14.0")

    implementation(kotlin("stdlib"))
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("com.google.android.material:material:1.14.0")

    implementation("org.slf4j:slf4j-simple:2.0.18")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

}