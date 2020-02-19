package fr.theorozier.procgen.common.util.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteDataOutputStream extends DataOutputStream {

    public ByteDataOutputStream(int size) {
        super(new ByteArrayOutputStream(size));
    }

    public ByteDataOutputStream() {
        super(new ByteArrayOutputStream());
    }

    public ByteArrayOutputStream getByteStream() {
        return (ByteArrayOutputStream) this.out;
    }

    public void reset() {

        this.getByteStream().reset();
        this.written = 0;

    }

}
