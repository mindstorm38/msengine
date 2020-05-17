package io.msengine.common.util.noise;

public class OctaveSimplexNoise {
	
	private final SeedSimplexNoise[] noises;
	private final float persistance;
	private final float lacunarity;
	
	public OctaveSimplexNoise(long seed, int octavesCount, float persistance, float lacunarity) {
		
		if (octavesCount < 1)
			throw new IllegalArgumentException("Invalid octaves count, must be at least 1.");
		
		this.noises = new SeedSimplexNoise[octavesCount];
		this.persistance = persistance;
		this.lacunarity = lacunarity;
		
		for (int i = 0; i < octavesCount; i++)
			this.noises[i] = new SeedSimplexNoise(seed);
		
	}
	
	public float noise(float x, float y, float scale) {
	
		float noise = 0f;
		float freq = scale;
		float ampl = 1f;
		
		float totalAmpl = 0f;
		
		for (SeedSimplexNoise simplex : this.noises) {
		
			noise += simplex.noise(x * freq, y * freq) * ampl;
			totalAmpl += ampl;
			
			ampl *= this.persistance;
			freq *= this.lacunarity;
			
		}
		
		return noise / totalAmpl;
	
	}
	
	public float noise(float x, float y, float z, float scale) {
		
		float noise = 0f;
		float freq = scale;
		float ampl = 1f;
		
		float totalAmpl = 0f;
		
		for (SeedSimplexNoise simplex : this.noises) {
			
			noise += simplex.noise(x * freq, y * freq, z * freq) * ampl;
			totalAmpl += ampl;
			
			ampl *= this.persistance;
			freq *= this.lacunarity;
			
		}
		
		return noise / totalAmpl;
		
	}

}
