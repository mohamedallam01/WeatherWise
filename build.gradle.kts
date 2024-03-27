// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0-beta02" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

buildscript{
    dependencies{
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}

//object TestingDependencies {
//    const val androidXVersion = "1.0.0"
//    const val androidXTestCoreVersion = "1.4.0"
//    const val androidXTestExtKotlinRunnerVersion = "1.1.3"
//    const val androidXTestRulesVersion = "1.2.0"
//    const val androidXAnnotations = "1.3.0"
//    const val appCompatVersion = "1.4.0"
//    const val archLifecycleVersion = "2.4.0"
//    const val archTestingVersion = "2.1.0"
//    const val coroutinesVersion = "1.5.2"
//    const val cardVersion = "1.0.0"
//    const val dexMakerVersion = "2.12.1"
//    const val espressoVersion = "3.4.0"
//    const val fragmentKtxVersion = "1.4.0"
//    const val hamcrestVersion = "1.3"
//    const val junitVersion = "4.13.2"
//    const val materialVersion = "1.4.0"
//    const val recyclerViewVersion = "1.2.1"
//    const val robolectricVersion = "4.5.1"
//    const val rulesVersion = "1.0.1"
//    const val swipeRefreshLayoutVersion = "1.1.0"
//    const val timberVersion = "4.7.1"
//    const val truthVersion = "1.1.2"
//}
