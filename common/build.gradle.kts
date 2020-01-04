
description = rootProject.description + " - Common Libs"

dependencies {

    val guavaVersion = rootProject.ext["guavaVersion"]
    val gsonVersion = rootProject.ext["gsonVersion"]
    val nettyVersion = rootProject.ext["nettyVersion"]

    "compile"(project(":sutil"))

    "compile"("com.google.guava:guava:${guavaVersion}")
    "compile"("com.google.code.gson:gson:${gsonVersion}")
    "compile"("io.netty:netty-all:${nettyVersion}")

}