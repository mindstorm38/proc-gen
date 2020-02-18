package fr.theorozier.procgen.common.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteOutputStream extends ByteArrayOutputStream {

    public ByteOutputStream() { }

    public ByteOutputStream(int size) {
        super(size);
    }

    public synchronized void writeTo(OutputStream other) throws IOException {
        other.write(this.buf, 0, this.size());
    }

}
