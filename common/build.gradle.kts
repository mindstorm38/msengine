
description = rootProject.description + " - Common Libs"

dependencies {

    val jomlVersion = project.ext["jomlVersion"]
    val guavaVersion = project.ext["guavaVersion"]
    val gsonVersion = project.ext["gsonVersion"]
    val nettyVersion = project.ext["nettyVersion"]

    "compile"("com.google.guava:guava:${guavaVersion}")
    "compile"("com.google.code.gson:gson:${gsonVersion}")
    "compile"("io.netty:netty-all:${nettyVersion}")
    "compile"("org.joml:joml:${jomlVersion}")

}