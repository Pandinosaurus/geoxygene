/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.appli.gl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

/**
 * tool static class for GL 
 * @author JeT
 *
 */
public final class GLTools {

  /**
   * Private constructor
   */
  private GLTools() {
    // utility class
  }

  /**
   * set gl texture coordinate from a Point2D point
   */
  public static void glTexCoord(final Point2d p) {
    GL11.glTexCoord2d(p.x, p.y);
  }

  /**
   * set gl vertex coordinate from a Point2D point
   */
  public static void glVertex(final Point2d p) {
    GL11.glVertex2d(p.x, p.y);
  }

  /**
   * set gl color from a LWJGL Color object
   */
  public static void glColor(final Color color) {
    GL11.glColor4d(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., color.getAlpha() / 255.);
  }

  /**
   * set gl color from an AWT Color object
   */
  public static void glColor(final java.awt.Color color) {
    GL11.glColor4d(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., color.getAlpha() / 255.);

  }

  /**
   * load a texture in GL context and return the texture id
   * @param image
   * @return the generated texture id
   */
  public static int loadTexture(final BufferedImage image) {

    int[] pixels = new int[image.getWidth() * image.getHeight()];
    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int pixel = pixels[y * image.getWidth() + x];
        buffer.put((byte) (pixel >> 16 & 0xFF));     // Red component
        buffer.put((byte) (pixel >> 8 & 0xFF));      // Green component
        buffer.put((byte) (pixel >> 0 & 0xFF));               // Blue component
        buffer.put((byte) (pixel >> 24 & 0xFF));    // Alpha component. Only for RGBA
        //        System.err.println("transparency = " + (pixel >> 24 & 0xFF));
      }
    }

    buffer.rewind();

    // You now have a ByteBuffer filled with the color data of each pixel.
    // Now just create a texture ID and bind it. Then you can load it using 
    // whatever OpenGL method you want, for example:

    int textureID = glGenTextures(); //Generate texture ID
    glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

    //Setup wrap mode
    //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    //Setup texture scaling filtering
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    //Send texel data to OpenGL
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

    //Return the texture ID so we can bind it later again
    return textureID;
  }

  public static int loadTexture(final String filename) throws IOException {
    return loadTexture(loadImage(filename));
  }

  public static BufferedImage loadImage(final String loc) throws IOException {
    return ImageIO.read(new File(loc));
  }

  public static void glDrawString(final String s, double x, double y) {
    double startX = x;
    GL11.glBegin(GL11.GL_POINTS);
    for (char c : s.toLowerCase().toCharArray()) {
      if (c == 'a') {
        for (int i = 0; i < 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == 'b') {
        for (int i = 0; i < 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
          GL11.glVertex2d(x + i, y - 4);
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 7, y - 5);
        GL11.glVertex2d(x + 7, y - 7);
        GL11.glVertex2d(x + 7, y - 6);

        GL11.glVertex2d(x + 7, y - 1);
        GL11.glVertex2d(x + 7, y - 2);
        GL11.glVertex2d(x + 7, y - 3);
        x += 8;
      } else if (c == 'c') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y);
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 6, y - 2);

        GL11.glVertex2d(x + 6, y - 6);
        GL11.glVertex2d(x + 6, y - 7);

        x += 8;
      } else if (c == 'd') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y);
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 6, y - 2);
        GL11.glVertex2d(x + 6, y - 3);
        GL11.glVertex2d(x + 6, y - 4);
        GL11.glVertex2d(x + 6, y - 5);
        GL11.glVertex2d(x + 6, y - 6);
        GL11.glVertex2d(x + 6, y - 7);

        x += 8;
      } else if (c == 'e') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 0);
          GL11.glVertex2d(x + i, y - 8);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == 'f') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == 'g') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y);
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 6, y - 2);
        GL11.glVertex2d(x + 6, y - 3);
        GL11.glVertex2d(x + 5, y - 3);
        GL11.glVertex2d(x + 7, y - 3);

        GL11.glVertex2d(x + 6, y - 6);
        GL11.glVertex2d(x + 6, y - 7);

        x += 8;
      } else if (c == 'h') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == 'i') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 3, y - i);
        }
        for (int i = 1; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 0);
          GL11.glVertex2d(x + i, y - 8);
        }
        x += 7;
      } else if (c == 'j') {
        for (int i = 1; i <= 8; i++) {
          GL11.glVertex2d(x + 6, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 0);
        }
        GL11.glVertex2d(x + 1, y - 3);
        GL11.glVertex2d(x + 1, y - 2);
        GL11.glVertex2d(x + 1, y - 1);
        x += 8;
      } else if (c == 'k') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        GL11.glVertex2d(x + 6, y - 8);
        GL11.glVertex2d(x + 5, y - 7);
        GL11.glVertex2d(x + 4, y - 6);
        GL11.glVertex2d(x + 3, y - 5);
        GL11.glVertex2d(x + 2, y - 4);
        GL11.glVertex2d(x + 2, y - 3);
        GL11.glVertex2d(x + 3, y - 4);
        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 5, y - 2);
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 7, y);
        x += 8;
      } else if (c == 'l') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
        }
        x += 7;
      } else if (c == 'm') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        GL11.glVertex2d(x + 3, y - 6);
        GL11.glVertex2d(x + 2, y - 7);
        GL11.glVertex2d(x + 4, y - 5);

        GL11.glVertex2d(x + 5, y - 6);
        GL11.glVertex2d(x + 6, y - 7);
        GL11.glVertex2d(x + 4, y - 5);
        x += 8;
      } else if (c == 'n') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        GL11.glVertex2d(x + 2, y - 7);
        GL11.glVertex2d(x + 2, y - 6);
        GL11.glVertex2d(x + 3, y - 5);
        GL11.glVertex2d(x + 4, y - 4);
        GL11.glVertex2d(x + 5, y - 3);
        GL11.glVertex2d(x + 6, y - 2);
        GL11.glVertex2d(x + 6, y - 1);
        x += 8;
      } else if (c == 'o' || c == '0') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 0);
        }
        x += 8;
      } else if (c == 'p') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 4);
        }
        GL11.glVertex2d(x + 6, y - 7);
        GL11.glVertex2d(x + 6, y - 5);
        GL11.glVertex2d(x + 6, y - 6);
        x += 8;
      } else if (c == 'q') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
          if (i != 1) {
            GL11.glVertex2d(x + 7, y - i);
          }
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
          if (i != 6) {
            GL11.glVertex2d(x + i, y - 0);
          }
        }
        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 5, y - 2);
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 7, y);
        x += 8;
      } else if (c == 'r') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 4);
        }
        GL11.glVertex2d(x + 6, y - 7);
        GL11.glVertex2d(x + 6, y - 5);
        GL11.glVertex2d(x + 6, y - 6);

        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 5, y - 2);
        GL11.glVertex2d(x + 6, y - 1);
        GL11.glVertex2d(x + 7, y);
        x += 8;
      } else if (c == 's') {
        for (int i = 2; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 1, y - 7);
        GL11.glVertex2d(x + 1, y - 6);
        GL11.glVertex2d(x + 1, y - 5);
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 4);
          GL11.glVertex2d(x + i, y);
        }
        GL11.glVertex2d(x + 7, y - 3);
        GL11.glVertex2d(x + 7, y - 2);
        GL11.glVertex2d(x + 7, y - 1);
        GL11.glVertex2d(x + 1, y - 1);
        GL11.glVertex2d(x + 1, y - 2);
        x += 8;
      } else if (c == 't') {
        for (int i = 0; i <= 8; i++) {
          GL11.glVertex2d(x + 4, y - i);
        }
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        x += 7;
      } else if (c == 'u') {
        for (int i = 1; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 0);
        }
        x += 8;
      } else if (c == 'v') {
        for (int i = 2; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 6, y - i);
        }
        GL11.glVertex2d(x + 2, y - 1);
        GL11.glVertex2d(x + 5, y - 1);
        GL11.glVertex2d(x + 3, y);
        GL11.glVertex2d(x + 4, y);
        x += 7;
      } else if (c == 'w') {
        for (int i = 1; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        GL11.glVertex2d(x + 2, y);
        GL11.glVertex2d(x + 3, y);
        GL11.glVertex2d(x + 5, y);
        GL11.glVertex2d(x + 6, y);
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + 4, y - i);
        }
        x += 8;
      } else if (c == 'x') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - i);
        }
        for (int i = 7; i >= 1; i--) {
          GL11.glVertex2d(x + i, y - 8 + i);
        }
        x += 8;
      } else if (c == 'y') {
        GL11.glVertex2d(x + 4, y);
        GL11.glVertex2d(x + 4, y - 1);
        GL11.glVertex2d(x + 4, y - 2);
        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 4, y - 4);

        GL11.glVertex2d(x + 3, y - 5);
        GL11.glVertex2d(x + 2, y - 6);
        GL11.glVertex2d(x + 1, y - 7);
        GL11.glVertex2d(x + 1, y - 8);

        GL11.glVertex2d(x + 5, y - 5);
        GL11.glVertex2d(x + 6, y - 6);
        GL11.glVertex2d(x + 7, y - 7);
        GL11.glVertex2d(x + 7, y - 8);
        x += 8;
      } else if (c == 'z') {
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - i);
        }
        GL11.glVertex2d(x + 6, y - 7);
        x += 8;
      } else if (c == '1') {
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
        }
        for (int i = 1; i <= 8; i++) {
          GL11.glVertex2d(x + 4, y - i);
        }
        GL11.glVertex2d(x + 3, y - 7);
        x += 8;
      } else if (c == '2') {
        for (int i = 1; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 1, y - 7);
        GL11.glVertex2d(x + 1, y - 6);

        GL11.glVertex2d(x + 6, y - 7);
        GL11.glVertex2d(x + 6, y - 6);
        GL11.glVertex2d(x + 6, y - 5);
        GL11.glVertex2d(x + 5, y - 4);
        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 3, y - 2);
        GL11.glVertex2d(x + 2, y - 1);
        x += 8;
      } else if (c == '3') {
        for (int i = 1; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y);
        }
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 6, y - i);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == '4') {
        for (int i = 2; i <= 8; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - 1);
        }
        for (int i = 0; i <= 4; i++) {
          GL11.glVertex2d(x + 4, y - i);
        }
        x += 8;
      } else if (c == '5') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        for (int i = 4; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        GL11.glVertex2d(x + 1, y - 1);
        GL11.glVertex2d(x + 2, y);
        GL11.glVertex2d(x + 3, y);
        GL11.glVertex2d(x + 4, y);
        GL11.glVertex2d(x + 5, y);
        GL11.glVertex2d(x + 6, y);

        GL11.glVertex2d(x + 7, y - 1);
        GL11.glVertex2d(x + 7, y - 2);
        GL11.glVertex2d(x + 7, y - 3);

        GL11.glVertex2d(x + 6, y - 4);
        GL11.glVertex2d(x + 5, y - 4);
        GL11.glVertex2d(x + 4, y - 4);
        GL11.glVertex2d(x + 3, y - 4);
        GL11.glVertex2d(x + 2, y - 4);
        x += 8;
      } else if (c == '6') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y);
        }
        for (int i = 2; i <= 5; i++) {
          GL11.glVertex2d(x + i, y - 4);
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 7, y - 1);
        GL11.glVertex2d(x + 7, y - 2);
        GL11.glVertex2d(x + 7, y - 3);
        GL11.glVertex2d(x + 6, y - 4);
        x += 8;
      } else if (c == '7') {
        for (int i = 0; i <= 7; i++) {
          GL11.glVertex2d(x + i, y - 8);
        }
        GL11.glVertex2d(x + 7, y - 7);
        GL11.glVertex2d(x + 7, y - 6);

        GL11.glVertex2d(x + 6, y - 5);
        GL11.glVertex2d(x + 5, y - 4);
        GL11.glVertex2d(x + 4, y - 3);
        GL11.glVertex2d(x + 3, y - 2);
        GL11.glVertex2d(x + 2, y - 1);
        GL11.glVertex2d(x + 1, y);
        x += 8;
      } else if (c == '8') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 0);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        x += 8;
      } else if (c == '9') {
        for (int i = 1; i <= 7; i++) {
          GL11.glVertex2d(x + 7, y - i);
        }
        for (int i = 5; i <= 7; i++) {
          GL11.glVertex2d(x + 1, y - i);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 8);
          GL11.glVertex2d(x + i, y - 0);
        }
        for (int i = 2; i <= 6; i++) {
          GL11.glVertex2d(x + i, y - 4);
        }
        GL11.glVertex2d(x + 1, y - 0);
        x += 8;
      } else if (c == '.') {
        GL11.glVertex2d(x + 1, y);
        x += 2;
      } else if (c == ',') {
        GL11.glVertex2d(x + 1, y);
        GL11.glVertex2d(x + 1, y - 1);
        x += 2;
      } else if (c == '\n') {
        y -= 10;
        x = startX;
      } else if (c == ' ') {
        x += 8;
      }
    }
    GL11.glEnd();
  }

}