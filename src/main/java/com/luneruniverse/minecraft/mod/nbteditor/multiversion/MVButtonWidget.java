package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import net.minecraft.util.Identifier;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.IdentifierInst;
import org.joml.Matrix3x2fStack;

public class MVButtonWidget extends PressableWidget {
	
	@FunctionalInterface
	public interface PressAction {
		public void onPress(MVButtonWidget button);
	}
	
	private final PressAction onPress;
	private final MVTooltip tooltip;
	
	public MVButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress, MVTooltip tooltip) {
		super(x, y, width, height, text);
		this.onPress = onPress;
		this.tooltip = tooltip;
		if (tooltip != null) {
			Version.newSwitch()
					.range("1.19.3", null, () -> setTooltip(tooltip.toNewTooltip()))
					.range(null, "1.19.2", () -> {})
					.run();
		}
	}
	public MVButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress) {
		this(x, y, width, height, text, onPress, null);
	}
	
	@Override
	public void onPress(AbstractInput a) {
		onPress.onPress(this);
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		appendDefaultNarrations(builder);
	}
	
	@Override
	public void render(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) {
		Version.newSwitch()
				.range("1.20.0", null, () -> {
					DrawContext context = MVDrawableHelper.getDrawContext(matrices);
					if (context != null)
						super.render(context, mouseX, mouseY, delta);
					else
						MVDrawableHelper.super_render(MVButtonWidget.class, this, matrices, mouseX, mouseY, delta);
				})
				.range(null, "1.19.4", () -> MVDrawableHelper.super_render(MVButtonWidget.class, this, matrices, mouseX, mouseY, delta))
				.run();
	}
	public final void method_25394(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) {
		render(matrices, mouseX, mouseY, delta);
	}
	@Override
	public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
		render(MVDrawableHelper.getMatrices(context), mouseX, mouseY, delta);
	}

	public void renderButton(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) {
		// Simple fallback rendering for newer MC versions when the vanilla
		// rendering path isn't available. Draw a solid background and centered
		// text so buttons remain visible (fixes invisible buttons on 1.21.11).
		int bx = this.x;
		int by = this.y;
		int bw = this.width;
		int bh = this.height;

		boolean hovered = mouseX >= bx && mouseX < bx + bw && mouseY >= by && mouseY < by + bh;
		int bgColor;
		int textColor;
		if (!this.active) {
			bgColor = 0xFF444444;
			textColor = 0xFF888888; // keep gray when disabled
		} else {
			bgColor = hovered ? 0xFFAAAAAA : 0xFF888888;
			textColor = 0xFFFFFFFF; // always white for active buttons (don't change on hover)
		}

		// Draw using vanilla widgets texture so resource-packs can replace it.
		// Use button sprite introduced in newer versions (found in loom-cache)
		Identifier widgets = IdentifierInst.of("textures/gui/sprites/widget/button.png");
		// Use same drawing logic as MVTexturedButtonWidget_1_20_2: pick u/v and offset for hovered/disabled
		int u = 0;
		int baseV = 46; // base v offset for normal button
		int hoveredVOffset = 20; // offset to add when hovered / disabled
		int i = baseV;
		if (!this.active) {
			i += hoveredVOffset * 2;
		} else if (hovered) {
			i += hoveredVOffset;
		}
		MVDrawableHelper.drawTexture(matrices, widgets, bx, by, (float) u, (float) i, bw, bh, 256, 256);

		int textY = by + (bh - MainUtil.client.textRenderer.fontHeight) / 2;
		MVDrawableHelper.drawCenteredTextWithShadow(matrices, MainUtil.client.textRenderer, this.getMessage(), bx + bw / 2, textY, textColor);
	}
	// New PressableWidget API: renderWidget is final; implement drawIcon instead.
	@Override
	public void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
		renderButton(MVDrawableHelper.getMatrices(context), mouseX, mouseY, delta);
	}
	public final void method_48579(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) {
		renderButton(matrices, mouseX, mouseY, delta);
	}
	public final void method_25359(Matrix3x2fStack matrices, int mouseX, int mouseY, float delta) { // renderButton <= 1.19.3
		renderButton(matrices, mouseX, mouseY, delta);
	}
	private void super_renderButton(String intermediary, MatrixStack matrices, int mouseX, int mouseY, float delta) {
		try {
			MethodHandles.lookup().findSpecial(ClickableWidget.class, intermediary,
					MethodType.methodType(void.class, MatrixStack.class, int.class, int.class, float.class),
					MVButtonWidget.class).invoke(this, matrices, mouseX, mouseY, delta);
		} catch (Throwable e) {
			throw new RuntimeException("Error calling super.renderButton (" + intermediary + ")", e);
		}
	}

	public void method_25352(Matrix3x2fStack matrices, int mouseX, int mouseY) { // renderTooltip
		if (tooltip != null)
			tooltip.render(matrices, mouseX, mouseY);
	}
	
}
