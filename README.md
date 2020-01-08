# MSEngine Java engine over LWJGL 3

This library is a game engine helper for LWJGL 3 adding support for :
- Basic mono-thread game logic loops for either server (ticks per second regulation, default to 20 TPS) and client (in addition to TPS regulation, FPS regulation)
- Advanced resource handler and a language system for client and server
- Lot of math utilities including fast floor/ceil, interpolation methods
- JOML compatible color class can be used for shaders uniforms
- Lot of middle-level helpers for OpenGL mainly for easy managing of GLFW window, shader managers, framebuffers and textures
- Lot of textures types including dynamic texture generated at runtime, texture maps generated at runtime with animations metadata
- A gui helper with a basic pixelated font
- Defaults shaders for basic 3D or 2D programs with predefined uniforms, sampler & attributes
- Vertex buffer or Indices vertex buffer to use with shaders to easily upload VAO and VBOs data
- OpenGL 4.0 minimum is required and will be configurable in the future

This engine is available on Maven Central since version `1.0.5` in two versions :
- `fr.theorozier:msengine-client:1.0.6`
- `fr.theorozier:msengine-common:1.0.6`

Each version released on GitLab will be soon available on Maven Central.

## Contribute

Section TODO.

Remember to use `./gradlew` or `./gradlew.bat` to use gradle.