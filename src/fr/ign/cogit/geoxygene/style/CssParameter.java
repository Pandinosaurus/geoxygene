/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut G�ographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.style;

/**
 * @author Julien Perret
 *
 */
public class CssParameter {
	
	private String name;

	/**
	 * Renvoie la valeur de l'attribut name.
	 * @return la valeur de l'attribut name
	 */
	public String getName() {return this.name;}

	/**
	 * Affecte la valeur de l'attribut name.
	 * @param name l'attribut name � affecter
	 */
	public void setName(String name) {this.name = name;}

	private String value;

	/**
	 * Renvoie la valeur de l'attribut value.
	 * @return la valeur de l'attribut value
	 */
	public String getValue() {return this.value;}

	/**
	 * Affecte la valeur de l'attribut value.
	 * @param value l'attribut value � affecter
	 */
	public void setValue(String value) {this.value = value;}
}
