import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "feelings.guide"
        minSdkVersion(15)
        targetSdkVersion(28)
        versionCode = 3
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    lintOptions {
        disable("MissingTranslation")
    }
    productFlavors {
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api("androidx.core:core-ktx:1.2.0-alpha01")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("androidx.appcompat:appcompat:1.1.0-alpha05")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta1")
    implementation("androidx.vectordrawable:vectordrawable:1.0.1")
    implementation("com.google.android.material:material:1.1.0-alpha07")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")

    testImplementation("androidx.test.ext:junit:1.1.1")
    testImplementation("com.google.truth:truth:0.44")
    testImplementation("org.threeten:threetenbp:1.3.8") {
        exclude(group = "com.jakewharton.threetenabp", module = "threetenabp")
    }

    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("com.google.truth:truth:0.44") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0-alpha04", {
        exclude(group = "com.android.support", module = "support-annotations")
    })

}
