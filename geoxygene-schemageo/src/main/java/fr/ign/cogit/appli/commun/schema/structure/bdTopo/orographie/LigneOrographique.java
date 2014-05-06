package fr.ign.cogit.appli.commun.schema.structure.bdTopo.orographie;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class LigneOrographique extends ElementBDTopo {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected double z_min;
	public double getZ_min() {return this.z_min; }
	public void setZ_min (double Z_min) {z_min = Z_min; }

	protected double z_max;
	public double getZ_max() {return this.z_max; }
	public void setZ_max (double Z_max) {z_max = Z_max; }

}