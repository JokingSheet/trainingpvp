package de.jokingsheet.trainingpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public final class ReactionTestScreen extends Screen {
    private enum State { WAITING, READY, RESULT, TOO_EARLY }

    private State state = State.WAITING;
    private long readyAtNanos;
    private long nextReadyNanos;
    private long resultMs = -1;

    public ReactionTestScreen() {
        super(Component.literal("Reaction Test"));
        resetRound();
    }

    private void resetRound() {
        state = State.WAITING;
        resultMs = -1;
        long delayMs = ThreadLocalRandom.current().nextLong(1500, 4001);
        nextReadyNanos = System.nanoTime() + delayMs * 1_000_000L;
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(Button.builder(Component.literal("TRY AGAIN"), button -> resetRound())
                .bounds(center - 110, height / 2 + 68, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("BACK TO MENU"), button ->
                Minecraft.getInstance().setScreen(new AcademyScreen()))
                .bounds(center + 10, height / 2 + 68, 112, 20).build());
    }

    @Override
    public void tick() {
        if (state == State.WAITING && System.nanoTime() >= nextReadyNanos) {
            state = State.READY;
            readyAtNanos = System.nanoTime();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE) {
            if (state == State.WAITING) {
                state = State.TOO_EARLY;
                TrainingStats.recordEarlyPress();
                return true;
            }
            if (state == State.READY) {
                resultMs = (System.nanoTime() - readyAtNanos) / 1_000_000L;
                state = State.RESULT;
                TrainingStats.recordReaction(resultMs);
                return true;
            }
            if (state == State.RESULT || state == State.TOO_EARLY) {
                resetRound();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xE9080D18);
        int panelW = 390;
        int panelH = 210;
        int left = (width - panelW) / 2;
        int top = (height - panelH) / 2;
        graphics.fill(left, top, left + panelW, top + panelH, 0xFF111B2C);
        graphics.fill(left, top, left + panelW, top + 4, 0xFF43D9C5);

        graphics.drawCenteredString(font, Component.literal("REACTION TRAINER"), width / 2, top + 22, 0xFFFFFFFF);

        String main;
        int color;
        switch (state) {
            case WAITING -> {
                main = "WAIT FOR GREEN";
                color = 0xFFFFC857;
            }
            case READY -> {
                main = "PRESS SPACE!";
                color = 0xFF43D9C5;
            }
            case RESULT -> {
                main = resultMs + " ms";
                color = 0xFF43D9C5;
            }
            case TOO_EARLY -> {
                main = "TOO EARLY!";
                color = 0xFFFF7272;
            }
            default -> {
                main = "";
                color = 0xFFFFFFFF;
            }
        }

        graphics.drawCenteredString(font, Component.literal(main), width / 2, top + 80, color);
        String hint = state == State.RESULT || state == State.TOO_EARLY
                ? "Press SPACE or TRY AGAIN for another round."
                : "Press SPACE only when the message turns green.";
        graphics.drawCenteredString(font, Component.literal(hint), width / 2, top + 116, 0xFFB8C7D9);

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
