package net.jackofalltrades.idea;

/**
 * Exception identifying the when an IntelliJ build number is invalid.
 *
 * @author bhandy
 */
public class IntellijBuildVersionFormatException extends RuntimeException {

    public IntellijBuildVersionFormatException(String message) {
        super(message);
    }

}
