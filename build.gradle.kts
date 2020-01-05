/*

	-- MS Engine (MSE) --

	Java   : 1.8
	Gradle : 6.0

*/

buildscript {

    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }

}

description = "MS Engine"

allprojects {

    apply(plugin = "com.github.johnrengelman.shadow")

    version = "1.0.0"

    ext {

        set("lwjglVersion", "3.1.6")
        set("lwjglNatives", listOf("natives-windows", "natives-linux", "natives-macos"))
        set("jomlVersion", "1.9.6")
        set("guavaVersion", "23.2-jre")
        set("gsonVersion", "2.8.2")
        set("nettyVersion", "4.1.17.Final")

    }

}

subprojects {

    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    dependencies {
        "compile"(files("../libs/sutil-1.0.14.jar"))
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
    }

}

