package me.agxpt.dark.renderer.interfaces;

import me.agxpt.dark.common.types.IDisposable;

public interface ITexture extends IDisposable {
    /**
     * Binds this texture to the default slot.
     *
     * @return This texture.
     */
    ITexture bind();

    /**
     * Binds this texture to the specified slot.
     *
     * @param slot The slot to bind to.
     * @return This texture.
     */
    ITexture bind(int slot);

    /**
     * Gets the last bound slot.
     *
     * @return The last bound slot.
     */
    int getSlot();
}
