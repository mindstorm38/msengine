
dependencies {

    val lwjglVersion = project.ext["lwjglVersion"] as String
    val lwjglNatives: List<String> = project.ext["lwjglNatives"] as List<String>

    "compileOnly"(project(":common"))

    "api"("org.lwjgl", "lwjgl", lwjglVersion)
    "api"("org.lwjgl", "lwjgl-glfw", lwjglVersion)
    "api"("org.lwjgl", "lwjgl-jemalloc", lwjglVersion)
    "api"("org.lwjgl", "lwjgl-openal", lwjglVersion)
    "api"("org.lwjgl", "lwjgl-opengl", lwjglVersion)
    "api"("org.lwjgl", "lwjgl-stb", lwjglVersion)

    lwjglNatives.forEach { natives ->

        "runtimeOnly"("org.lwjgl", "lwjgl", lwjglVersion, classifier=natives)
        "runtimeOnly"("org.lwjgl", "lwjgl-glfw", lwjglVersion, classifier=natives)
        "runtimeOnly"("org.lwjgl", "lwjgl-jemalloc", lwjglVersion, classifier=natives)
        "runtimeOnly"("org.lwjgl", "lwjgl-openal", lwjglVersion, classifier=natives)
        "runtimeOnly"("org.lwjgl", "lwjgl-opengl", lwjglVersion, classifier=natives)
        "runtimeOnly"("org.lwjgl", "lwjgl-stb", lwjglVersion, classifier=natives)

    }

}

configure<PublishingExtension> {

    publications {
        named<MavenPublication>("mavenJar") {

            pom.withXml {

                val dependenciesNode = (asNode().get("dependencies") as groovy.util.NodeList)[0] as groovy.util.Node
                val dependencyCommonNode = dependenciesNode.appendNode("dependency")

                dependencyCommonNode.appendNode("groupId", project.group)
                dependencyCommonNode.appendNode("artifactId", "msengine-common")
                dependencyCommonNode.appendNode("version", project.version)
                dependencyCommonNode.appendNode("scope", "compile")

            }

        }
    }

}