package me.agxpt.dark.renderer.defaultImpl;

import me.agxpt.dark.renderer.interfaces.IGL;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

/**
 * Default implementation of {@link IGL}
 */
public class GL implements IGL {
    @Override
    public void bindVAO(int array) {
        glBindVertexArray(array);
    }

    @Override
    public void bindVBO(int buffer) {
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
    }

    @Override
    public void bindIBO(int buffer) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer);
    }

    @Override
    public void bindTexture(int texture, int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, texture);
    }
}
