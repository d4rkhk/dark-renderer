package me.agxpt.dark.renderer.defaultImpl;

import me.agxpt.dark.renderer.Renderer;
import me.agxpt.dark.renderer.interfaces.IShader;
import me.agxpt.dark.renderer.interfaces.ITexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_UNPACK_IMAGE_HEIGHT;
import static org.lwjgl.opengl.GL12C.GL_UNPACK_SKIP_IMAGES;
import static org.lwjgl.opengl.GL30C.*;

/**
 * Default implementation of {@link IShader}
 */
public class Texture implements ITexture {
    protected final int id;
    protected final int width, height;
    protected final Format format;
    protected final MinFilter minFilter;
    private int lastSlot = 0;

    /**
     * Creates a new texture.
     *
     * @param width     The texture width.
     * @param height    The texture height.
     * @param format    The texture format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     */
    protected Texture(int width, int height, Format format, MinFilter minFilter, MagFilter magFilter) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.minFilter = minFilter;

        id = glGenTextures();
        Renderer.gl.bindTexture(id);

        glPixelStorei(GL_UNPACK_SWAP_BYTES, GL_FALSE);
        glPixelStorei(GL_UNPACK_LSB_FIRST, GL_FALSE);
        glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
        glPixelStorei(GL_UNPACK_IMAGE_HEIGHT, 0);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
        glPixelStorei(GL_UNPACK_SKIP_IMAGES, 0);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter.gl);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter.gl);
    }

    /**
     * Creates a new texture from a {@link ByteBuffer}.
     *
     * @param width     The texture width.
     * @param height    The texture height.
     * @param buffer    The texture buffer.
     * @param format    The texture format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     */
    public Texture(int width, int height, ByteBuffer buffer, Format format, MinFilter minFilter, MagFilter magFilter) {
        this(width, height, format, minFilter, magFilter);
        upload(buffer);
    }

    /**
     * Creates a new texture from a {@link FloatBuffer}.
     *
     * @param width     The texture width.
     * @param height    The texture height.
     * @param buffer    The texture buffer.
     * @param format    The texture format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     */
    public Texture(int width, int height, FloatBuffer buffer, Format format, MinFilter minFilter, MagFilter magFilter) {
        this(width, height, format, minFilter, magFilter);
        upload(buffer);
    }

    /**
     * Creates a new texture from a byte array.
     *
     * @param width     The texture width.
     * @param height    The texture height.
     * @param data      The texture data.
     * @param format    The texture format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     */
    public Texture(int width, int height, byte[] data, Format format, MinFilter minFilter, MagFilter magFilter) {
        this(width, height, BufferUtils.createByteBuffer(data.length).put(data), format, minFilter, magFilter);
    }

    /**
     * Creates a new texture from a float array.
     *
     * @param width     The texture width.
     * @param height    The texture height.
     * @param data      The texture data.
     * @param format    The texture format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     */
    public Texture(int width, int height, float[] data, Format format, MinFilter minFilter, MagFilter magFilter) {
        this(width, height, BufferUtils.createFloatBuffer(data.length).put(data), format, minFilter, magFilter);
    }

    /**
     * Creates a new texture from a file.
     *
     * @param fileName  The file path.
     * @param format    The pixel format.
     * @param minFilter The minifying filter.
     * @param magFilter The magnifying filter.
     * @return The created texture.
     */
    public static Texture fromFile(String fileName, Format format, MinFilter minFilter, MagFilter magFilter) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer file = stack.ASCII(fileName, true);
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer texture = STBImage.stbi_load(file, width, height, channels, format.channels);
            if (texture == null) return null;
            Texture textureObj = new Texture(width.get(), height.get(), texture, format, minFilter, magFilter);
            STBImage.stbi_image_free(texture);
            return textureObj;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Uploads a byte texture.
     *
     * @param buffer The texture data buffer.
     */
    protected void upload(ByteBuffer buffer) {
        if (buffer != null) buffer.rewind();

        Renderer.gl.bindTexture(id);
        glTexImage2D(GL_TEXTURE_2D, 0, format.internalFormat, width, height, 0, format.format, GL_UNSIGNED_BYTE, buffer);
        if (minFilter.mipMap) glGenerateMipmap(GL_TEXTURE_2D);
    }

    /**
     * Uploads a float texture.
     *
     * @param buffer The texture data buffer.
     */
    protected void upload(FloatBuffer buffer) {
        if (buffer != null) buffer.rewind();

        Renderer.gl.bindTexture(id);
        glTexImage2D(GL_TEXTURE_2D, 0, format.internalFormat, width, height, 0, format.format, GL_FLOAT, buffer);
        if (minFilter.mipMap) glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void dispose() {
        glDeleteTextures(id);
    }

    @Override
    public Texture bind() {
        Renderer.gl.bindTexture(id, lastSlot = 0);
        return this;
    }

    @Override
    public Texture bind(int slot) {
        Renderer.gl.bindTexture(id, lastSlot = slot);
        return this;
    }

    @Override
    public int getSlot() {
        return lastSlot;
    }

    /**
     * Texture pixel formats.
     */
    public enum Format {
        R(GL_RED, GL_RED, 1),
        RGB(GL_RGB, GL_RGB, 3),
        RGBA(GL_RGBA, GL_RGBA, 4),

        FloatR(GL_RED, GL_R32F, 1),
        FloatRGB(GL_RGB, GL_RGB32F, 3),
        FloatRGBA(GL_RGBA, GL_RGBA32F, 4);
        public final int format, internalFormat, channels;

        Format(int format, int internalFormat, int channels) {
            this.internalFormat = internalFormat;
            this.format = format;
            this.channels = channels;
        }
    }

    /**
     * Texture minification filters.
     */
    public enum MinFilter {
        Nearest(GL_NEAREST, false),
        Linear(GL_LINEAR, false),
        MipMapLinear(GL_LINEAR_MIPMAP_LINEAR, true);
        public final int gl;
        public final boolean mipMap;

        MinFilter(int gl, boolean mipMap) {
            this.gl = gl;
            this.mipMap = mipMap;
        }
    }

    /**
     * Texture magnification filters.
     */
    public enum MagFilter {
        Nearest(GL_NEAREST),
        Linear(GL_LINEAR);
        public final int gl;

        MagFilter(int gl) {
            this.gl = gl;
        }
    }
}
