package io.msengine.common.util.noise;

public class OctaveSimplexNoise {
	
	private final SeedSimplexNoise[] noises;
	private final float persistance;
	private final float lacunarity;
	
	public OctaveSimplexNoise(long seed, int octavesCount, float persistance, float lacunarity) {
		
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
		
		for (SeedSimplexNoise simplex : this.noises) {
		
			noise += simplex.normnoise(x * freq, y * freq) * ampl;
			ampl *= this.persistance;
			freq *= this.lacunarity;
			
		}
		
		return noise;
	
	}

}
