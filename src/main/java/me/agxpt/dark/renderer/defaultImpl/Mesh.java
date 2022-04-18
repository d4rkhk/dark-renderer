package me.agxpt.dark.renderer.defaultImpl;

import me.agxpt.dark.common.types.IColor;
import me.agxpt.dark.common.types.IV2d;
import me.agxpt.dark.common.types.IV3d;
import me.agxpt.dark.renderer.Renderer;
import me.agxpt.dark.renderer.interfaces.IMesh;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh implements IMesh {
    private final int primitiveVerticesSize;
    private final int vao, vbo, ibo;
    private final DrawMode drawMode;
    private int verticesCapacity;
    private long vertices;
    private int indicesCapacity;
    private long indices;
    private long verticesI;
    private boolean building;
    private int vertexI, indicesCount;

    /**
     * Creates a new mesh.
     *
     * @param drawMode   The draw shape.
     * @param attributes The shader attributes.
     */
    public Mesh(DrawMode drawMode, Attrib... attributes) {
        this.drawMode = drawMode;

        int stride = 0;
        for (Attrib attrib : attributes) stride += attrib.size;

        this.primitiveVerticesSize = stride * 3;

        verticesCapacity = primitiveVerticesSize * 256 * 4;
        vertices = nmemAllocChecked(verticesCapacity);

        indicesCapacity = 3 * 512 * 4;
        indices = nmemAllocChecked(indicesCapacity);

        vao = glGenVertexArrays();
        Renderer.gl.bindVAO(vao);

        vbo = glGenBuffers();
        Renderer.gl.bindVBO(vbo);

        ibo = glGenBuffers();
        Renderer.gl.bindIBO(ibo);

        int offset = 0;
        for (int i = 0; i < attributes.length; i++) {
            Attrib attrib = attributes[i];

            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attrib.count, attrib.glEnum, false, stride, offset);

            offset += attrib.size;
        }

        Renderer.gl.bindVAO(0);
        Renderer.gl.bindVBO(0);
        Renderer.gl.bindIBO(0);
    }

    @Override
    public void dispose() {
        nmemFree(vertices);
        nmemFree(indices);
    }

    /**
     * Resets the buffers and starts the mesh.
     */
    public void begin() {
        if (building) throw new IllegalStateException("Mesh.begin() called while already building.");

        verticesI = vertices;
        vertexI = 0;
        indicesCount = 0;

        building = true;
    }

    /**
     * Puts an unsigned byte.
     *
     * @param v The unsigned byte value.
     */
    public Mesh uByte(int v) {
        memPutByte(verticesI, (byte) v);

        verticesI++;
        return this;
    }

    /**
     * Puts a float.
     *
     * @param v The float value.
     */
    public Mesh float_(double v) {
        memPutFloat(verticesI, (float) v);

        verticesI += 4;
        return this;
    }

    /**
     * Puts a 2d float vector.
     *
     * @param x The x of vector.
     * @param y The y of vector.
     */
    public Mesh vec2(double x, double y) {
        memPutFloat(verticesI, (float) x);
        memPutFloat(verticesI + 4, (float) y);

        verticesI += 8;
        return this;
    }

    /**
     * Puts a 2d float vector.
     *
     * @param vec The vector.
     */
    public Mesh vec2(IV2d vec) {
        memPutFloat(verticesI, (float) vec.x());
        memPutFloat(verticesI + 4, (float) vec.y());

        verticesI += 8;
        return this;
    }

    /**
     * Puts a 3d float vector.
     *
     * @param x The x of vector.
     * @param y The y of vector.
     * @param z The z of vector.
     */
    public Mesh vec3(double x, double y, double z) {
        memPutFloat(verticesI, (float) x);
        memPutFloat(verticesI + 4, (float) y);
        memPutFloat(verticesI + 8, (float) z);

        verticesI += 12;
        return this;
    }

    /**
     * Puts a 2d float vector.
     *
     * @param vec The vector.
     */
    public Mesh vec3(IV3d vec) {
        memPutFloat(verticesI, (float) vec.x());
        memPutFloat(verticesI + 4, (float) vec.y());
        memPutFloat(verticesI + 8, (float) vec.z());

        verticesI += 12;
        return this;
    }

    /**
     * Puts a 4d float vector.
     *
     * @param x The x of vector.
     * @param y The y of vector.
     * @param z The z of vector.
     * @param w The w of vector.
     */
    public Mesh vec4(double x, double y, double z, double w) {
        memPutFloat(verticesI, (float) x);
        memPutFloat(verticesI + 4, (float) y);
        memPutFloat(verticesI + 8, (float) z);
        memPutFloat(verticesI + 12, (float) w);

        verticesI += 16;
        return this;
    }

    /**
     * Puts a rgba color.
     *
     * @param color The color.
     */
    public Mesh color(IColor color) {
        memPutFloat(verticesI, (float) color.r());
        memPutFloat(verticesI + 4, (float) color.g());
        memPutFloat(verticesI + 8, (float) color.b());
        memPutFloat(verticesI + 12, (float) color.a());

        verticesI += 16;
        return this;
    }

    /**
     * End the current vertex.
     *
     * @return The vertex index.
     */
    public int next() {
        return vertexI++;
    }

    /**
     * Creates a line.
     *
     * @param i1 First vertex index.
     * @param i2 Second vertex index.
     */
    public void line(int i1, int i2) {
        long p = indices + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);

        indicesCount += 2;
        growIfNeeded();
    }

    /**
     * Creates a triangle.
     *
     * @param i1 First vertex index.
     * @param i2 Second vertex index.
     * @param i3 Third vertex index.
     */
    public void triangle(int i1, int i2, int i3) {
        long p = indices + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);
        memPutInt(p + 8, i3);

        indicesCount += 3;
        growIfNeeded();
    }

    /**
     * Creates a quad.
     *
     * @param i1 First vertex index.
     * @param i2 Second vertex index.
     * @param i3 Third vertex index.
     * @param i4 Fourth vertex index.
     */
    public void quad(int i1, int i2, int i3, int i4) {
        long p = indices + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);
        memPutInt(p + 8, i3);

        memPutInt(p + 12, i3);
        memPutInt(p + 16, i4);
        memPutInt(p + 20, i1);

        indicesCount += 6;
        growIfNeeded();
    }

    /**
     * Increases the buffer sizes if needed.
     */
    public void growIfNeeded() {
        // Vertices
        if ((vertexI + 1) * primitiveVerticesSize >= verticesCapacity) {
            verticesCapacity = verticesCapacity * 2;
            if (verticesCapacity % primitiveVerticesSize != 0)
                verticesCapacity += verticesCapacity % primitiveVerticesSize;

            long newVertices = nmemAllocChecked(verticesCapacity);
            memCopy(vertices, newVertices, verticesI - vertices);

            verticesI = newVertices + (verticesI - vertices);
            vertices = newVertices;
        }

        // Indices
        if (indicesCount * 4 >= indicesCapacity) {
            indicesCapacity = indicesCapacity * 2;
            if (indicesCapacity % 3 != 0) indicesCapacity += indicesCapacity % (3 * 4);

            long newIndices = nmemAllocChecked(indicesCapacity);
            memCopy(indices, newIndices, indicesCount * 4L);

            indices = newIndices;
        }
    }

    /**
     * Ends the mesh.
     */
    public void end() {
        if (!building) throw new IllegalStateException("Mesh.end() called while not building.");

        if (indicesCount > 0) {
            Renderer.gl.bindVBO(vbo);
            nglBufferData(GL_ARRAY_BUFFER, verticesI - vertices, vertices, GL_DYNAMIC_DRAW);
            Renderer.gl.bindVBO(0);

            Renderer.gl.bindIBO(ibo);
            nglBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesCount * 4L, indices, GL_DYNAMIC_DRAW);
            Renderer.gl.bindIBO(0);
        }

        building = false;
    }

    protected void beforeRender() {
    }

    protected void afterRender() {
    }

    @Override
    public void render() {
        if (building) end();

        if (indicesCount > 0) {
            beforeRender();
            Renderer.gl.bindVAO(vao);
            glDrawElements(drawMode.gl, indicesCount, GL_UNSIGNED_INT, 0);
            Renderer.gl.bindVAO(0);
            afterRender();
        }
    }

    /**
     * Shader vertex attributes.
     */
    public enum Attrib {
        UByte(1, GLType.UByte, false),
        Float(1, GLType.Float, false),
        Vec2(2, GLType.Float, false),
        Vec3(3, GLType.Float, false),
        Vec4(4, GLType.Float, false);

        public final int count, size, glEnum;
        public final boolean normalize;

        Attrib(int count, GLType type, boolean normalize) {
            this.count = count;
            this.size = count * type.size;
            this.glEnum = type.glEnum;
            this.normalize = normalize;
        }

        /**
         * OpenGL data types.
         */
        private enum GLType {
            Byte(1, GL_BYTE),
            UByte(1, GL_UNSIGNED_BYTE),
            Short(2, GL_SHORT),
            UShort(2, GL_UNSIGNED_SHORT),
            Int(4, GL_INT),
            UInt(4, GL_UNSIGNED_INT),
            Float(4, GL_FLOAT),
            Double(8, GL_DOUBLE);
            public final int size, glEnum;

            GLType(int size, int glEnum) {
                this.size = size;
                this.glEnum = glEnum;
            }
        }
    }

    /**
     * OpenGL draw modes.
     */
    public enum DrawMode {
        Lines(GL_LINES, 2),
        Triangles(GL_TRIANGLES, 3);

        public final int gl, indicesCount;

        DrawMode(int gl, int indicesCount) {
            this.gl = gl;
            this.indicesCount = indicesCount;
        }
    }
}
