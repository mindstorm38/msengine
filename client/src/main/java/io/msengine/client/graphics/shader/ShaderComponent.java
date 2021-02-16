package io.msengine.client.graphics.shader;

/**
 * <p>A component of a {@link ShaderProgram}, objects implementing
 * the {@link #close()} method should not throw exceptions (avoid
 * runtime exceptions) while closing.</p>
 * <p>A component can be closed manually, but the program can also
 * call {@link #close()} when closing itself.</p>
 */
public abstract class ShaderComponent implements AutoCloseable {
	
	private ShaderProgram program;
	
	protected void setup(ShaderProgram program) {
		this.program = program;
	}
	
	public ShaderProgram getProgram() {
		return this.program;
	}
	
	/**
	 * @return True if there's no program attached to this component, or if the attached program is currently used.
	 */
	public boolean isProgramUsed() {
		return this.program == null || this.program.isUsed();
	}
	
	@Override
	public abstract void close();
	
}
