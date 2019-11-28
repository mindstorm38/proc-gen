package fr.theorozier.procgen.world.biome;

public class BiomeWeatherRange {
	
	private final float tempInf;
	private final float tempSup;
	
	private final float humidityInf;
	private final float humiditySup;
	
	private final float typicalTemp;
	private final float typicalHumidity;
	
	public BiomeWeatherRange(float tempInf, float tempSup, float humidityInf, float humiditySup) {
		
		this.tempInf = tempInf;
		this.tempSup = tempSup;
		
		this.humidityInf = humidityInf;
		this.humiditySup = humiditySup;
		
		this.typicalTemp = (tempInf + tempSup) / 2f;
		this.typicalHumidity = (tempInf + tempSup) / 2f;
		
	}
	
	public float getTempInf() {
		return this.tempInf;
	}
	
	public float getTempSup() {
		return this.tempSup;
	}
	
	public float getHumidityInf() {
		return this.humidityInf;
	}
	
	public float getHumiditySup() {
		return this.humiditySup;
	}
	
	public float getTypicalTemp() {
		return this.typicalTemp;
	}
	
	public float getTypicalHumidity() {
		return this.typicalHumidity;
	}
	
}
