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
        // classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }

}

// typealias ShadowJar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Import from your gradle.properties
val ossrhUsername: String by project
val ossrhPassword: String by project

allprojects {

    version = "1.0.5"
    group = "fr.theorozier"

    ext {

        set("lwjglVersion", "3.1.6")
        set("lwjglNatives", listOf("natives-windows", "natives-linux", "natives-macos"))
        set("jomlVersion", "1.9.6")
        set("guavaVersion", "23.2-jre")
        set("gsonVersion", "2.8.2")
        set("nettyVersion", "4.1.17.Final")

    }

}

description = "A Java 3D engine on top of LWJGL 3, using OpenGL, GLFW and JOML"
project("client").description = "$description - Client side library, containing OpenGL natives."
project("common").description = "$description - Common library, containing math utilies and resources handling."

subprojects {

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "maven")
    apply(plugin = "signing")
    // apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
    }

    dependencies {
        "compile"("fr.theorozier:sutil:1.1.0")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
    }

    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
    }

    tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("javadoc"))
    }

    configure<PublishingExtension> {

        publications {
            register<MavenPublication>("mavenJar") {

                from(components["java"])
                artifact(tasks.named<Jar>("sourcesJar").get())
                artifact(tasks.named<Jar>("javadocJar").get())

                pom {

                    artifactId = "${rootProject.name}-${project.name}"

                    name.set("${groupId}-${artifactId}")
                    description.set(project.description)
                    url.set("https://gitlab.com/mindstorm38/msengine")

                    developers {
                        developer {

                            id.set("fr.theorozier")
                            name.set("Théo Rozier")
                            email.set("contact@theorozier.fr")
                            url.set("https://github.com/mindstorm38")

                        }
                    }

                    licenses {
                        license {
                            name.set("GNU General Public License v3.0")
                            url.set("https://opensource.org/licenses/GPL-3.0")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/mindstorm38/sutil.git")
                        developerConnection.set("scm:git:ssh://github.com:mindstorm38/sutil.git")
                        url.set("https://github.com/mindstorm38/sutil/tree/master")
                    }

                }

            }
        }

        repositories {
            maven {

                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }

                val releaseRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                val snapshotRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"

                url = uri(if ((project.version as String).endsWith("SNAPSHOT")) snapshotRepoUrl else releaseRepoUrl)

            }
        }

    }

    configure<SigningExtension> {
        sign(project.the<PublishingExtension>().publications.named("mavenJar").get())
    }

    /*
    tasks.named<ShadowJar>("shadowJar") {
        exclude("*.dll.git", "*.dll.sha1")
        exclude("*.dylib.git", "*.dylib.sha1")
        exclude("*.so.git", "*.so.sha1")
    }
    */

}

/*
tasks.create<Copy>("distrib") {

    group = "shadow"
    dependsOn("common:shadowJar", "client:shadowJar")

    from(
        project("common").tasks.named<ShadowJar>("shadowJar").get().archiveFile,
        project("client").tasks.named<ShadowJar>("shadowJar").get().archiveFile
    )

    into("build/libs")

}
*/
