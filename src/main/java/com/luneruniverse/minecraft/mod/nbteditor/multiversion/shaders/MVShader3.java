package com.luneruniverse.minecraft.mod.nbteditor.multiversion.shaders;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;

// Minimal stub implementation to avoid compilation issues with mapping changes.
public class MVShader3 extends MVShader {

	public static final List<RenderPipeline> RENDER_PIPELINES = new ArrayList<>();

	private final RenderLayer layer;

	public MVShader3(MVShader.Builder builder) {
		// Defer actual pipeline/layer construction to a future, mapping-aware implementation
		this.layer = null;
	}

	@Override
	public RenderLayer getLayer() {
		return layer;
	}

}
