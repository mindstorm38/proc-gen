package fr.theorozier.procgen.common.world.util.exception;

public class InvalidDimensionMetadataException extends IllegalStateException {

    public InvalidDimensionMetadataException() {}

    public InvalidDimensionMetadataException(String s) {
        super(s);
    }

    public InvalidDimensionMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDimensionMetadataException(Throwable cause) {
        super(cause);
    }

}
