
description = rootProject.description + " - Client Side"

dependencies {

    val lwjglVersion = rootProject.ext["lwjglVersion"]
    val lwjglNatives: List<String> = rootProject.ext["lwjglNatives"] as List<String>
    val jomlVersion = rootProject.ext["jomlVersion"]

    "compile"(project(":sutil"))
    "compile"(project(":common"))

    "compile"("org.lwjgl:lwjgl:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-jemalloc:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-openal:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    "compile"("org.lwjgl:lwjgl-stb:$lwjglVersion")
    "compile"("org.joml:joml:${jomlVersion}")

    lwjglNatives.forEach { natives ->

        "runtime"("org.lwjgl:lwjgl:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-glfw:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-jemalloc:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-openal:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-opengl:$lwjglVersion:$natives")
        "runtime"("org.lwjgl:lwjgl-stb:$lwjglVersion:$natives")

    }

}