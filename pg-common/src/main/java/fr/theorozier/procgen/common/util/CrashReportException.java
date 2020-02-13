package fr.theorozier.procgen.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Special exception catched by main loop for better crash description.
 *
 * @author Theo Rozier
 *
 */
public class CrashReportException extends RuntimeException {

    public static class Builder {

        private final List<String> lines = new ArrayList<>();
        private Throwable cause = null;

        public Builder caused(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public Builder line(String line) {
            this.lines.add(line);
            return this;
        }

        public CrashReportException build() {
            return new CrashReportException(String.join("\n", this.lines), this.cause);
        }

    }

    private CrashReportException(String message, Throwable cause) {
        super(message, cause);
    }

}
