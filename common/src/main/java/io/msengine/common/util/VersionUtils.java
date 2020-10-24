package io.msengine.common.util;

import io.sutil.JavaVersion;

@Deprecated
public class VersionUtils {

	public static void checkMinimumJavaVersion(JavaVersion version) {
		
		if ( JavaVersion.getCurrentJavaVersion().isSmallerThan( version ) )
			throw new IllegalStateException( "Invalid java version, must be at least " + version.toString() );
		
	}
	
}
