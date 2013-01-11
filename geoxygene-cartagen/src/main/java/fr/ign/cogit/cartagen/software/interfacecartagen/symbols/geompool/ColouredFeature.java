/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool;

import java.awt.Color;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LayerManager;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * Extension of a GeOxygene default feature with additional information of
 * associated symbolisation - used for default symbolisation of a geometry in
 * the geometries pool
 * @see LayerManager#getGeometriesPoolLayer()
 * @author JRenard april 2012
 * @author CDuchene may 2012
 */

public class ColouredFeature extends DefaultFeature {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(ColouredFeature.class
      .getName());

  /**
   * Colour of the symbol the feature should be drawn with - used if {@code
   * this.symbolisation} is {@code null}
   */
  private Color symbolColour = null;

  /**
   * A {@link Symbolisation} associated to the feature. If not null the feature
   * will be drawn using the {@code draw()} method of this Symbolisation. If
   * null, the colour or so will be used instead.
   */
  private Symbolisation symbolisation = null;

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Retrieves the colour of the symbol
   */
  public Color getSymbolColour() {
    return this.symbolColour;
  }

  /**
   * Retrieves the symbolisation associated to the feature.
   * @return the symbolisation
   */
  public Symbolisation getSymbolisation() {
    return this.symbolisation;
  }

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * default constructor without colour : the colour will be red
   * @param geom
   */
  public ColouredFeature(IGeometry geom) {
    super(geom);
    this.symbolColour = Color.RED;
  }

  /**
   * Constructor with colour. Sets the colour, symbolisation left empty.
   * @param geom
   * @param colour
   */
  public ColouredFeature(IGeometry geom, Color colour) {
    super(geom);
    if (colour == null) {
      this.symbolColour = Color.RED;
    } else {
      this.symbolColour = colour;
    }
  }

  /**
   * Constructor with a symbolisation. Sets the symbolisation, colour left
   * empty.
   * @param geom the geometry
   * @param symbolisation the symbolisation
   */
  public ColouredFeature(IGeometry geom, Symbolisation symbolisation) {
    super(geom);
    if (symbolisation == null) {
      this.symbolColour = Color.RED;
    } else {
      this.symbolisation = symbolisation;
    }
  }

  /**
   * Constructor with color and width (IN PIXELS). Sets the symbolisation so
   * that: if the geometry is a point, it is drawn as a "+" cross; if it is a
   * line, it is drawn with the colour and width in parameters; if it is a
   * polygon, its border is drawn with the colour and width in parameter and it
   * is filled with the same colour but semi-transparent.
   * 
   * @param geom
   * @param colour
   * @param widthPixels
   */
  public ColouredFeature(IGeometry geom, Color colour, int widthPixels) {
    super(geom);
    // Set colour to red if null
    Color actualColour = colour;
    if (colour == null) {
      actualColour = Color.RED;
    }
    // If not "simple" geom type, assign color and that's all
    if (!(geom instanceof IPolygon) && !(geom instanceof IMultiSurface<?>)
        && !(geom instanceof ILineString) && !(geom instanceof IMultiCurve<?>)
        && !(geom instanceof IPoint)) {
      this.symbolColour = actualColour;
      return;
    }
    // Here we have a "simple" geom type, assign a symbolisation based on the
    // required colour and pixels width
    // IPoint case:
    if (geom instanceof IPoint) {
      this.symbolisation = Symbolisation.pointAsPlusCross(actualColour,
          widthPixels);
      return;
    }
    // Line or Polygon case: assign a symbolisation that draws the (contour)
    // line opaque with required width and color, and the filling if any
    // semi-transparent with the same colour.
    this.symbolisation = Symbolisation.lineOrSurfaceWidthColourTransparency(
        widthPixels, actualColour, 255, actualColour, 120);
  }

}