package io.msengine.client.audio;

import org.lwjgl.openal.ALUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.openal.ALC11.*;

public class AudioDevice {
	
	private static boolean hasEnumerateAllExt() {
		return alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT");
	}
	
	private static boolean hasEnumerateExt() {
		return alcIsExtensionPresent(0L, "ALC_ENUMERATION_EXT");
	}
	
	/**
	 * @return The list of audio devices, <b>empty</b> if the OpenAL implementation don't support enumeration of devices.
	 */
	public static List<AudioDevice> getDevices() {
		if (hasEnumerateAllExt()) {
			return fromNamesSafe(ALUtil.getStringList(0L, ALC_ALL_DEVICES_SPECIFIER));
		} else if (hasEnumerateExt()) {
			return fromNamesSafe(ALUtil.getStringList(0L, ALC_DEVICE_SPECIFIER));
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * @return The default audio device, or <b>null</b> if the OpenAL implementation don't support enumeration of devices.
	 */
	public static AudioDevice getDefaultDevice() {
		if (hasEnumerateAllExt()) {
			return fromNameSafe(alcGetString(0L, ALC_DEFAULT_ALL_DEVICES_SPECIFIER));
		} else if (hasEnumerateExt()) {
			return fromNameSafe(alcGetString(0L, ALC_DEFAULT_DEVICE_SPECIFIER));
		} else {
			return null;
		}
	}
	
	private static List<AudioDevice> fromNamesSafe(List<String> names) {
		return names == null ? Collections.emptyList() : names.stream().map(AudioDevice::new).collect(Collectors.toList());
	}
	
	private static AudioDevice fromNameSafe(String name) {
		return name == null ? null : new AudioDevice(name);
	}
	
	private final String name;
	
	private AudioDevice(String name) {
		this.name = Objects.requireNonNull(name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isDefault() {
		String def;
		if (hasEnumerateAllExt()) {
			def = alcGetString(0L, ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		} else if (hasEnumerateExt()) {
			def = alcGetString(0L, ALC_DEFAULT_DEVICE_SPECIFIER);
		} else {
			return false;
		}
		return this.name.equals(def);
	}
	
	@Override
	public String toString() {
		return "AudioDevice<" + this.name + ">";
	}
	
}
