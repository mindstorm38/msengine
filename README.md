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
- `fr.theorozier:msengine-client:1.0.7`
- `fr.theorozier:msengine-common:1.0.7`

Each version released on GitLab will be soon available on Maven Central.

## Goals for MSEngine 1.1
- [ ] Deprecate all uses of SUtil
- [ ] Remove `{client,common}.game` packages
- [x] Create tick/frame regulated interfaces
    - `common.logic.TickRegulated`
    - `common.logic.FrameRegulated`
- [ ] Remove OSF package `common.osf`
- [ ] Rework GUI (`client.graphics.gui`)
    - [x] Rework base GUI framework classes
    - [x] Rework shaders, buffers and masking
    - [ ] Re-implement all useful classes from old GUI framework
- [x] Rework event managers :
    - Object event manager `common.util.event.ObjectEventManager`
    - Method event manager `common.util.event.MethodEventManager`
- [x] Rework `Window` class (no longer a singleton, but an object oriented wrapper for GLFW)
    - `client.window.Monitor`
    - `client.window.Window`
    - `client.window.WindowBuilder`
    - `client.window.*`
- [x] Rework resources (using java FileSystems)
    - `common.asset.*`
- [ ] Rework options
- [ ] Work on audio package which was never finished
- [ ] Deprecate uses of `GameLogger` *(use individual named loggers instead)*
- [ ] Update `AxisAlignedBB`
- [ ] Remove or adapt `client.renderer.texture.TextureObject`
- [ ] Promote raw uses of GL functions and make simple wrappers for complicated GL objects *(shader, programs, framebuffers, etc.)*

## Contribute

Remember to use `./gradlew` or `./gradlew.bat` to use gradle.
