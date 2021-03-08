# MSEngine - Java Library over LWJGL 3

This library is built over LWJGL 3 that provide a lot of useful wrapper
classes over OpenGL, OpenAL or GLFW "C objects".

The library is split between two modules `common` and `client`, the
client one depends on the common.

# Table of contents

- [Common](#common-module)
  - [Logic](#logic)
  - [Assets](#assets)
- [Client module](#client-module)
  - [Windowing](#windowing)
  - []()

# Common module

The common module provides common methods and classes for both your client
and server side. This module provides logic, assets management or common
utilities.

> Package: `io.msengine.common`

## Logic

This module provides two interfaces `TickRegulated`, `FrameRegulated`
useful for TPS and/or FPS regulation, *theses two regulations use only
the calling thread*.

The `TickRegulated` interface can be implemented on your class and provide
a **default** method `regulateTick(tps)` to call with the number of ticks
per seconds to regulate. This method will call the `tick()` method every
`1/tps` second unless the implemented method `shouldStop()` return true.

The `FrameRegulated` interfaces extends the tick one, but instead of the
method `regulateTick(tps)`, you must use `regulateFrames(tps, fps)` to
start the regulation. In addition to calls to `tick()` done by ticks
regulation, the `render(alpha)` method is called every `1/fps` second
with the interpolation factor. This factor is useful when FPS are
greater than TPS, because in this case the rendering happens multiple
times between ticks. You must also implement a method `mustSync()`, you
must return false if the rendering is vertically-synchronized.

```java
class FooApp implements FrameRegulated {
    private static final int TPS = 20;
    private static final int FPS = 60;
    public void tick() {
        System.out.println("I tick every 1/" + TPS + " second");
    }
    public void render(float alpha) {
        System.out.println("I render every 1/" + FPS + " second");
    }
    public boolean shouldStop() {
        return false;
    }
    public boolean mustSync() {
        return true;
    }
    public void start() {
        this.regulateFrames(TPS, FPS);
    }
}
```

> Package: `io.msengine.common.logic`

## Assets

The abstract class `Assets` is designed to be as generic as possible to fit
most of the file systems that can be useful for assets resolution.

For now  the only implementation is `ClassLoaderAssets`, it's useful if you
store your assets in the JAR *(it also works with IDEs)*. You can construct
a `ClassLoaderAssets` by using its constructor, but you can use the more
practical methods `Assets.forClassLoader(loader, root)` or
`Assets.forClass(class, root)` by providing either the desired `ClassLoader`
or the `Class` loaded by the desired loader, you must also give the `root`
directory containing the assets, it will be appended to all the given
path before resolving the resource.

Every `Assets` object provides multiple methods to get or list the resources.

```java
class BarApp {
    public static final Assets ASSETS = Assets.forClass(FooApp.class, "assets");
	public static InputStream getIconStream() {
        // Internally this calls: FooApp.class.getClassLoader().getResourceAsStream("assets/icons/window_icon.png")
        return ASSETS.openAssetStream("icons/window_icon.png");
    }
}
```

> Package: `io.msengine.common.asset`

# Client module

The client module provides client-side methods and classes, mainly oriented
around OpenGL for the rendering, OpenAL for the audio or GLFW for the
windowing.

> Package: `io.msengine.client`<br>
> Maven: `fr.theorozier:msengine-client:1.1.0`

## Windowing

The windowing module of this library is an 

> Package: `io.msengine.common.asset`

## Graphics
TODO

### GUI
TODO

### Object-Oriented OpenGL
TODO

## Audio
TODO

## Miscellaneous Utilities
TODO

----------------

# Deprecated doc

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

The current version of the library is "1.1.0" :
- `fr.theorozier:msengine-client:1.1.0`
- `fr.theorozier:msengine-common:1.1.0`

Each version released on Github will be soon available on Maven Central.

## Goals for MSEngine 1.1
- [x] Deprecate all uses of SUtil
- [x] Deprecate `{client,common}.game` packages
- [x] Create tick/frame regulated interfaces
    - `common.logic.TickRegulated`
    - `common.logic.FrameRegulated`
- [x] Deprecate OSF package `common.osf`
- [x] TrueType fonts support
- [x] Rework GUI (`client.graphics.gui`)
    - [x] Rework base GUI framework classes
    - [x] Rework shaders, buffers and masking
    - [x] GUI text box (including TrueType font)
    - [x] Re-implement all useful classes from old GUI framework
- [x] Rework event managers :
    - Object event manager `common.util.event.ObjectEventManager`
    - Method event manager `common.util.event.MethodEventManager`
- [x] Rework `Window` class (no longer a singleton, but an object oriented wrapper for GLFW)
    - `client.window.Monitor`
    - `client.window.Window`
    - `client.window.WindowBuilder`
    - `client.window.*`
- [x] Rework resources (~~using java FileSystems~~)
    - `common.asset.*`
- [x] Rework textures (for 1D, 2D, 3D)
- [x] Work on audio package which was never finished
- [x] Deprecate uses of `GameLogger` *(use individual named loggers instead)*
    - [x] Create a logger formatter to apply to global loger
- [x] Update `AxisAlignedBB`
    - `common.util.phys.AABB2d`
    - `common.util.phys.AABB3d`
- [x] Remove or adapt `client.renderer.texture.TextureObject`
- [x] Promote raw uses of GL functions and make simple wrappers for complicated GL objects *(shader, programs, framebuffers, etc.)*

## Contribute

Remember to use `./gradlew` or `./gradlew.bat` to use gradle.
