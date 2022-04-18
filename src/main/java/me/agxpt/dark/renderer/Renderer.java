package me.agxpt.dark.renderer;

import me.agxpt.dark.renderer.defaultImpl.GL;
import me.agxpt.dark.renderer.interfaces.IGL;

/**
 * The renderer implementation.
 * Replace the default implementation with your implementation.
 */
public class Renderer {
    /**
     * The OpenGL implementation
     */
    public static IGL gl = new GL();
}
