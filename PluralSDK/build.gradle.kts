import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.pinelabs.pluralsdk"
    compileSdk = 34

    lint {
        baseline = file("lint-baseline.xml")
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-rules.pro")

        buildConfigField(
            "String",
            "SHA256_UAT",
            "\"c2hhMjU2LzRnZU5TQkpuem9BYVc2K3puR2x3YmhYZWdSS1Q0c0s2bEdUZ0w2YmVZQmM9\""
        )
        buildConfigField("String", "SHA256_QA", "\"c2hhMjU2LzVwNjZBekxRU0kzdjdUd2RBeGVuQUswY0dU\"")
        buildConfigField(
            "String",
            "SHA256_PROD",
            "\"c2hhMjU2L2dmVUJRQzB1WWNmQ2k3d21CdWllcnZjNWlNWGZnSXE3U2JQcVNyeU1LZDA9\""
        )

    }

    fun Packaging.() {
        resources.excludes.add("proguard-rules.pro")
        resources.excludes.add("proguard.txt")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.logging.interceptor)

    implementation(libs.lottie)

    implementation(libs.shimmer)

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.extension)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    implementation(libs.flexbox)
    implementation(libs.clevertap)

    implementation(libs.coil)
    implementation(libs.coil.svg)

    implementation(libs.play.service.phone)

    //implementation(files("libs/flexbox-3.0.0.aar"))
    /*implementation(files("libs/retrofit-2.9.0.jar"))
    implementation(files("libs/converter-gson-2.9.0.jar"))
    implementation(files("libs/logging-interceptor-4.10.0.jar"))*/
}

