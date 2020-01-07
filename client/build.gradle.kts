
dependencies {

    val lwjglVersion = project.ext["lwjglVersion"]
    val lwjglNatives: List<String> = project.ext["lwjglNatives"] as List<String>

    "compile"(project(":common"))

    "compile"("org.lwjgl:lwjgl:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-jemalloc:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-openal:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-stb:$lwjglVersion")

    lwjglNatives.forEach { natives ->

        "runtime"("org.lwjgl:lwjgl:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-glfw:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-jemalloc:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-openal:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-opengl:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-stb:$lwjglVersion:$natives")

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
