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
package com.lobseek.decimated.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.lobseek.decimated.Main;
import com.lobseek.decimated.particles.Explosion;
import com.lobseek.utils.ColorFabricator;
import static com.lobseek.utils.Math.*;
import static java.lang.Math.max;

/**
 *
 * @author Yew_Mentzaki
 */
public class Unit extends Actor {

    public int owner, hpNumber;
    public float speed, turnSpeed, selectionAlpha;
    public float tx, ty, experience = 10;
    public float hp, maxHp, visiblity = 1;
    private float hpColor;
    protected float deathTimer;
    public boolean selected, immortal;
    public Weapon weapons[];
    public boolean flying;
    public Unit target;

    protected static final Sprite selection = new Sprite("selection");
    protected Sprite body, body_team, body_shadow;
    public static Sprite MMS = new Sprite("minimap/unit", true);
    public static final Sprite HEALTHBAR[] = new Sprite[]{
        new Sprite("healthbar/0"),
        new Sprite("healthbar/1"),
        new Sprite("healthbar/2"),
        new Sprite("healthbar/3"),
        new Sprite("healthbar/4"),
        new Sprite("healthbar/5"),
        new Sprite("healthbar/6")
    };
    private static Sprite deathExplosion = new Sprite("plasma/explosion3");

    public Unit(float x, float y, float angle, int owner) {
        super(x, y, angle);
        tx = x;
        ty = y;
        z = 10;
        this.owner = owner;
        minimapSprite = MMS;
    }

    /**
     * Renders this unit on minimap. Overrided, 'cause color must be changed and
     * sprite must not.
     *
     * @param batch
     * @param delta
     */
    @Override
    public void minimapRender(Batch batch, float delta, float alpha) {
        minimapSprite.setColor(room.players[owner].color);
        super.minimapRender(batch, delta, alpha);
    }

    /**
     * Called when unit created. Not spawned: it could be called from base to
     * make some animations, to do a barrel roll or to do anything other.
     */
    @Override
    public void create() {
        room.players[owner].units++;
        super.create();
        if (weapons != null) {
            for (int i = 0; i < weapons.length; i++) {
                weapons[i].on = this;
                weapons[i].room = room;
                weapons[i].angle = angle;
                weapons[i].create();
            }
        }
    }

    /**
     * Called when unit must be hurted. By someone, exacly.
     *
     * @param hp healthpoints that must be removed. Armor or shield wasn't
     * calculated, do it yourself it this method.
     * @param from unit that attacked.
     */
    public void hit(float hp, Unit from) {
        /**
         * @todo make angle of attack. Powershell animation will be soon.
         */
        if (this.hp > 0 && this.hp - hp <= 0) {
            if (from != null) {
                from.experience += this.experience / 2;
                this.experience = 0;
            }
            for (int i = 0; i < Main.R.nextInt(5) + 6; i++) {
                room.add(
                        new Explosion(
                                x + Main.R.nextInt((int) width) - width / 2,
                                y + Main.R.nextInt((int) width) - width / 2,
                                300 + Main.R.nextInt(200),
                                deathExplosion, 60, 150 + Main.R.nextInt(100)
                        )
                );
            }
            for (int i = 0; i < Main.R.nextInt(3) + 2; i++) {
                room.add(
                        new Explosion(
                                x + Main.R.nextInt((int) width * 2) - width,
                                y + Main.R.nextInt((int) width * 2) - width,
                                300 + Main.R.nextInt(300),
                                deathExplosion, 60, 350 + Main.R.nextInt(100)
                        )
                );
            }
        }
        this.hp = max(0, this.hp - hp);
    }

    /**
     * Handles movement and all turns. Can be overrided.
     *
     * @param delta
     */
    public void move(float delta) {
        if (speed > 0 && turnSpeed > 0) {
            if (x != tx && y != ty) {
                float ta = (float) atan2(ty - y, tx - x);
                if (angle < -PI) {
                    angle += 2 * PI;
                }
                if (angle > +PI) {
                    angle -= 2 * PI;
                }
                if (angle != ta) {
                    if (abs(ta - angle) > turnSpeed * delta) {
                        int v = (abs(ta - angle) <= 2 * PI - abs(ta - angle)) ? 1 : -1;

                        if (ta < angle) {
                            angle -= turnSpeed * v * delta;
                        } else if (ta > angle) {
                            angle += turnSpeed * v * delta;
                        }
                    } else {
                        angle = ta;
                    }
                }
                if (angle == ta || speed >= 200) {
                    float d = (float) sqrt(pow(tx - x, 2) + pow(ty - y, 2));
                    if (d > speed * delta) {
                        d = speed * delta;
                    }
                    move(d * cos(angle), d * sin(angle));
                }
            }
        }
    }

    /**
     * Sets all sprites for this unit. Also calls sprites for weapons.
     *
     * @param name first part of sprite name. "_body" et cetera shall be added
     * automatically.
     */
    public void setSprite(String name) {
        body = new Sprite(name + "_body");
        body_team = new Sprite(name + "_body_team");
        body_shadow = new Sprite(name + "_body_shadow");
        if (weapons != null) {
            for (int i = 0; i < weapons.length; i++) {
                weapons[i].setSprite(name);
            }
        }
    }

    /**
     * Acts unit.
     *
     * @param delta
     */
    @Override
    public void act(float delta) {
        hpColor = Math.max(hpColor - delta, 0);
        if (selected && hp > 0) {
            selectionAlpha = Math.min(1, selectionAlpha + delta * 2);
        } else {
            selectionAlpha = Math.max(0, selectionAlpha - delta * 2);
        }
        if (hp > 0) {
            move();
            move(delta);
            handleCollision(delta);
            handleBarricadeCollision(delta);
            if (weapons != null) {
                for (int i = 0; i < weapons.length; i++) {
                    weapons[i].act(delta);
                }
            }
        } else {
            deathTimer += delta * 2;
            if (deathTimer >= 1) {
                room.players[owner].units--;
                remove();
            }
        }
    }

    /**
     * Handles collision with barricade. Won't be handled if unit is flying.
     * (Remember, "flying" is boolean field)
     *
     * @param delta
     */
    public void handleBarricadeCollision(float delta) {
        if (!flying) {
            Barricade b1 = room.getBarricade(x, y);
            handleBarricadeCollision(b1);
            Barricade b2 = room.getBarricade(x + width / 2, y);
            if (b2 != b1) {
                handleBarricadeCollision(b2);
            }
            Barricade b3 = room.getBarricade(x - width / 2, y);
            if (b3 != b1 && b3 != b2) {
                handleBarricadeCollision(b3);
            }
            Barricade b4 = room.getBarricade(x, y - height / 2);
            if (b4 != b1 && b4 != b2 && b4 != b3) {
                handleBarricadeCollision(b4);
            }
            Barricade b5 = room.getBarricade(x, y + height / 2);
            if (b5 != b1 && b5 != b2 && b5 != b3 && b5 != b4) {
                handleBarricadeCollision(b5);
            }
        }
    }

    /**
     * Internal method, don't give a fuck.
     *
     * @param b barricade
     */
    private void handleBarricadeCollision(Barricade b) {
        if (b != null) {
            if (abs(x - b.x) > width + 20 || abs(y - b.y) > height + 20) {
                return;
            }
            float d = dist(x, y, b.x, b.y);
            float r = dist(0, 0, width + b.width, height + b.height) / 2 - 20;
            if (d < r) {
                r -= d;
                float angle = atan2(b.y - y, b.x - x);
                kick(r, angle + PI);
                b.kick(0, angle);
            }
        }
    }

    /**
     * Ticks weapons, searching enemies and making coffee.
     *
     * @param delta
     */
    @Override
    public void tick(float delta) {
        if (weapons != null) {
            for (int i = 0; i < weapons.length; i++) {
                weapons[i].tick(delta);
            }
        }
        handleTargetCollision(delta);
    }

    /**
     * Called from the Room, renders unit like white one.
     *
     * @param batch SpriteBatch
     * @param delta
     */
    @Override
    public void render(Batch batch, float delta) {
        render(batch, delta, Color.WHITE);
    }

    /**
     * Renders shadow. Or light. I don't care, it will be on the background.
     *
     * @param batch
     * @param delta
     */
    @Override
    public void renderShadow(Batch batch, float delta) {
        body_shadow.x = x;
        body_shadow.y = y + 15;
        body_shadow.angle = angle;
        body_shadow.a = Math.max(0, 1 - deathTimer) * visiblity;
        body_shadow.draw(batch);
        body_shadow.y = y + 7.5f;
        body_shadow.draw(batch);
        if (weapons != null) {
            for (Weapon w : weapons) {
                w.renderShadow(batch, delta);
            }
        }
    }

    public void render(Batch batch, float delta, Color parentColor) {
        parentColor = new Color(parentColor.r, parentColor.g,
                parentColor.b, parentColor.a * Math.max(0, 1 - deathTimer));
        body.x = x;
        body.y = y;
        body.angle = angle;
        body.setColor(parentColor);
        body.a *= visiblity;
        body.draw(batch);
        body_team.x = x;
        body_team.y = y;
        body_team.angle = angle;
        body_team.setColor(room.players[owner].color);
        body_team.r *= parentColor.r;
        body_team.g *= parentColor.g;
        body_team.b *= parentColor.b;
        body_team.a *= parentColor.a;
        if (owner != room.player) {
            body_team.a *= visiblity;
        }
        body_team.draw(batch);
        for (int i = 0; i < weapons.length; i++) {
            weapons[i].render(batch, delta, parentColor);
        }
    }

    public void handleTargetCollision(float delta) {
        /*
        for (Actor a : room.actors) {
            if (a != null && a instanceof Unit) {
                Unit u = (Unit) a;
                if (u.owner == this.owner && !a.phantom && u != this) {

                    float size = (Math.max(width, height) + Math.max(u.width, u.height));
                    if (abs(tx - u.tx) > size || abs(ty - u.ty) > size) {
                        continue;
                    }
                    float d = dist(tx, ty, u.tx, u.ty);
                    float r = dist(0, 0, width + u.width, height + u.height) / 2 + 30;
                    if (d < r) {
                        r -= d;
                        r *= delta * 5;
                        float an = atan2(u.y - y, u.x - x);
                        //System.out.println(an + ":" + u.y + "-" + y);
                        u.tx += cos(an) * r;
                        u.tx += sin(an) * r;
                        tx -= cos(an) * r;
                        tx -= sin(an) * r;
                    }
                }
            }
        }*/
    }

    @Override
    public void renderInterface(Batch batch, float delta) {
        if (selectionAlpha > 0) {
            selection.x = x;
            selection.y = y;
            selection.angle = atan2(ty - y, tx - x);
            selection.setColor(ColorFabricator.neon(selectionAlpha));
            selection.draw(batch);
        }

        int hp_index = (int) Math.max(0, Math.min(hp / maxHp * 6, 6));
        if (hp_index != hpNumber) {
            if (hpColor < 1) {
                hpColor = 2 - hpColor;
            }
            hpNumber = hp_index;
        }
        if (hpColor > 0) {
            Sprite hp = HEALTHBAR[hp_index];

            hp.setScale(sqrt(0.6f - Math.abs(1 - hpColor) * 0.3f) * 0.5f);
            hp.x = x;
            hp.y = y + height / 3;
            hp.setColor(ColorFabricator.neon(1 - Math.abs(1 - hpColor)));
            hp.draw(batch);
        }
    }
}
