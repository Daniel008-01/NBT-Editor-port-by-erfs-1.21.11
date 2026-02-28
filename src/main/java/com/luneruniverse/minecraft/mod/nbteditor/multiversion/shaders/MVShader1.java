package com.luneruniverse.minecraft.mod.nbteditor.multiversion.shaders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;

// Minimal stub implementation to avoid compilation issues with mapping changes.
// This preserves the class API but defers correct shader implementation.
public class MVShader1 extends MVShader {

	public static final List<MVShader1> SHADERS = new ArrayList<>();

	private final String shaderName;
	private final MVVertexFormat vertexFormat;
	private final MVDrawMode drawMode;
	private final RenderLayer layer;
	private ShaderProgram shaderProgram;

	public MVShader1(MVShader.Builder builder) {
		this.shaderName = builder.getShaderName();
		this.vertexFormat = builder.getVertexFormat();
		this.drawMode = builder.getDrawMode();
		this.layer = null; // Real implementation deferred; keeps compilation working
		SHADERS.add(this);
	}

	public static RenderLayer createLayer(MVShader.Builder builder, Object renderPhaseShaderProgram) {
		return null;
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

	public ShaderProgram getShaderProgram() {
		return this.shaderProgram;
	}

	@Override
	public RenderLayer getLayer() {
		return layer;
	}

	public String getShaderName() { return shaderName; }
	public MVVertexFormat getVertexFormat() { return vertexFormat; }
	public MVDrawMode getDrawMode() { return drawMode; }
}
