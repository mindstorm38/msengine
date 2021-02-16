package io.msengine.common.util.classfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Iterator;

final class ClassLoaderPath implements Path {
	
	static ClassLoaderPath checkPath(Path path) {
		if (path.getClass() != ClassLoaderPath.class) {
			throw new IllegalArgumentException("Invalid path for manipulation of a class loader file system.");
		} else {
			return (ClassLoaderPath) path;
		}
	}
	
	// Class //
	
	private final ClassLoaderFileSystem system;
	private final String absolute;
	private final String effective;
	
	public ClassLoaderPath(ClassLoaderFileSystem system, String absolute, String effective) {
		this.system = system;
		this.absolute = absolute;
		this.effective = effective;
	}
	
	public ClassLoaderPath(ClassLoaderFileSystem system, String absolute) {
		this(system, absolute, null);
	}
	
	// Customs //
	
	public boolean isRootPath() {
		return this == this.system.getPathRoot();
	}
	
	private String getEffective() {
		return this.effective == null ? this.absolute : this.effective;
	}
	
	// Overrides //
	
	@Override
	public FileSystem getFileSystem() {
		return this.system;
	}
	
	@Override
	public boolean isAbsolute() {
		return this.effective == null;
	}
	
	@Override
	public Path getRoot() {
		return this.isRootPath() ? this : this.system.getPathRoot();
	}
	
	@Override
	public Path getFileName() {
		if (this.isRootPath()) {
			return null;
		} else if (this.effective == null) {
			return new ClassLoaderPath(this.system, this.absolute, this.absolute.substring(this.absolute.lastIndexOf('/')));
		} else {
			int lastIdx = this.effective.lastIndexOf('/');
			return lastIdx == -1 ? this : new ClassLoaderPath(this.system, this.absolute, this.effective.substring(lastIdx));
		}
	}
	
	@Override
	public Path getParent() {
		if (this.isRootPath()) {
			return null;
		} else {
			String newAbsolute = this.absolute.substring(0, this.absolute.lastIndexOf('/'));
			if (this.effective == null) {
				return new ClassLoaderPath(this.system, newAbsolute);
			} else {
				int effLastIdx = this.effective.lastIndexOf('/');
				return effLastIdx == -1 ? null : new ClassLoaderPath(this.system, newAbsolute, this.effective.substring(0, effLastIdx));
			}
		}
	}
	
	@Override
	public int getNameCount() {
		return this.effective.split("/").length;
	}
	
	@Override
	public Path getName(int index) {
		
		/*if (this.effective == null) {
			
			String[] names = this.absolute.split("/");
			String name = names[index];
			
			StringBuilder newAbsolute = new StringBuilder("/");
			for (int i = 0; i < index; ++i) {
				if (i != 0) newAbsolute.append('/');
				newAbsolute.append(names[i]);
			}
			
			return new ClassLoaderPath(this.system, newAbsolute.toString(), name);
			
		} else {
		
		
		
		}
		
		String[] absNames = this.absolute.split("/");
		String[] names = this.getEffective().split("/");
		
		if (index < 0 || index >= names.length) {
			throw new IndexOutOfBoundsException();
		}
		
		String name = names[index];
		return new ClassLoaderPath(this.system, );*/
		
		return null;
		
	}
	
	@Override
	public Path subpath(int beginIndex, int endIndex) {
		return null;
	}
	
	@Override
	public boolean startsWith(Path other) {
		return false;
	}
	
	@Override
	public boolean startsWith(String other) {
		return false;
	}
	
	@Override
	public boolean endsWith(Path other) {
		return false;
	}
	
	@Override
	public boolean endsWith(String other) {
		return false;
	}
	
	@Override
	public Path normalize() {
		return this;
	}
	
	@Override
	public Path resolve(Path other) {
		return null;
	}
	
	@Override
	public Path resolve(String other) {
		return null;
	}
	
	@Override
	public Path resolveSibling(Path other) {
		return null;
	}
	
	@Override
	public Path resolveSibling(String other) {
		return null;
	}
	
	@Override
	public Path relativize(Path other) {
		return null;
	}
	
	@Override
	public URI toUri() {
		return null;
	}
	
	@Override
	public Path toAbsolutePath() {
		return this.effective == null ? this : new ClassLoaderPath(this.system, this.absolute);
	}
	
	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return null;
	}
	
	@Override
	public File toFile() {
		return null;
	}
	
	@Override
	public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
		return null;
	}
	
	@Override
	public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
		return null;
	}
	
	@Override
	public Iterator<Path> iterator() {
		return null;
	}
	
	@Override
	public int compareTo(Path other) {
		return 0;
	}
	
	@Override
	public String toString() {
		return "ClassLoaderPath<" + this.effective + ">";
	}
	
}
