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

package test.app;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import fr.ign.cogit.geoxygene.util.ImageUtil;

/**
 * @author JeT
 *         a Tile is a small image used to fill textures with
 *         it contains a mask extracted from the transparency 0: pixel 255:no
 *         pixel
 */
public class Tile {

    private BufferedImage image = null;
    private List<Point> borders = null;
    private int size = 0; // number of lighten mask pixels
    private BufferedImage mask = null;
    private BufferedImage border = null;
    private final int transparencyThreshold = 127;
    public static final byte MASK_IN = (byte) 0;
    public static final byte MASK_OUT = (byte) 255;

    /**
     * Constructor
     */
    public Tile() {
    }

    /**
     * Constructor
     */
    public Tile(BufferedImage image) {
        this.setImage(image);
    }

    /**
     * set the tile image. The image is converted to BYTE_ABGR
     * 
     */
    public void setImage(BufferedImage image) {
        this.image = ImageUtil.convert(image, BufferedImage.TYPE_4BYTE_ABGR);
        this.computeMaskAndBorders();
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * read an image as tile
     * 
     * @param filename
     *            file to read
     * @return a newly created tile
     * @throws IOException
     *             on IO Error
     */
    public static Tile read(String filename) throws IOException {
        File f = new File(filename);
        return read(f);
    }

    /**
     * read an image as tile
     * 
     * @param filename
     *            file to read
     * @return a newly created tile
     * @throws IOException
     *             on IO Error
     */
    public static Tile read(File f) throws IOException {
        Tile tile = new Tile();
        tile.setImage(ImageIO.read(f));
        return tile;
    }

    /**
     * @return the borders
     */
    public List<Point> getBorders() {
        return this.borders;
    }

    /**
     * @return the mask
     */
    public BufferedImage getMask() {
        return this.mask;
    }

    /**
     * @return the border
     */
    public BufferedImage getBorder() {
        return this.border;
    }

    /**
     * @return the number of lighten pixels
     */
    public int getSize() {
        return this.size;
    }

    /**
     * create the mask and border of the tile.
     * the tile image transparency is set to binary values (0 || 255)
     * image alpha = 255 (opaque) when mask = 0
     * image alpha = 0 (transparent) when mask = 255
     */
    private void computeMaskAndBorders() {
        if (this.image == null) {
            return;
        }
        int w = this.image.getWidth();
        int h = this.image.getHeight();
        this.borders = new ArrayList<Point>();
        this.size = 0;
        this.mask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        this.border = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        byte[] imagePixels = ((DataBufferByte) this.image.getRaster().getDataBuffer()).getData();
        byte[] maskPixels = ((DataBufferByte) this.mask.getRaster().getDataBuffer()).getData();
        byte[] borderPixels = ((DataBufferByte) this.border.getRaster().getDataBuffer()).getData();

        for (int y = 0, lMask = 0; y < h; y++) {
            for (int x = 0; x < w; x++, lMask++) {
                int lImage = lMask * 4;
                boolean in = (imagePixels[lImage] & 0xFF) > this.transparencyThreshold;
                // border
                if ((x > 0 && (((maskPixels[lMask - 1] & 0xFF) > this.transparencyThreshold) != in))
                        || (x < w - 1 && (((imagePixels[lImage + 4] & 0xFF) > this.transparencyThreshold) != in))
                        || (y > 0 && (((maskPixels[lMask - w] & 0xFF) > this.transparencyThreshold) != in))
                        || (y < h - 1 && (((imagePixels[lImage + 4 * w] & 0xFF) > this.transparencyThreshold) != in))) {
                    this.borders.add(new Point(x, y));
                    borderPixels[lMask] = MASK_IN;

                } else {
                    borderPixels[lMask] = MASK_OUT;
                }

                // mask
                if (in) {
                    this.size++;
                    maskPixels[lMask] = MASK_IN; // mask black : visible pixel
                    imagePixels[lImage] = (byte) 255; // alpha = 255 : pure opaque pixel
                } else {
                    maskPixels[lMask] = MASK_OUT; // mask white : invisible pixel
                    imagePixels[lImage] = (byte) 0; // alpha = 0 : pure transparent pixel 
                }
            }

        }
    }

}
