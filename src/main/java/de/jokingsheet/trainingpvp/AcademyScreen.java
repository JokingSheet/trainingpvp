package de.jokingsheet.trainingpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class AcademyScreen extends Screen {
    private static final int PANEL_W = 590;
    private static final int PANEL_H = 360;

    private final Page page;

    private enum Page {
        HOME, MOVEMENT, KIT, MONITOR, STATS
    }

    public AcademyScreen() {
        this(Page.HOME);
    }

    private AcademyScreen(Page page) {
        super(Component.literal("Movement Academy"));
        this.page = page;
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_W) / 2;
        int top = (this.height - PANEL_H) / 2;

        addNavButton("START", left + 18, top + 76, Page.HOME);
        addNavButton("MOVEMENT LAB", left + 18, top + 108, Page.MOVEMENT);
        addNavButton("KIT LOCKER", left + 18, top + 140, Page.KIT);
        addNavButton("PERFORMANCE MONITOR", left + 18, top + 172, Page.MONITOR);
        addNavButton("STATS", left + 18, top + 204, Page.STATS);

        int actionX = left + 170;
        int actionY = top + PANEL_H - 54;

        if (page == Page.HOME || page == Page.KIT) {
            addRenderableWidget(Button.builder(Component.literal("GET PARKOUR KIT"), button ->
                    TrainingPvpClient.giveMovementKit(Minecraft.getInstance())
            ).bounds(actionX, actionY, 180, 22).build());
        }

        if (page == Page.HOME || page == Page.MOVEMENT) {
            addRenderableWidget(Button.builder(Component.literal("START REACTION TEST"), button ->
                    Minecraft.getInstance().setScreen(new ReactionTestScreen())
            ).bounds(actionX + 190, actionY, 180, 22).build());
        }

        addRenderableWidget(Button.builder(Component.literal("CLOSE"), button ->
                Minecraft.getInstance().setScreen(null)
        ).bounds(left + PANEL_W - 104, top + PANEL_H - 30, 86, 20).build());
    }

    private void addNavButton(String label, int x, int y, Page target) {
        addRenderableWidget(Button.builder(Component.literal(label), button ->
                Minecraft.getInstance().setScreen(new AcademyScreen(target))
        ).bounds(x, y, 132, 22).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xB8080D18);

        int left = (width - PANEL_W) / 2;
        int top = (height - PANEL_H) / 2;
        graphics.fill(left, top, left + PANEL_W, top + PANEL_H, 0xF0111728);
        graphics.fill(left, top, left + PANEL_W, top + 4, 0xFF43D9C5);
        graphics.fill(left + 154, top + 54, left + 156, top + PANEL_H - 18, 0xFF293A50);
        graphics.fill(left + 18, top + 54, left + PANEL_W - 18, top + 55, 0xFF293A50);

        graphics.drawString(font, Component.literal("MOVEMENT ACADEMY"), left + 20, top + 20, 0xFFFFFFFF);
        graphics.drawString(font, Component.literal("FABRIC 1.21.11  •  CLIENT TRAINING"), left + 20, top + 36, 0xFF43D9C5);
        graphics.drawString(font, Component.literal("H"), left + PANEL_W - 31, top + 22, 0xFF43D9C5);

        drawPage(graphics, left + 176, top + 72);

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawPage(GuiGraphics g, int x, int y) {
        switch (page) {
            case HOME -> {
                heading(g, "TRAINING OVERVIEW", x, y);
                line(g, "Build clean movement fundamentals with short, repeatable drills.", x, y + 28);
                statCard(g, x, y + 62, "REACTION BEST", TrainingStats.getBestReactionText());
                statCard(g, x + 180, y + 62, "REACTION RUNS", Integer.toString(TrainingStats.getReactionAttempts()));
                statCard(g, x, y + 126, "KIT CLAIMS", Integer.toString(TrainingStats.getMovementKitClaims()));
                statCard(g, x + 180, y + 126, "CLIENT TICKS", TrainingPvpClient.getMeasuredClientTicks() + " / s");
                line(g, "Choose a module on the left to open its practice page.", x, y + 218);
            }
            case MOVEMENT -> {
                heading(g, "MOVEMENT LAB", x, y);
                line(g, "01  Sprint control: run a straight line, then repeat with clean 90-degree turns.", x, y + 34);
                line(g, "02  Jump rhythm: clear a row of one-block gaps without stopping.", x, y + 58);
                line(g, "03  Bridging: build a straight 20-block path, then a diagonal path.", x, y + 82);
                line(g, "04  Landing control: use water, slime and honey blocks in a safe test area.", x, y + 106);
                line(g, "05  Route memory: repeat the same parkour route and beat your time.", x, y + 130);
                line(g, "Tip: practise each drill slowly first; consistency beats rushing.", x, y + 178);
            }
            case KIT -> {
                heading(g, "KIT LOCKER", x, y);
                line(g, "A simple building kit for solo parkour practice:", x, y + 34);
                line(g, "64 Oak Planks  •  32 Scaffolding  •  16 Slime Blocks", x, y + 62);
                line(g, "16 Honey Blocks  •  1 Water Bucket", x, y + 86);
                line(g, "The button below adds items; it never clears your inventory.", x, y + 130);
            }
            case MONITOR -> {
                heading(g, "CLIENT PERFORMANCE", x, y);
                line(g, "Measured client logic ticks over the last one-second window:", x, y + 36);
                statCard(g, x, y + 76, "CLIENT TICKS", TrainingPvpClient.getMeasuredClientTicks() + " / s");
                line(g, "Minecraft's normal game-loop target is 20 ticks per second.", x, y + 154);
                line(g, "This is a local client estimate, not a server TPS reading.", x, y + 178);
                line(g, "Use this local reading to spot slowdowns during parkour practice.", x, y + 202);
            }
            case STATS -> {
                heading(g, "SESSION STATS", x, y);
                line(g, "Reaction attempts: " + TrainingStats.getReactionAttempts(), x, y + 38);
                line(g, "Successful reactions: " + TrainingStats.getReactionSuccesses(), x, y + 64);
                line(g, "Best reaction: " + TrainingStats.getBestReactionText(), x, y + 90);
                line(g, "Parkour kits claimed: " + TrainingStats.getMovementKitClaims(), x, y + 116);
                line(g, "Stats reset when you close the game.", x, y + 166);
            }
        }
    }

    private void heading(GuiGraphics g, String text, int x, int y) {
        g.drawString(font, Component.literal(text), x, y, 0xFFFFFFFF);
        g.fill(x, y + 16, x + 150, y + 18, 0xFF43D9C5);
    }

    private void line(GuiGraphics g, String text, int x, int y) {
        g.drawString(font, Component.literal(text), x, y, 0xFFB8C7D9);
    }

    private void statCard(GuiGraphics g, int x, int y, String label, String value) {
        g.fill(x, y, x + 164, y + 54, 0xFF1B2940);
        g.fill(x, y, x + 3, y + 54, 0xFF43D9C5);
        g.drawString(font, Component.literal(label), x + 12, y + 10, 0xFF8FA7C0);
        g.drawString(font, Component.literal(value), x + 12, y + 29, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
