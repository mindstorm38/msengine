
dependencies {

    val jomlVersion = project.ext["jomlVersion"] as String
    val guavaVersion = project.ext["guavaVersion"] as String
    val gsonVersion = project.ext["gsonVersion"] as String
    val nettyVersion = project.ext["nettyVersion"] as String

    "api"("com.google.guava", "guava", guavaVersion)
    "api"("com.google.code.gson", "gson", gsonVersion)
    "api"("io.netty", "netty-all", nettyVersion)
    "api"("org.joml", "joml", jomlVersion)

}