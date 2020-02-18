package fr.theorozier.procgen.common.util.io;

import java.io.DataOutputStream;

public class ByteDataOutputStream extends DataOutputStream {

    public ByteDataOutputStream(int size) {
        super(new ByteOutputStream(size));
    }

    public ByteDataOutputStream() {
        super(new ByteOutputStream());
    }

    public ByteOutputStream getByteStream() {
        return (ByteOutputStream) this.out;
    }

    public void reset() {

        this.getByteStream().reset();
        this.written = 0;

    }

}
