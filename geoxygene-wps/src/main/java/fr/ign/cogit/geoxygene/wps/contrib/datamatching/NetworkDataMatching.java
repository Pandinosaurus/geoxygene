package fr.ign.cogit.geoxygene.wps.contrib.datamatching;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;

/**
*
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL-C_fr.html
*        see Licence_CeCILL-C_en.html
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
* 
* 
* @copyright IGN
* 
* @author M-D Van Damme
* 
* Network data matching process.
* return 
*/
@DescribeProcess(title = "NetworkDataMatching", description = "Do network data matching")
public class NetworkDataMatching implements GeoServerProcess {

  private final static Logger LOGGER = Logger
      .getLogger(NetworkDataMatching.class.getName());

  @DescribeResult(name = "popApp", description = "network Matched")
  public SimpleFeatureCollection execute(
      @DescribeParameter(name = "popRef", description = "Less detailed network") SimpleFeatureCollection popRef,
      @DescribeParameter(name = "popComp", description = "Comparison network") SimpleFeatureCollection popComp,

      // defaultValue = 50
      @DescribeParameter(name = "distanceNoeudsMax", description = "Distance maximale autorisée entre deux noeuds appariés") float distanceNoeudsMax,
      // defaultValue = 10
      @DescribeParameter(name = "distanceArcsMin", description = "Distance minimum sous laquelle l'écart de distance " +
      		"pour divers arcs du réseaux comp (distance vers les arcs du réseau ref) n'a plus aucun sens.") float distanceArcsMin,
      // defaultValue = 25
      @DescribeParameter(name = "distanceArcsMax", description = "Distance maximum autorisée entre les arcs des deux réseaux") float distanceArcsMax) {

    LOGGER.info("Start Converting");
    IFeatureCollection<?> gPopRef = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popRef);
    IFeatureCollection<?> gPopComp = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popComp);
    LOGGER.info("End Converting");

    ParametresApp param = new ParametresApp();

    param.populationsArcs1.add(gPopRef);
    param.populationsArcs2.add(gPopComp);

    param.topologieFusionArcsDoubles1 = true;
    param.topologieFusionArcsDoubles2 = true;
    param.topologieGraphePlanaire1 = true;
    param.topologieGraphePlanaire2 = true;
    param.topologieSeuilFusionNoeuds2 = 0.1;
    param.varianteFiltrageImpassesParasites = false;
    param.projeteNoeuds1SurReseau2 = false;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1 = false;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    param.varianteForceAppariementSimple = true;
    param.distanceArcsMax = distanceArcsMax; // 50
    param.distanceArcsMin = distanceArcsMin; // 30
    param.distanceNoeudsMax = distanceNoeudsMax;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugTirets = false;
    param.debugBilanSurObjetsGeo = false;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugAffichageCommentaires = 2;

    try {

      List<ReseauApp> reseaux = new ArrayList<ReseauApp>();

      LOGGER.info("Start network data matching");
      EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, reseaux);
      LOGGER.info("End network data matching");

      LOGGER.info("Start recalage");
      CarteTopo reseauRecale = Recalage.recalage(reseaux.get(0), reseaux.get(1), liens);
      LOGGER.info("End recalage");

      // Get links
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();

      // Convert to geoserver object
      SimpleFeatureCollection correctedNetwork = GeOxygeneGeoToolsTypes.convert2FeatureCollection(arcs, popRef.getSchema()
        .getCoordinateReferenceSystem());
        
      return correctedNetwork;

    } catch (Exception e) {
      e.printStackTrace();
    }

    LOGGER.info("Failed data matching");
    return null;
  }

}