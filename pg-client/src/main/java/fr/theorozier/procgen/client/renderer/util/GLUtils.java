package fr.theorozier.procgen.client.renderer.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.*;

public final class GLUtils {
	
	/**
	 * Remove a bytes segment of specified length and offset (all in bytes) in an GL buffer and optionnaly replace
	 * removed bytes segment by a replacement.
	 * @param bufferName The buffer's name to splice.
	 * @param tempBufferName Temporary buffer's name used for temporary copy origin buffer into and copy back segments to the original one.
	 * @param offset Bytes offset in the GL buffer.
	 * @param length Number of bytes to remove at this offset.
	 * @param replacement Optional (can be null) replacement to place at the offset after bytes are removed.
	 * @param elementShift Bit shift to apply on replacement capacity to calculate capacity in bytes.
	 */
	public static void glSpliceBuffer(int bufferName, int tempBufferName, int offset, int length, Buffer replacement, int elementShift) {
		
		glBindBuffer(GL_COPY_READ_BUFFER, bufferName);
		
		int currentSize = glGetBufferParameteri(GL_COPY_READ_BUFFER, GL_BUFFER_SIZE);
		int srcNdOff = offset + length;
		
		if (srcNdOff >= currentSize) {
			throw new IndexOutOfBoundsException("'offset + length' is not shorter than buffer size.");
		}
		
		int replaceSize = replacement == null ? 0 : (replacement.remaining() << elementShift);
		
		if (replaceSize != 0 || length != 0) {
			
			if (length == replaceSize) {
				
				glBindBuffer(GL_COPY_WRITE_BUFFER, bufferName);
				nglBufferSubData(GL_COPY_WRITE_BUFFER, offset, length, getBufferMemAddress(replacement));
				
			} else {
				
				int usage = glGetBufferParameteri(GL_COPY_READ_BUFFER, GL_BUFFER_USAGE);
				
				glBindBuffer(GL_COPY_WRITE_BUFFER, tempBufferName);
				glBufferData(GL_COPY_WRITE_BUFFER, currentSize, GL_STATIC_COPY);
				glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, currentSize);
				
				glBindBuffer(GL_COPY_READ_BUFFER, tempBufferName);
				glBindBuffer(GL_COPY_WRITE_BUFFER, bufferName);
				glBufferData(GL_COPY_WRITE_BUFFER, currentSize - length + replaceSize, usage);
				
				if (offset > 0) {
					glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, offset);
				}
				
				if (offset < currentSize) {
					glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, srcNdOff, offset + replaceSize, currentSize - srcNdOff);
				}
				
				if (replaceSize != 0) {
					nglBufferSubData(GL_COPY_WRITE_BUFFER, offset, replaceSize, getBufferMemAddress(replacement));
				}
				
			}
			
			glBindBuffer(GL_COPY_WRITE_BUFFER, 0);
			
		}
		
		glBindBuffer(GL_COPY_READ_BUFFER, 0);
		
	}
	
	public static void glSpliceIntBuffer(int bufferPtr, int tempBufferPtr, int offset, int length, IntBuffer replacement) {
		glSpliceBuffer(bufferPtr, tempBufferPtr, offset, length, replacement, 2);
	}
	
	public static void glSpliceFloatBuffer(int bufferPtr, int tempBufferPtr, int offset, int length, FloatBuffer replacement) {
		glSpliceBuffer(bufferPtr, tempBufferPtr, offset, length, replacement, 2);
	}
	
	public static long getBufferMemAddress(Buffer buffer) {
		return MemoryUtil.memAddress0(buffer) + buffer.position();
	}
	
}
