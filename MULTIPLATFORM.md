# Kotlin/Native in multiplatform projects

*TODO: Some introduction*

## Creating multiplatform Android/iOS application with Kotlin

*TODO: High-level application structure: what code is common (data, logic) and what code is platform (io, ui, network).
What languages are used in what parts of the project.*

*TODO: link to the calculator project*

*TODO: link to the multiplatform description*

### 1. Preparing a workspace

Our multiplatform application will include three parts:

 * An **Android application** represented by a separate Android Studio project.
 * An **iOS application** represented by a separate XCode project.
 * A **multiplatform library** represented by a separate Gradle-build and containing a business logic of the application.
   This library can contain both platform-dependent and platform-independent code and is compiled into a `jar`-library
   for Android and in a framework for iOS.

In its turn, the multiplatform library will include three subprojects:

 * `common` - contains a common logic for both applications;
 * `ios` - contains an iOS-specific code;
 * `android` - contains an Android-specific code.

Represent this structure as a directory tree. Assume that our multiplatform library is intended to generate different
greetings on different platform. Create the following directory structure:

    application/
    ├── androidApp/
    ├── iosApp/
    └── greeting/
        ├── common/
        ├── android/
        └── ios/

Now we have a basic structure of the multiplatform application and can proceed to implementing of the multiplatform library.

### 2. Multiplatform library

*Note: It's highly recommended to use [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
to run the build. To create the wrapper, just execute `gradle wrapper` in the `greeting` directory. After that you can
use `./gradlew` to run the build instead of using your local Gradle installation.*

The multiplatform library is build using Gradle so the first thing we need to do is to specify the structure of the
project. To do this, create `settings.gradle` and declare in it all the subprojects:

    include ':common'
    include ':android'
    include ':ios'

Now Gradle knows how our library is organized and can work with it. Let's write some code and build logic.
Create `build.gradle` in the `greeting` directory and add into it the following snippet:

    // Set up a buildscript dependency on the Kotlin plugin.
    buildscript {
        // Specify a Kotlin version you need.
        ext.kotlin_version = '1.2.31'

        repositories {
            google()
            jcenter()
            mavenCentral()
            maven { url "https://dl.bintray.com/jetbrains/kotlin-native-dependencies" }
        }

        // Specify all the plugins used as dependencies
        dependencies {
            classpath 'com.android.tools.build:gradle:3.1.0'
            classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
            classpath "org.jetbrains.kotlin:kotlin-native-gradle-plugin:0.6.2"

        }
    }

    // Set up compilation dependency repositories for all projects.
    subprojects {
        repositories {
            google()
            jcenter()
            mavenCentral()
        }
    }

Now all subprojects of the library can use Kotlin plugins.

#### 2.1 Common subproject

The `common` subproject contains a platform-independent code. Let's create `common/src/main/kotlin/common.kt` and
add some functionality into it:

    // greeting/common/src/main/kotlin/common.kt
    package org.greeting

    expect class Platform() {
        val platform: String
    }

    class Greeting {
        fun greeting(): String = "Hello, ${Platform().platform}"
    }


Here we create a simple class using `expect`/`actual` paradigm. See details about platform-specific declarations
[here](https://kotlinlang.org/docs/reference/multiplatform.html#platform-specific-declarations) **TODO: more details here?**

To build this common project add the following snippet in `common/build.gradle`:

    apply plugin: 'kotlin-platform-common'

    // Specify a group and a version of the library to access it in Android Studio.
    group = 'org.greeting'
    version = 1.0

    dependencies {
        // Set up compilation dependency on common Kotlin stdlib
        compile "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version"
    }

#### 2.2 Android subproject

The `android` subproject contains platform-dependent implementations of `expect`-declarations we've created in the
`common` project. Let's implement our `expect`-class:

    // greeting/android/src/main/kotlin/android.kt
    package org.greeting

    actual class Platform actual constructor() {
        actual val platform: String = "Android"
    }

We build this project into a Java-library which can be used by Android Studio project as a dependency. So the content
of `android/build.gradle` will be the following:

    apply plugin: 'java-library'
    apply plugin: 'kotlin-platform-jvm'

    // Specify a group and a version of the library to access it in Android Studio.
    group = 'org.greeting'
    version = 1.0

    dependencies {
        // Specify Kotlin/JVM stdlib dependency.
        implementation "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlin_version"

        // Specify dependency on a common project for Kotlin multiplatform build.
        expectedBy project(':common')
    }

#### 2.3 iOS subproject

As well as `android`, this project contains platform-dependent implementations of `expect`-declarations:

    // greeting/ios/src/main/kotlin/ios.kt
    package org.greeting

    actual class Platform actual constructor() {
        actual val platform: String = "iOS"
    }

We build this project into an Objective-C framework using Kotlin/Native compiler. To do this, declare a framework in
`ios/build.gradle` and add an `expectedBy` dependency in the same manner as in the Android project:

    apply plugin: 'konan'

    konanArtifacts {
        // Declare building into a framework.
        framework('ios-greeting') {
            // The multiplatform support is disabled by default.
            enableMultiplatform true
        }
    }

    dependencies {
        // Specify dependency on a common project for Kotlin multiplatform build
        expectedBy project(':common')
    }

### 3. Android application

Now we can create an Android application which will use the library we implemented on the previous step. Open Android
Studio and create a new project in the `androidApp` directory. Android Studio generates all necessary files and
directories so we need only add a dependency on our library. There are only 2 actions we have to do:

1. Add dependency on the library. To do this just open `androidApp/app/build.gradle` and add the following snippet in
the `dependencies` script block:

    ```
    implementation 'org.greeting:android:1.0'
    implementation 'org.greeting:common:1.0'
    ```
2. Include `greeting` build in the Android Studio project as part of
[composite build](https://docs.gradle.org/current/userguide/composite_builds.html). To do it, add the
following line in `androidApp/settings.gradle`:

    ```
    includeBuild '../greeting'
    ```
    Now dependencies of the application can be resolved in artefacts built by `greeting`, so we can access our library.

**TODO: What is better: composite build or adding `greeting` as a subproject in the AS project?**

### 4. iOS application

**TODO**


































