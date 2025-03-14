plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.english_learning"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.english_learning"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core:1.12.0") // Giữ lại vì có thể cần thiết
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.adapters) // Giữ lại nếu bạn đang sử dụng
    implementation(libs.tools.core) // Giữ lại nếu bạn đang sử dụng
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Room Database (chỉ dành cho Java, không có kapt)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1") // Không cần kapt

    // Gson (dùng để xử lý JSON)
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.cardview:cardview:1.0.0")



    // Lifecycle Components (chỉ dành cho Java)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // Navigation Components (chỉ dành cho Java)
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
}