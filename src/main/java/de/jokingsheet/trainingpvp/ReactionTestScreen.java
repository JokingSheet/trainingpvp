package de.jokingsheet.trainingpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.concurrent.ThreadLocalRandom;

public final class ReactionTestScreen extends Screen {
    private enum State { WAITING, READY, RESULT, TOO_EARLY }

    private State state = State.WAITING;
    private long readyAtNanos;
    private long nextReadyNanos;
    private long resultMs = -1;
    private Button actionButton;

    public ReactionTestScreen() {
        super(Component.literal("Reaction Test"));
        resetRound();
    }

    private void resetRound() {
        state = State.WAITING;
        resultMs = -1;
        long delayMs = ThreadLocalRandom.current().nextLong(1500, 4001);
        nextReadyNanos = System.nanoTime() + delayMs * 1_000_000L;
        if (actionButton != null) {
            actionButton.setMessage(Component.literal("WAIT FOR GREEN"));
        }
    }

    @Override
    protected void init() {
        int center = width / 2;
        actionButton = Button.builder(Component.literal("WAIT FOR GREEN"), button -> handleTap())
                .bounds(center - 110, height / 2 + 42, 220, 24).build();
        addRenderableWidget(actionButton);

        addRenderableWidget(Button.builder(Component.literal("BACK TO MENU"), button ->
                Minecraft.getInstance().setScreen(new AcademyScreen()))
                .bounds(center - 70, height / 2 + 78, 140, 20).build());
    }

    private void handleTap() {
        if (state == State.WAITING) {
            state = State.TOO_EARLY;
            TrainingStats.recordEarlyPress();
            actionButton.setMessage(Component.literal("TRY AGAIN"));
            return;
        }

        if (state == State.READY) {
            resultMs = (System.nanoTime() - readyAtNanos) / 1_000_000L;
            state = State.RESULT;
            TrainingStats.recordReaction(resultMs);
            actionButton.setMessage(Component.literal("TRY AGAIN"));
            return;
        }

        resetRound();
    }

    @Override
    public void tick() {
        if (state == State.WAITING && System.nanoTime() >= nextReadyNanos) {
            state = State.READY;
            readyAtNanos = System.nanoTime();
            if (actionButton != null) {
                actionButton.setMessage(Component.literal("CLICK NOW!"));
            }
        }
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
                main = "GO! GO! GO!";
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
                ? "Click TRY AGAIN to start another round."
                : "Wait for the button to turn green, then click once.";
        graphics.drawCenteredString(font, Component.literal(hint), width / 2, top + 116, 0xFFB8C7D9);

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
