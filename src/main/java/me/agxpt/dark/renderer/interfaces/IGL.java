package me.agxpt.dark.renderer.interfaces;

public interface IGL {
    /**
     * Bind a vertex array object.
     *
     * @param array The vertex array object.
     */
    void bindVAO(int array);

    /**
     * Binds an array buffer object.
     *
     * @param buffer The buffer object.
     */
    void bindVBO(int buffer);

    /**
     * Binds an element array buffer object.
     *
     * @param buffer The buffer object.
     */
    void bindIBO(int buffer);

    /**
     * Binds a texture.
     *
     * @param texture The texture object.
     * @param slot    The texture slot.
     */
    void bindTexture(int texture, int slot);

    /**
     * Binds a texture to default slot.
     *
     * @param texture The texture object.
     */
    default void bindTexture(int texture) {
        bindTexture(texture, 0);
    }
}
