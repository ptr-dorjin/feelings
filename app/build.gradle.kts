plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "feelings.guide"
    compileSdk = 34

    defaultConfig {
        applicationId = "feelings.guide"
        minSdk = 26
        targetSdk = 34
        versionCode = 10
        versionName = "2.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    productFlavors {
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
    sourceSets["test"].java.srcDir("src/test/kotlin")
    sourceSets["androidTest"].java.srcDir("src/androidTest/kotlin")

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    api(libs.androidx.core.ktx)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //Kotlin standard library
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.card.view)
    implementation(libs.androidx.recycler.view)
    implementation(libs.androidx.constraint.layout)
    implementation(libs.androidx.vector.drawable)
    implementation(libs.androidx.nav.runtime.ktx)
    implementation(libs.androidx.nav.fragment.ktx)
    implementation(libs.androidx.nav.ui.ktx)
    implementation(libs.android.material)
    implementation(libs.opencsv) {
        exclude("commons-logging")
    }
    // to workaround the 64K reference limit
    implementation(libs.androidx.multidex)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.truth) //todo replace?
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlin.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.truth) { //todo replace?
        exclude("com.google.guava", "listenablefuture")
    }
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.nav.testing)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.hamcrest.library)
}
