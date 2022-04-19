package me.agxpt.dark.renderer.defaultImpl;

import me.agxpt.dark.renderer.interfaces.IShader;

public class ShaderMesh extends Mesh {
    protected final IShader shader;

    public ShaderMesh(IShader shader, DrawMode drawMode, Attrib... attributes) {
        super(drawMode, attributes);
        this.shader = shader;
    }

    @Override
    protected void beforeRender() {
        shader.bind();
        shader.setDefaults();
    }
}
