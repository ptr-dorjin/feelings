import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "feelings.guide"
        minSdkVersion(16)
        targetSdkVersion(29)
        versionCode = 5
        versionName = "2.1.1"
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
    sourceSets["test"].java.srcDir("src/test/kotlin")
    sourceSets["androidTest"].java.srcDir("src/androidTest/kotlin")

    packagingOptions {
        exclude("README.txt")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/atomicfu.kotlin_module")
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    api("androidx.core:core-ktx:1.5.0-alpha01")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // UI
    implementation("androidx.core:core-ktx:1.5.0-alpha01")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.appcompat:appcompat:1.3.0-alpha01")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha04")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta8")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.navigation:navigation-runtime-ktx:2.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")

    //threetenabp is needed due to minSdkVersion=15. Can be removed after setting minSdkVersion=26
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")

    // Room components
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    implementation("androidx.room:room-ktx:2.2.5")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Kotlin components
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")

    // Material design
    implementation("com.google.android.material:material:1.1.0")

    // Testing
    testImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    testImplementation(kotlin("test-junit", KotlinCompilerVersion.VERSION))
    testImplementation("androidx.test.ext:junit-ktx:1.1.2-rc01")
    testImplementation("com.google.truth:truth:0.44")

    androidTestImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    androidTestImplementation(kotlin("test-junit", KotlinCompilerVersion.VERSION))
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2-rc01")
    androidTestImplementation("androidx.test:rules:1.3.0-rc01")
    androidTestImplementation("com.google.truth:truth:0.44") {
        exclude("com.google.guava", "listenablefuture")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0-rc01")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0-rc01")
    androidTestImplementation("org.hamcrest:hamcrest:2.1")
    androidTestImplementation("org.hamcrest:hamcrest-library:2.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.room:room-testing:2.2.5")
}
