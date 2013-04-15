/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.Set;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.hexagonaltess.HexagonalCell;
import fr.ign.cogit.cartagen.spatialanalysis.hexagonaltess.HexagonalTessellation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * Implementation of the hexagonal tessellation line simplification algorithm
 * from (Raposo 2010). It's the same principle as Li-Openshaw algorithm but with
 * hexagonal tessellation instead of a grid. All options from the paper are
 * implemented. Be careful, it uses the scale as parameter.
 * @author GTouya
 * 
 */
public class RaposoSimplification {

  private boolean method1 = false, toblerRes = false;
  private Double initialScale;
  private HexagonalTessellation tess;

  /**
   * Builds an algorithm instance with a choice for the options of the
   * algorithms.
   * @param method1 true if the first method (use of centroid of the hexagon) is
   *          used, false to use the second (use of the nearest vertex of the
   *          hexagon centroid)
   * @param toblerRes true to compute the width according to Tobler's (87)
   *          rule-of-thumb principle from scale, false to use Raposo's
   *          proposal.
   * @param initialScale the scale of the initial data (useless if toblerRes is
   *          true)
   */
  public RaposoSimplification(boolean method1, boolean toblerRes,
      Double initialScale) {
    this.method1 = method1;
    this.toblerRes = toblerRes;
    this.initialScale = initialScale;
  }

  private double computeToblerWidth() {
    double scale = Legend.getSYMBOLISATI0N_SCALE();
    return scale / 4000 * 5;
  }

  private double computeRaposoWidth(ILineString line) {
    if (initialScale == null)
      return computeToblerWidth();
    double firstFactor = line.length() / line.getSegment().size();
    double secondFactor = Legend.getSYMBOLISATI0N_SCALE() / initialScale;
    return firstFactor * secondFactor;
  }

  public ILineString simplify(ILineString line) {
    IDirectPositionList points = new DirectPositionList();
    // compute the hexagonal tessellation for this line
    double width = computeToblerWidth();
    if (!toblerRes)
      width = computeRaposoWidth(line);
    IEnvelope envelope = line.getEnvelope();
    tess = new HexagonalTessellation(envelope, width);
    int currentIndex = 0;
    HexagonalCell prevCell = null;
    while (currentIndex < line.coord().size() - 1) {
      HexagonalCell current = null;
      // now loop on the vertices from current index
      IMultiPoint ptCloud = new GM_MultiPoint();
      for (int i = currentIndex; i < line.coord().size(); i++) {
        Set<HexagonalCell> containingCells = tess.getContainingCells(line
            .coord().get(i));
        if (current == null) {
          containingCells.remove(prevCell);
          current = containingCells.iterator().next();
          ptCloud.add(line.coord().get(i).toGM_Point());
          continue;
        }
        if (containingCells.contains(current)) {
          ptCloud.add(line.coord().get(i).toGM_Point());
          currentIndex = i + 1;
        } else {
          currentIndex = i;
          prevCell = current;
          break;
        }
      }
      if (method1)
        points.add(ptCloud.centroid());
      else {
        points.add(CommonAlgorithmsFromCartAGen.getNearestVertexFromPoint(line,
            ptCloud.centroid()));
      }
      if (currentIndex == line.coord().size() - 1)
        points.add(line.endPoint());
    }

    // on ajoute le point initial et le point final s'ils n'y sont pas
    if (!points.get(0).equals(line.startPoint()))
      points.add(0, line.startPoint());
    if (!points.get(points.size() - 1).equals(line.endPoint()))
      points.add(0, line.endPoint());
    return new GM_LineString(points);
  }

  public HexagonalTessellation getTess() {
    return tess;
  }

  public void setTess(HexagonalTessellation tess) {
    this.tess = tess;
  }
}