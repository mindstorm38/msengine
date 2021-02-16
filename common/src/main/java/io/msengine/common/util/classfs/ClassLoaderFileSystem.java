package io.msengine.common.util.classfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ClassLoaderFileSystem extends FileSystem {
	
	private final ClassLoader classLoader;
	private final ClassLoaderPath root;
	
	private ClassLoaderFileSystem(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.root = new ClassLoaderPath(this, "/");
	}
	
	public static ClassLoaderFileSystem newFileSystem(Class<?> clazz) {
		return new ClassLoaderFileSystem(Objects.requireNonNull(clazz.getClassLoader(), "Invalid class, do not have a class loader."));
	}
	
	// Specific methods //
	
	ClassLoaderPath getPathRoot() {
		return this.root;
	}
	
	private static void foreachSubPath(String rawPath, Consumer<String> subPathConsumer) {
		for (String part : rawPath.split("/")) {
			if (!part.isEmpty()) {
				subPathConsumer.accept(part);
			}
		}
	}
	
	// Overridden methods //
	
	@Override
	public FileSystemProvider provider() {
		return ClassLoaderFileSystemProvider.INSTANCE;
	}
	
	@Override
	public void close() throws IOException { }
	
	@Override
	public boolean isOpen() {
		return true;
	}
	
	@Override
	public boolean isReadOnly() {
		return true;
	}
	
	@Override
	public String getSeparator() {
		return "/";
	}
	
	@Override
	public Iterable<Path> getRootDirectories() {
		return Collections.singleton(this.root);
	}
	
	@Override
	public Iterable<FileStore> getFileStores() {
		return null;
	}
	
	@Override
	public Set<String> supportedFileAttributeViews() {
		return null;
	}
	
	@Override
	public Path getPath(String first, String... more) {
		
		StringBuilder builder = new StringBuilder();
		
		foreachSubPath(first, builder::append);
		for (String moreRaw : more) {
			foreachSubPath(moreRaw, builder::append);
		}
		
		return new ClassLoaderPath(this, builder.toString());
		
	}
	
	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		return null;
	}
	
	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		return null;
	}
	
	@Override
	public WatchService newWatchService() throws IOException {
		throw new ReadOnlyFileSystemException();
	}
	
}
