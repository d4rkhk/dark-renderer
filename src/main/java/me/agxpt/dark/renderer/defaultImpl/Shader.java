package me.agxpt.dark.renderer.defaultImpl;

import me.agxpt.dark.common.types.IM4f;
import me.agxpt.dark.common.types.IV2d;
import me.agxpt.dark.renderer.interfaces.IShader;
import me.agxpt.dark.renderer.interfaces.ITexture;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;

/**
 * Default implementation of {@link IShader}
 */
public class Shader implements IShader {
    private final int id;
    private final Map<String, Integer> uniformLocations = new HashMap<>();

    public Shader(String vertexSrc, String fragmentSrc) {
        int vert = createShader(vertexSrc, ShaderType.Vertex);
        int frag = createShader(fragmentSrc, ShaderType.Fragment);

        id = glCreateProgram();

        glAttachShader(id, vert);
        glAttachShader(id, frag);

        linkProgram();

        glDetachShader(id, vert);
        glDeleteShader(vert);

        glDetachShader(id, frag);
        glDeleteShader(frag);
    }

    /**
     * Creates a new shader.
     *
     * @param src  The shader source string.
     * @param type The shader type.
     * @return The shader id.
     */
    private int createShader(String src, ShaderType type) {
        int sid = glCreateShader(type.gl);
        if (sid == 0) {
            throw new RuntimeException("Error creating " + type.name + " shader.");
        }

        glShaderSource(sid, src);
        glCompileShader(sid);

        if (glGetShaderi(sid, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling " + type.name + " shader: " + glGetShaderInfoLog(sid, 1024));
        }

        return sid;
    }

    /**
     * Links the shader program and checks for errors.
     */
    private void linkProgram() {
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(id, 1024));
        }

        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    @Override
    public void dispose() {
        glDeleteProgram(id);
    }

    @Override
    public void bind() {
        glUseProgram(id);
    }

    /**
     * Finds the location of the uniform with the specified name.
     *
     * @param name The uniform name.
     * @return The location of the uniform with the specified name.
     */
    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.get(name);

        int location = glGetUniformLocation(id, name);
        uniformLocations.put(name, location);
        return location;
    }

    @Override
    public void set(String name, int v) {
        glUniform1i(getLocation(name), v);
    }

    @Override
    public void set(String name, double v) {
        glUniform1f(getLocation(name), (float) v);
    }

    @Override
    public void set(String name, double x, double y) {
        glUniform2f(getLocation(name), (float) x, (float) y);
    }

    @Override
    public void set(String name, IV2d v) {
        glUniform2f(getLocation(name), (float) v.x(), (float) v.y());
    }

    @Override
    public void set(String name, IM4f mat) {
        glUniformMatrix4fv(getLocation(name), false, mat.getAsBuffer());
    }

    @Override
    public void set(String name, ITexture texture) {
        glUniform1i(getLocation(name), texture.getSlot());
    }


    @Override
    public void setDefaults() {
    }

    /**
     * OpenGL shader types.
     */
    enum ShaderType {
        Vertex("vertex", GL_VERTEX_SHADER),
        Fragment("fragment", GL_FRAGMENT_SHADER);
        public final String name;
        public final int gl;

        ShaderType(String name, int gl) {
            this.name = name;
            this.gl = gl;
        }
    }
}
