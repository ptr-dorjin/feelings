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
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    api("androidx.core:core-ktx:1.2.0-alpha02")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //Kotlin standard library
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.preference:preference-ktx:1.1.0-rc01")
    implementation("androidx.appcompat:appcompat:1.1.0-rc01")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta2")
    implementation("androidx.vectordrawable:vectordrawable:1.0.1")
    implementation("androidx.navigation:navigation-runtime-ktx:2.1.0-beta02")
    implementation("androidx.navigation:navigation-fragment-ktx:2.1.0-beta02")
    implementation("androidx.navigation:navigation-ui-ktx:2.1.0-beta02")
    implementation("com.google.android.material:material:1.1.0-alpha09")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0") //needed due to minSdkVersion=15

    testImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    testImplementation(kotlin("test-junit", KotlinCompilerVersion.VERSION))
    testImplementation("androidx.test.ext:junit:1.1.1")
    testImplementation("com.google.truth:truth:0.44") //todo replace?

    androidTestImplementation(kotlin("test", KotlinCompilerVersion.VERSION))
    androidTestImplementation(kotlin("test-junit", KotlinCompilerVersion.VERSION))
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.1")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("com.google.truth:truth:0.44") { //todo replace?
        exclude("com.google.guava", "listenablefuture")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0-alpha02")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0-alpha02")
    androidTestImplementation("org.hamcrest:hamcrest:2.1")
    androidTestImplementation("org.hamcrest:hamcrest-library:2.1")
}
