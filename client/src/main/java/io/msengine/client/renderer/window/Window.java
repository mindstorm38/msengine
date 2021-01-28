package io.msengine.client.renderer.window;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.EnumMap;
import java.util.Objects;
import java.util.logging.Level;

import io.msengine.common.util.event.MethodEventManager;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.window.listener.WindowCharEventListener;
import io.msengine.client.renderer.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.client.renderer.window.listener.WindowKeyEventListener;
import io.msengine.client.renderer.window.listener.WindowMouseButtonEventListener;
import io.msengine.client.renderer.window.listener.WindowMousePositionEventListener;
import io.msengine.client.renderer.window.listener.WindowScrollEventListener;
import io.msengine.client.util.Utils;
import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.Tuple;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * Main window for the game used to create OpenGL context and handle events
 * 
 * @author Mindstorm
 *
 */
@Deprecated
public class Window {
	
	// Constants \\
	
	public static final String ICON_BASE_PATH = "textures/logos/logo%s.png";
	public static final String[] ICON_SIZES = { "16", "32", "48", "64", "128" };
	
	// Singleton \\
	
	private static Window INSTANCE = null;
	
	public static Window getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( Window.class );
		return INSTANCE;
	}
	
	// Class \\
	
	private long id = 0L;
	private boolean fullscreen = false;
	private boolean vsync = false;
	private int framebufferWidth = 1280;
	private int framebufferHeight = 720;
	private int minimizedWidth = framebufferWidth;
	private int minimizedHeight = framebufferHeight;
	private GLCapabilities capabilities = null;
	// private KeyboardType keyboardType = KeyboardType.getDefault();
	
	private int cursorX = 0;
	private int cursorY = 0;
	
	private final EnumMap<WindowCursor, Long> cursors = new EnumMap<>( WindowCursor.class );
	private WindowCursor cursor = null;
	
	/*private final List<WindowKeyEventListener> keyEventListeners = new ArrayList<>();
	private final List<WindowMouseButtonEventListener> mouseButtonEventListeners = new ArrayList<>();
	private final List<WindowScrollEventListener> scrollEventListeners = new ArrayList<>();
	private final List<WindowMousePositionEventListener> mousePositionEventListeners = new ArrayList<>();
	private final List<WindowCharEventListener> charEventListeners = new ArrayList<>();
	private final List<WindowFramebufferSizeEventListener> framebufferSizeEventListeners = new ArrayList<>();*/
	
	private final MethodEventManager eventManager;
	
	public Window() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( Window.class );
		INSTANCE = this;
		
		this.eventManager = new MethodEventManager(
				WindowKeyEventListener.class,
				WindowMouseButtonEventListener.class,
				WindowScrollEventListener.class,
				WindowMousePositionEventListener.class,
				WindowCharEventListener.class,
				WindowFramebufferSizeEventListener.class
		);
		
	}
	
	/**
	 * Start window
	 */
	public void start(String initialWindowTitle) {
		
		if ( this.id != 0L ) throw new IllegalStateException("Window already started");
		
		LOGGER.info("Starting window ...");
		
		if ( !glfwInit() ) throw new IllegalStateException("Unable to init GLFW window !");
		
		glfwSetErrorCallback( (int error, long rawDescription) -> {
			
			String description = MemoryUtil.memASCII( rawDescription );
			LOGGER.severe( "[GLFW] " + description );
			
		} );
		
		glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE );
		glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 4 );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 0 );
		glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );
		glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE );
		
		this.id = glfwCreateWindow( this.framebufferWidth, this.framebufferHeight, initialWindowTitle, 0L, 0L );
		
		if ( this.id == 0L ) throw new IllegalStateException("Unable to create GLFW window !");
		
		glfwSetWindowSizeLimits( this.id, 420, 220, GLFW_DONT_CARE, GLFW_DONT_CARE );
		
		this.setIcons();
		this.initCursors();
		
		glfwMakeContextCurrent( this.id );
		this.setVsync( this.vsync );
		glfwShowWindow( this.id );
		this.capabilities = createCapabilities();
		
		glfwSetKeyCallback( this.id, ( long window, int key, int scancode, int action, int mods ) -> {
			
			this.eventManager.fireListeners(WindowKeyEventListener.class, l ->
					l.windowKeyEvent(key, scancode, action, mods));
			
			/*for ( WindowKeyEventListener l : this.keyEventListeners ) {
				l.windowKeyEvent( key, scancode, action, mods );
			}*/
			
		} );
		
		glfwSetMouseButtonCallback( this.id, ( long window, int button, int action, int mods ) -> {
			
			this.eventManager.fireListeners(WindowMouseButtonEventListener.class, l ->
					l.windowMouseButtonEvent(button, action, mods));
			
			/*for ( WindowMouseButtonEventListener l : this.mouseButtonEventListeners ) {
				l.windowMouseButtonEvent( button, action, mods );
			}*/
			
		} );
		
		glfwSetScrollCallback( this.id, ( long window, double xoffset, double yoffset ) -> {
			
			this.eventManager.fireListeners(WindowScrollEventListener.class, l ->
					l.windowScrollEvent(xoffset, yoffset));
			
			/*for ( WindowScrollEventListener l : this.scrollEventListeners ) {
				l.windowScrollEvent( xoffset, yoffset );
			}*/
			
		} );
		
		glfwSetCursorPosCallback( this.id, ( long window, double xpos, double ypos ) -> {
			
			this.cursorX = (int) xpos;
			this.cursorY = (int) ypos;
			
			this.eventManager.fireListeners(WindowMousePositionEventListener.class, l ->
					l.windowMousePositionEvent(this.cursorX, this.cursorY));
			
			/*for ( WindowMousePositionEventListener l : this.mousePositionEventListeners ) {
				l.windowMousePositionEvent( this.cursorX, this.cursorY );
			}*/
			
		} );
		
		glfwSetCharCallback( this.id, ( long window, int codepoint ) -> {
			
			this.eventManager.fireListeners(WindowCharEventListener.class, l ->
					l.windowCharEvent((char) codepoint));
			
			/*for ( WindowCharEventListener l : this.charEventListeners ) {
				l.windowCharEvent( (char) codepoint );
			}*/
			
		} );
		
		glfwSetFramebufferSizeCallback( this.id, ( long window, int width, int height ) -> {
			
			this.triggerFramebufferResized( width, height );
			
		} );
		
		glfwSetWindowMaximizeCallback( this.id, ( long window, boolean maximized ) -> {
			
			this.triggerFramebufferResizedRaw();
			
		} );
		
		this.setFullscreen( this.fullscreen, true );
		
		LOGGER.info("Window started");
		
	}
	
	/**
	 * Stop and destroy window
	 */
	public void stop() {
		
		this.checkWindowState();
		
		LOGGER.info("Stoping window ...");
		
		Callbacks.glfwFreeCallbacks( this.id );
		glfwDestroyWindow( this.id );
		glfwTerminate();
		
		this.id = 0L;
		
		LOGGER.info("Window stoped");
		
	}
	
	/**
	 * Set icons of the window, only used in {@link #start(String)}
	 */
	private void setIcons() {
		
		ResourceManager resourceManager = ResourceManager.getInstance();
		
		BufferedImage image;
		ByteBuffer pixelBuffer;
		
		try ( GLFWImage.Buffer icons = GLFWImage.malloc( ICON_SIZES.length ) ) {
			
			for ( int i = 0; i < ICON_SIZES.length; i++ ) {
				
				try ( DetailledResource resource = resourceManager.getDetailledResource( String.format( ICON_BASE_PATH, ICON_SIZES[ i ] ) ) ) {
					
					if ( resource == null ) {
						
						LOGGER.warning( "Failed to load icon resource for size '" + ICON_SIZES[ i ] + "'" );
						continue;
						
					}
					
					image = resource.getImage();
					
					if ( image == null ) {
						
						LOGGER.warning( "Failed to load icon image for size '" + ICON_SIZES[ i ] + "'" );
						continue;
						
					}
					
					pixelBuffer = Utils.getImageBuffer( image );
					
					icons
						.position( i )
						.width( image.getWidth() )
						.height( image.getHeight() )
						.pixels( pixelBuffer );
					
				} catch (IOException e) {
					LOGGER.log( Level.WARNING, "Failed to close icon resource", e );
				}
				
			}
			
			icons.flip();
			
			glfwSetWindowIcon( this.id, icons );
			
		}
		
	}
	
	/**
	 * Init cursors
	 */
	private void initCursors() {
		
		this.initDefaultCursors();
		
	}
	
	/**
	 * Init default cursors
	 */
	private void initDefaultCursors() {
		
		this.cursors.put( WindowCursor.ARROW, glfwCreateStandardCursor( GLFW_ARROW_CURSOR ) );
		this.cursors.put( WindowCursor.TEXT, glfwCreateStandardCursor( GLFW_IBEAM_CURSOR ) );
		this.cursors.put( WindowCursor.CROSSHAIR, glfwCreateStandardCursor( GLFW_CROSSHAIR_CURSOR ) );
		this.cursors.put( WindowCursor.HAND, glfwCreateStandardCursor( GLFW_HAND_CURSOR ) );
		this.cursors.put( WindowCursor.HRESIZE, glfwCreateStandardCursor( GLFW_HRESIZE_CURSOR ) );
		this.cursors.put( WindowCursor.VRESIZE, glfwCreateStandardCursor( GLFW_VRESIZE_CURSOR ) );
		
	}
	
	/**
	 * Set the current used cursor
	 * @param cursor The cursor to use
	 */
	public void setCursor(WindowCursor cursor) {
		this.checkWindowState();
		long pointer = cursor == null ? 0L : this.cursors.get( cursor );
		this.cursor = pointer == 0L ? WindowCursor.ARROW : cursor; 
		glfwSetCursor( this.id, pointer );
	}
	
	/**
	 * Reset cursor to default
	 */
	public void resetCursor() {
		this.checkWindowState();
		this.cursor = WindowCursor.ARROW;
		glfwSetCursor( this.id, 0L );
	}
	
	/**
	 * @return Currently used cursor
	 */
	public WindowCursor getCursor() {
		return this.cursor;
	}
	
	/**
	 * Set cursor mode.
	 * @param cursorMode Cursor mode.
	 */
	public void setCursorMode(CursorMode cursorMode) {
		
		this.checkWindowState();
		glfwSetInputMode(this.getWindowId(), GLFW_CURSOR, Objects.requireNonNull(cursorMode).nativ);
		
	}
	
	/**
	 * Internal function to trigger WindowFramebufferSizeEventListener(s) and define new 'width' and 'height' fields
	 * @param width New framebuffer width
	 * @param height New framebuffer height
	 */
	private void triggerFramebufferResized(int width, int height) {
		
		if ( width < 1 || height < 1 ) return;
		
		/*for ( WindowFramebufferSizeEventListener l : this.framebufferSizeEventListeners ) {
			l.windowFramebufferSizeChangedEvent( width, height );
		}*/
		
		this.eventManager.fireListeners(WindowFramebufferSizeEventListener.class, l ->
				l.windowFramebufferSizeChangedEvent(width, height));
		
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		
	}
	
	/**
	 * Raw use for {@link #triggerFramebufferResized(int, int)}, using {@link GLFW#glfwGetFramebufferSize(long, IntBuffer, IntBuffer)} function
	 */
	private void triggerFramebufferResizedRaw() {
		
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			
			glfwGetFramebufferSize( this.id, width, height );
			
			this.triggerFramebufferResized( width.get(), height.get() );
			
		}
		
	}
	
	/**
	 * Checking window state, {@link IllegalStateException} thrown if window not started
	 */
	private void checkWindowState() {
		if ( this.id == 0L ) throw new IllegalStateException("Window not started !");
	}
	
	/**
	 * Update window buffer, poll events ...
	 */
	public void pollEvents() {
		this.checkWindowState();
		glfwPollEvents();
	}
	
	/**
	 * Swap window buffers
	 */
	public void swapBuffers() {
		this.checkWindowState();
		glfwSwapBuffers( this.id );
	}
	
	/**
	 * @return Window width
	 */
	public int getWidth() {
		return this.framebufferWidth;
	}
	
	/**
	 * @return Window height
	 */
	public int getHeight() {
		return this.framebufferHeight;
	}
	
	/**
	 * @return VSync state of the window
	 */
	public boolean isVsync() {
		return this.vsync;
	}
	
	/**
	 * @param vsync New VSync state for the window
	 */
	public void setVsync(boolean vsync) {
		this.checkWindowState();
		glfwSwapInterval( ( this.vsync = vsync ) ? 1 : 0 );
	}
	
	/**
	 * Non-force version of {@link #setFullscreen(boolean, boolean)}
	 * @param fullscreen Fullscreen state
	 */
	public void setFullscreen(boolean fullscreen) {
		this.setFullscreen( fullscreen, false );
	}
	
	/**
	 * Change window fullscreen state
	 * @param fullscreen Fullscreen state
	 * @param force Force window to the desired fullscreen state
	 */
	public void setFullscreen(boolean fullscreen, boolean force) {
		
		this.checkWindowState();
		
		if ( this.fullscreen == fullscreen && !force ) return;
		
		this.fullscreen = fullscreen;
		
		long monitor = glfwGetPrimaryMonitor();
		if ( monitor == 0L ) this.fullscreen = false;
		GLFWVidMode vidmode = monitor == 0L ? null : glfwGetVideoMode( monitor );
		
		if ( this.fullscreen ) {
			
			try ( MemoryStack stack = MemoryStack.stackPush() ) {
				
				IntBuffer width = stack.mallocInt(1);
				IntBuffer height = stack.mallocInt(1);
				
				glfwGetWindowSize( this.id, width, height);
				
				this.minimizedWidth = width.get();
				this.minimizedHeight = height.get();
				
			}
			
			int width = vidmode.width();
			int height = vidmode.height();
			
			glfwSetWindowMonitor( this.id, monitor, 10, 0, width, height, GLFW_DONT_CARE );
			
			this.triggerFramebufferResized( width, height );
			
		} else {
			
			glfwSetWindowMonitor( this.id, 0L, ( vidmode.width() / 2 - this.minimizedWidth / 2 ), ( vidmode.height() / 2 - this.minimizedHeight / 2 ), this.minimizedWidth, this.minimizedHeight, GLFW_DONT_CARE );
			
			this.triggerFramebufferResizedRaw();
			
		}
		
	}
	
	/**
	 * Switch current fullscreen state
	 * @return Fullscreen state of the window
	 */
	public boolean toggleFullscreen() {
		this.setFullscreen( !fullscreen );
		return this.fullscreen;
	}
	
	/**
	 * @return Fullscreen state of the window
	 */
	public boolean isFullscreen() {
		return this.fullscreen;
	}
	
	/**
	 * @return Current cursor position
	 */
	public Tuple<Integer, Integer> getCursorPosition() {
		return new Tuple<>( this.cursorX, this.cursorY );
	}
	
	public int getCursorPosX() {
		return this.cursorX;
	}
	
	public int getCursorPosY() {
		return this.cursorY;
	}
	
	/**
	 * Set current cursor position
	 * @param x Cursor x position
	 * @param y Cursor y position
	 */
	public void setCursorPosition(int x, int y) {
		glfwSetCursorPos(this.id, this.cursorX = x, this.cursorY = y);
	}
	
	/*
	 * @return Current window {@link KeyboardType}
	 */
	/*public KeyboardType getKeyboardType() {
		return this.keyboardType;
	}*/
	
	/*
	 * @param newKeyboardType Define new {@link KeyboardType}
	 */
	/*public void setKeyboardType(KeyboardType keyboardType) {
		this.keyboardType = keyboardType;
	}*/
	
	/**
	 * @return Current context {@link GLCapabilities}
	 */
	public GLCapabilities getCapabilities() {
		return this.capabilities;
	}
	
	/**
	 * @return Window should close state
	 */
	public boolean shouldClose() {
		return this.id == 0L || glfwWindowShouldClose( this.id );
	}
	
	/**
	 * @return Current window id (<code>0L</code> if not created)
	 */
	public long getWindowId() {
		return this.id;
	}
	
	/**
	 * Check if key pressed
	 * @param keyCode Keycode
	 * @return Key pressed
	 */
	public boolean isKeyPressed(int keyCode) {
		this.checkWindowState();
		return glfwGetKey( this.id, keyCode ) == GLFW_PRESS;
	}
	
	/**
	 * Check if one or more Ctrl key pressed
	 * @return Ctrl mod state
	 */
	public boolean isKeyCtrlMod() {
		this.checkWindowState();
		return glfwGetKey( this.id, GLFW_KEY_LEFT_CONTROL ) == GLFW_PRESS || glfwGetKey( this.id, GLFW_KEY_RIGHT_CONTROL ) == GLFW_PRESS;
	}
	
	/**
	 * Check if one or more Shift key pressed
	 * @return Shift mod state
	 */
	public boolean isKeyShiftMod() {
		this.checkWindowState();
		return glfwGetKey( this.id, GLFW_KEY_LEFT_SHIFT ) == GLFW_PRESS || glfwGetKey( this.id, GLFW_KEY_RIGHT_SHIFT ) == GLFW_PRESS;
	}
	
	/**
	 * Check if alt key pressed
	 * @return Alt mod state
	 */
	public boolean isKeyAltMod() {
		this.checkWindowState();
		return glfwGetKey( this.id, GLFW_KEY_LEFT_ALT ) == GLFW_PRESS;
	}
	
	public boolean isMouseButtonPressed(int buttonCode) {
		this.checkWindowState();
		return glfwGetMouseButton( this.id, buttonCode ) == GLFW_PRESS;
	}
	
	/**
	 * @return Current window time.
	 */
	public double getTime() {
		return glfwGetTime();
	}
	
	// Static utils //
	
	public static boolean isModActive(int mods, int modbit) {
		return (mods & modbit) == modbit;
	}
	
	// - Listeners
	
	public MethodEventManager getEventManager() {
		return this.eventManager;
	}
	
	public void addKeyEventListener(WindowKeyEventListener l) {
		//this.keyEventListeners.add( l );
		this.eventManager.addEventListener(WindowKeyEventListener.class, l);
	}
	public void removeKeyEventListener(WindowKeyEventListener l) {
		//this.keyEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowKeyEventListener.class, l);
	}
	
	public void addMouseButtonEventListener(WindowMouseButtonEventListener l) {
		//this.mouseButtonEventListeners.add( l );
		this.eventManager.addEventListener(WindowMouseButtonEventListener.class, l);
	}
	public void removeMouseButtonEventListener(WindowMouseButtonEventListener l) {
		//this.mouseButtonEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowMouseButtonEventListener.class, l);
	}
	
	public void addScrollEventListener(WindowScrollEventListener l) {
		//this.scrollEventListeners.add( l );
		this.eventManager.addEventListener(WindowScrollEventListener.class, l);
	}
	public void removeScrollEventListener(WindowScrollEventListener l) {
		//this.scrollEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowScrollEventListener.class, l);
	}
	
	public void addMousePositionEventListener(WindowMousePositionEventListener l) {
		//this.mousePositionEventListeners.add( l );
		this.eventManager.addEventListener(WindowMousePositionEventListener.class, l);
	}
	public void removeMousePositionEventListener(WindowMousePositionEventListener l) {
		//this.mousePositionEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowMousePositionEventListener.class, l);
	}
	
	public void addCharEventListener(WindowCharEventListener l) {
		//this.charEventListeners.add( l );
		this.eventManager.addEventListener(WindowCharEventListener.class, l);
	}
	public void removeCharEventListener(WindowCharEventListener l) {
		//this.charEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowCharEventListener.class, l);
	}
	
	public void addFramebufferSizeEventListener(WindowFramebufferSizeEventListener l) {
		//this.framebufferSizeEventListeners.add( l );
		this.eventManager.addEventListener(WindowFramebufferSizeEventListener.class, l);
	}
	public void removeFramebufferSizeEventListener(WindowFramebufferSizeEventListener l) {
		//this.framebufferSizeEventListeners.remove( l );
		this.eventManager.removeEventListener(WindowFramebufferSizeEventListener.class, l);
	}
	
}
