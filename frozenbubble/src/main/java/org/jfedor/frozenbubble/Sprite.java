/*
 *                 [[ Frozen-Bubble ]]
 *
 * Copyright (c) 2000-2003 Guillaume Cottenceau.
 * Java sourcecode - Copyright (c) 2003 Glenn Sanson.
 * Additional source - Copyright (c) 2013 Eric Fortin.
 *
 * This code is distributed under the GNU General Public License
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 or 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to:
 * Free Software Foundation, Inc.
 * 675 Mass Ave
 * Cambridge, MA 02139, USA
 *
 * Artwork:
 *    Alexis Younes <73lab at free.fr>
 *      (everything but the bubbles)
 *    Amaury Amblard-Ladurantie <amaury at linuxfr.org>
 *      (the bubbles)
 *
 * Soundtrack:
 *    Matthias Le Bidan <matthias.le_bidan at caramail.com>
 *      (the three musics and all the sound effects)
 *
 * Design & Programming:
 *    Guillaume Cottenceau <guillaume.cottenceau at free.fr>
 *      (design and manage the project, whole Perl sourcecode)
 *
 * Java version:
 *    Glenn Sanson <glenn.sanson at free.fr>
 *      (whole Java sourcecode, including JIGA classes
 *             http://glenn.sanson.free.fr/jiga/)
 *
 * Android port:
 *    Pawel Aleksander Fedorynski <pfedor@fuw.edu.pl>
 *    Eric Fortin <videogameboy76 at yahoo.com>
 *    Copyright (c) Google Inc.
 *
 *          [[ http://glenn.sanson.free.fr/fb/ ]]
 *          [[ http://www.frozen-bubble.org/   ]]
 */

package org.jfedor.frozenbubble;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;

public abstract class Sprite {
  public static int TYPE_BUBBLE = 1;
  public static int TYPE_IMAGE = 2;
  public static int TYPE_LAUNCH_BUBBLE = 3;
  public static int TYPE_PENGUIN = 4;

  private Rect spriteArea;
  private int saved_id;

  public Sprite(Rect spriteArea) {
    this.spriteArea = spriteArea;
    saved_id = -1;
  }

  public void saveState(Bundle map, Vector<Sprite> saved_sprites, int id) {
    if (saved_id != -1) {
      return;
    }
    saved_id = saved_sprites.size();
    saved_sprites.addElement(this);
    map.putInt(String.format("%d-%d-left", id, saved_id), spriteArea.left);
    map.putInt(String.format("%d-%d-right", id, saved_id), spriteArea.right);
    map.putInt(String.format("%d-%d-top", id, saved_id), spriteArea.top);
    map.putInt(String.format("%d-%d-bottom", id, saved_id), spriteArea.bottom);
    map.putInt(String.format("%d-%d-type", id, saved_id), getTypeId());
  }

  public final int getSavedId() {
    return saved_id;
  }

  public final void clearSavedId() {
    saved_id = -1;
  }

  public abstract int getTypeId();

  public void changeSpriteArea(Rect newArea) {
    spriteArea = newArea;
  }

  public final void relativeMove(Point p) {
    spriteArea = new Rect(spriteArea);
    spriteArea.offset(p.x, p.y);
  }

  public final void relativeMove(int x, int y) {
    spriteArea = new Rect(spriteArea);
    spriteArea.offset(x, y);
  }

  public final void absoluteMove(Point p) {
    spriteArea = new Rect(spriteArea);
    spriteArea.offsetTo(p.x, p.y);
  }

  public final Point getSpritePosition() {
    return new Point(spriteArea.left, spriteArea.top);
  }

  public final Rect getSpriteArea() {
    return spriteArea;
  }

  public static void drawImage(BmpWrap image, int x, int y,
                               Canvas c, double scale, int dx, int dy) {
    c.drawBitmap(image.bmp, (float)(x * scale + dx), (float)(y * scale + dy),
                 null);
  }

  public static void drawImageClipped(BmpWrap image, int x, int y, Rect clipr,
                                      Canvas c, double scale, int dx, int dy) {
    //
    //   NOTE 1: Canvas.CLIP_SAVE_FLAG was deprecated in API 26.  Previously the canvas save
    //           appeared as follows: c.save(Canvas.CLIP_SAVE_FLAG);
    //
    //           The parameter was removed for API 26 compatibility.
    //
    //   NOTE 2: Region.Op values other than Region.Op.INTERSECT and Region.Op.DIFFERENCE were
    //           deprecated in API 26.  Previously the rectangle clipping operation used the
    //           following parameter: Region.Op.REPLACE
    //
    //           The parameter was changed for API 26 compatibility to: Region.Op.INTERSECT
    //
    c.save();
    c.clipRect((float)(clipr.left * scale + dx),
               (float)(clipr.top * scale + dy),
               (float)(clipr.right * scale + dx),
               (float)(clipr.bottom * scale + dy),
               Region.Op.INTERSECT);
    c.drawBitmap(image.bmp, (float)(x * scale + dx), (float)(y * scale + dy),
                 null);
    c.restore();
  }

  public abstract void paint(Canvas c, double scale, int dx, int dy);
}
