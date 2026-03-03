package uk.co.tmdavies.nibanking.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.co.tmdavies.nibanking.NIBanking;

import java.util.ArrayList;
import java.util.List;

// https://github.com/terrarium-earth/Cognition/blob/1.21.1-neoforge/src/main/java/com/cyanogen/cognition/gui/ExperienceObeliskScreen.java#L125

public class PDAScreen extends AbstractContainerScreen<PDAMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(NIBanking.MODID, "textures/gui/pda/backgroundtest160.png");
    private int offsetX;
    private int offsetY;
    private boolean initialised = false;

    public PDAScreen(PDAMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 160;
        imageHeight = 160;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        if (!initialised) {
            setupWidgetElements();
            initialised = true;
        }
        super.init();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        renderBackground(gui, mouseX, mouseY, partialTick);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);

        gui.blit(BACKGROUND, offsetX, offsetY, 0, 0, imageWidth, imageHeight, 160, 160);

        gui.drawCenteredString(this.font,  this.title.getString(), this.width / 2, this.offsetY, 0xFFFFFF);

        clearWidgets();
        loadWidgetElements();

        for (Renderable widget : this.renderables) {
            widget.render(gui, mouseX, mouseY, partialTick);
        }

    }

    @Override
    protected void renderBg(GuiGraphics gui, float v, int i, int i1) {

    }

    private final List<Button> buttons = new ArrayList<>();
    private final List<EditBox> editBoxes = new ArrayList<>();

    private void loadWidgetElements() {
        if (!this.buttons.isEmpty()) {
            for (Button button : this.buttons) {
//                button.setFocused(false);
                addRenderableWidget(button);
            }
        }
        if (!this.editBoxes.isEmpty()) {
            for (EditBox editBox : this.editBoxes) {
//                editBox.setFocused(false);
                addRenderableWidget(editBox);
            }
        }
    }

    private void setupWidgetElements() {
        this.buttons.clear();

        int buttonWidth = 100;
        int buttonHeight = 20;

        this.offsetX = (width - imageWidth) / 2;
        this.offsetY = (height - imageHeight) / 2;

        Button iamabutton = myButton(offsetX + 5, offsetY + 20, buttonWidth, buttonHeight);
        this.buttons.add(iamabutton);

        EditBox editBox = myEditBox(offsetX + 5, offsetY + 45, buttonWidth, buttonHeight);
        this.editBoxes.add(editBox);
    }

    public Button myButton(int x, int y, int width, int height) {
        return Button.builder(Component.literal("I am a button"), (onPress) -> myPress()).bounds(x, y, width, height).tooltip(Tooltip.create(Component.literal("And this is my tooltip text"))).build();
    }

    public EditBox myEditBox(int x, int y, int width, int height) {
        EditBox editBox = new EditBox(this.font, x, y, width, height, Component.literal("start text"));
        editBox.setEditable(true);
        editBox.setResponder((v) -> {
            NIBanking.LOGGER.info("Responder?");
        });
        return editBox;
    }

    public static void myPress() {
        NIBanking.LOGGER.info("You press the button!");
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);


    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (EditBox editBox : this.editBoxes) {
            if (editBox.isFocused()) {
                editBox.keyPressed(keyCode, scanCode, modifiers);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
