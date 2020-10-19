//
//  [MSEngine] Examples module
//

apply(plugin = "java")

description = "MSEngine - Examples."

dependencies {
    "implementation"(project(":client"))
}

tasks.register<JavaExec>("window") {
    group = "Examples"
    description = "Test MSE windows"
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
    main = "io.msengine.example.window.WindowExample"
}