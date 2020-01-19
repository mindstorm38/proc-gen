package fr.theorozier.procgen.common.world.gen;

public class WorldIncompatException extends Exception {
	
	public WorldIncompatException() {
	}
	
	public WorldIncompatException(String message) {
		super(message);
	}
	
	public WorldIncompatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WorldIncompatException(Throwable cause) {
		super(cause);
	}
	
	public WorldIncompatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
