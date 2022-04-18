package me.agxpt.dark.renderer.interfaces;

import me.agxpt.dark.common.types.IDisposable;
import me.agxpt.dark.common.types.IM4f;
import me.agxpt.dark.common.types.IV2d;

public interface IShader extends IDisposable {
    /**
     * Binds the shader.
     */
    void bind();

    /**
     * Sets the value of a uniform.
     *
     * @param name The uniform name.
     * @param v    The int value to set.
     */
    void set(String name, int v);

    /**
     * Sets the value of a uniform.
     *
     * @param name The uniform name.
     * @param v    The float value to set.
     */
    void set(String name, double v);

    /**
     * Sets the value of a uniform.
     *
     * @param name The uniform name.
     * @param x    The x value of the vector.
     * @param y    The y value of the vector.
     */
    void set(String name, double x, double y);

    /**
     * Sets the value of a uniform.
     *
     * @param name The uniform name.
     * @param v    The vector2 to set.
     */
    void set(String name, IV2d v);

    /**
     * Sets the value of a uniform.
     *
     * @param name The uniform name.
     * @param mat  The 4x4 matrix to set.
     */
    void set(String name, IM4f mat);

    /**
     * Sets the value of a uniform.
     *
     * @param name    The uniform name.
     * @param texture The texture to set.
     */
    void set(String name, ITexture texture);


    /**
     * Sets the default values of the shader uniforms.
     */
    void setDefaults();
}
