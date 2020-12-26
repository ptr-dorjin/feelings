buildscript {
    val kotlinVersion = "1.4.0"
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath(kotlin("gradle-plugin", kotlinVersion))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks {
    @Suppress("UNUSED_VARIABLE")
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}
