package pl.example.guimod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GuiModClient implements ClientModInitializer {

    // Ustawienia (zmieniane z poziomu GUI)
    public static boolean showCoords = true;
    public static boolean showFps = false;

    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        // Klawisz: prawy Shift (można zmienić w Ustawienia -> Sterowanie)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.guimod.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.guimod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new MainScreen());
                }
            }
        });

        // Prosty HUD zależny od przełączników z GUI
        HudRenderCallback.EVENT.register((DrawContext context, net.minecraft.client.render.RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;

            int y = 5;
            if (showCoords) {
                String text = String.format("XYZ: %.1f / %.1f / %.1f",
                        mc.player.getX(), mc.player.getY(), mc.player.getZ());
                context.drawTextWithShadow(mc.textRenderer, text, 5, y, 0xFFFFFF);
                y += 12;
            }
            if (showFps) {
                context.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", 5, y, 0xFFFFFF);
            }
        });
    }

    // ---------------- GUI ----------------
    public static class MainScreen extends Screen {

        private ButtonWidget coordsButton;
        private ButtonWidget fpsButton;

        public MainScreen() {
            super(Text.literal("Moje GUI"));
        }

        @Override
        protected void init() {
            int w = 160, h = 20;
            int x = this.width / 2 - w / 2;
            int y = this.height / 2 - 40;

            coordsButton = ButtonWidget.builder(label("Koordynaty", showCoords), b -> {
                showCoords = !showCoords;
                b.setMessage(label("Koordynaty", showCoords));
            }).dimensions(x, y, w, h).build();

            fpsButton = ButtonWidget.builder(label("FPS", showFps), b -> {
                showFps = !showFps;
                b.setMessage(label("FPS", showFps));
            }).dimensions(x, y + 26, w, h).build();

            ButtonWidget closeButton = ButtonWidget.builder(Text.literal("Zamknij"), b -> this.close())
                    .dimensions(x, y + 62, w, h).build();

            addDrawableChild(coordsButton);
            addDrawableChild(fpsButton);
            addDrawableChild(closeButton);
        }

        private static Text label(String name, boolean on) {
            return Text.literal(name + ": " + (on ? "WŁ." : "WYŁ."));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 65, 0xFFFFFF);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            // Prawy Shift zamyka GUI
            if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                this.close();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}
