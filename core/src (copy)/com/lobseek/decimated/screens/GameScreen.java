/*
 * This program is open software.
 * You may:
 *  * buy this program with Google Play or App Store.
 *  * read code, change code.
 *  * compile and run code if you bought this program.
 *  * share your modification with people who bought this program.
 * You may not:
 *  * sell this program.
 *  * sell your modification of this program as independent product.
 *  * share your modification with people who have no legal copy of
 *                                                    this program.
 *  * share compiled program with people who have no legal copy of it. 
 */
package com.lobseek.decimated.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.lobseek.decimated.actors.Base;
import com.lobseek.decimated.actors.Test;
import com.lobseek.decimated.components.Room;
import com.lobseek.decimated.components.Touch;
import com.lobseek.decimated.gui.Button;
import com.lobseek.decimated.gui.Image;
import com.lobseek.decimated.gui.SpawnBar;
import com.lobseek.decimated.units.Disruptor;
import com.lobseek.utils.ColorFabricator;
import static com.lobseek.utils.Math.*;
import com.lobseek.widgets.LWAlignment;
import com.lobseek.widgets.LWContainer;

/**
 *
 * @author Yew_Mentzaki
 */
public class GameScreen extends Screen {

    Room room;
    public LWContainer menu, unitList;

    void beginGame() {
        menu.hide();
        if (room == null) {
            room = new Room(this, 1024, 64);
            room.player = 1;
            add(room);
            room.add(new Base(600, 2000, 1));
            room.add(new Base(-600, 2000, 1));
            room.add(new Base(-0, 2000, 1));
            room.add(new Base(600, -2000, 2));
            room.add(new Base(-600, -2000, 2));
            room.add(new Base(-0, -2000, 2));
            room.add(new Base(2000, 600, 3));
            room.add(new Base(2000, -600, 3));
            room.add(new Base(2000, 0, 3));
            room.add(new Base(-2000, 600, 4));
            room.add(new Base(-2000, -600, 4));
            room.add(new Base(-2000, 0, 4));
            unitList = room.players[room.player].getUnitList();
            add(unitList);
        }
        room.pause();
    }
    com.badlogic.gdx.graphics.g2d.Sprite background;

    public GameScreen() {
        background = new com.badlogic.gdx.graphics.g2d.Sprite(
                new Texture(Gdx.files.internal("background.png")));
        background.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        menu = new LWContainer() {
            float alpha = 0;

            @Override
            public void act(float delta) {
                if (isVisible()) {
                    alpha = Math.min(1, alpha + delta);
                } else {
                    alpha = Math.max(0, alpha - delta);
                }
                super.act(delta);
            }

            @Override
            public void draw(Batch b) {
                background.setSize(parent.width, parent.height);
                background.setPosition(0, 0);
                background.setColor(ColorFabricator.neon(alpha));
                background.draw(b);
                width = parent.width;
                height = parent.height;
                super.draw(b);
            }

            @Override
            public boolean checkSwipe(Touch t) {
                super.checkSwipe(t);
                return isVisible();
            }

            @Override
            public boolean checkTapDown(Touch t) {
                super.checkTapDown(t);
                return isVisible();
            }

            @Override
            public boolean checkTapUp(Touch t) {
                super.checkTapUp(t);
                return isVisible();
            }

        };
        Image i = new Image("gui/logo");
        i.setAlign(LWAlignment.CENTER);
        i.y = 220;
        menu.add(i);
        Button play = new Button("menu.play") {
            @Override
            public void tapUp(Touch t) {
                beginGame();
            }

        };
        play.setAlign(LWAlignment.CENTER);
        play.y = 90 - 140 * 0;
        menu.add(play);
        play = new Button("menu.settings") {

        };
        play.setAlign(LWAlignment.CENTER);
        play.y = 90 - 140 * 1;
        menu.add(play);
        play = new Button("menu.exit") {
            @Override
            public void tapUp(Touch t) {
                System.exit(0);
            }

        };
        play.setAlign(LWAlignment.CENTER);
        play.y = 90 - 140 * 2;
        menu.add(play);
        SpawnBar bar = new SpawnBar();
        bar.setAlign(LWAlignment.LEFT);
        bar.x = 60;
        bar.setValue(0.5f);
        menu.add(bar);
        add(menu);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

}
