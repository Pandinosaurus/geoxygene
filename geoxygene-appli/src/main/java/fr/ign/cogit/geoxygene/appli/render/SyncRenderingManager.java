/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.style.Layer;
import groovy.ui.Console;

/**
 * A rendering manager responsible for rendering layers in a {@link LayerViewPanel}.
 * @author Julien Perret
 */
public class SyncRenderingManager implements RenderingManager {

  private static final Logger logger = Logger.getLogger(SyncRenderingManager.class.getName()); // logger

  private LayerViewGLPanel layerViewPanel = null; // managed LayerViewPanel
  private RenderingType renderingType = null;
  private final LinkedHashMap<Layer, LayerRenderer> rendererMap = new LinkedHashMap<Layer, LayerRenderer>(); // Insertion-ordered map between a layer and its renderer.
  private SelectionRenderer selectionRenderer = null; // The selection renderer used to render the selected features.

  /**
   * Constructor of Rendering manager.
   * @param theLayerViewPanel the panel the rendering manager draws into
   * @param renderingType in sync rendering manager only JOGL and LWJGL types are allowed
   */
  public SyncRenderingManager(final LayerViewPanel theLayerViewPanel, final RenderingType renderingType) {
    this.setLayerViewPanel(theLayerViewPanel);
    if (renderingType != RenderingType.LWJGL) {
      throw new IllegalStateException("in sync rendering manager only LWJGL types are allowed (not " + renderingType + ")");
    }
    this.renderingType = renderingType;
    this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#setLayerViewPanel(fr.ign.cogit.geoxygene.appli.layerview.LayerViewPanel)
   */
  @Override
  public final void setLayerViewPanel(final LayerViewPanel aLayerViewPanel) {
    this.layerViewPanel = (LayerViewGLPanel) aLayerViewPanel;
  }

  /** @return The managed {@link LayerViewPanel} panel. */
  public final LayerViewGLPanel getLayerViewPanel() {
    return this.layerViewPanel;
  }

  /** @return The selection renderer used to render the selected features */
  @Override
  public SelectionRenderer getSelectionRenderer() {
    return this.selectionRenderer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getRenderers()
   */
  @Override
  public final Collection<LayerRenderer> getRenderers() {
    return this.rendererMap.values();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#renderAll()
   */
  @Override
  public final void renderAll() {
    synchronized (this.getLayerViewPanel().getProjectFrame().getSld()) {

      // render all layers
      for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld().getLayers()) {
        if (layer.isVisible()) {
          this.render(this.rendererMap.get(layer));
        }
      }
    }
    this.render(this.selectionRenderer);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#addLayer(fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final void addLayer(final Layer layer) {
    if (this.rendererMap.get(layer) == null) {
      LayerRenderer renderer = null;
      switch (this.renderingType) {
      case LWJGL:
        renderer = new LwjglLayerRenderer(layer, this.getLayerViewPanel());
        break;
      default:
        logger.error("Cannot handle rendering type " + this.renderingType + " in " + this.getClass().getSimpleName());
        return;
      }
      this.rendererMap.put(layer, renderer);
      // Adding the layer legend panel to the listeners of the renderer
      renderer.addActionListener(this.getLayerViewPanel().getProjectFrame().getLayerLegendPanel());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#removeLayer(fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final void removeLayer(final Layer layer) {
    if (this.rendererMap.get(layer) == null) {
      return;
    }
    this.rendererMap.remove(layer);
  }

  private static int n = 0;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render(fr.ign.cogit.geoxygene.appli.render.Renderer)
   */
  @Override
  public void render(final LayerRenderer renderer) {
    // if the renderer is already rendering, interrupt the current
    // rendering to start a new one
    if (renderer == null) {
      return;
    }
    if (false) {
      Console console = new Console();
      console.run();
      console.getInputEditor().getTextEditor().setText("console #" + n);
      System.err.println("******************************************* " + n + " ********************************");
      n++;
      Thread.dumpStack();
    }
    // create a new runnable for the rendering
    Runnable runnable = renderer.createRunnable();
    if (runnable != null) {
      try {
        runnable.run(); // do not launch runnable into a thread, just call the run method synchronously
      } catch (Exception e) {
        logger.error("An error occurred during Sync Rendering : " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render(java.lang.String)
   */
  @Override
  public void render(final String layerName) {
    for (Layer layer : this.getLayers()) {
      if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
        this.render(this.getRenderer(layer));
        return;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getLayers()
   */
  @Override
  public final Collection<Layer> getLayers() {
    return this.rendererMap.keySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getRenderer(fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final LayerRenderer getRenderer(final Layer layer) {
    return this.rendererMap.get(layer);
  }

  //  /**
  //   * Copy the rendered images to a 2D graphics in the same order the layers were added to the manager.
  //   * @param destination a 2D graphics to copy the images to
  //   */
  //  public final void copyTo(final Graphics2D destination) {
  //    for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld().getLayers()) {
  //      if (layer.getOpacity() > 0.0d && this.rendererMap.get(layer) != null) {
  //        this.rendererMap.get(layer).copyTo(destination);
  //      }
  //    }
  //    this.selectionRenderer.copyTo(destination);
  //  }

  /** Dispose of the manager. Cleans up all threads, renderers, daemons, etc. */
  @Override
  public final void dispose() {
    this.rendererMap.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#repaint()
   */
  @Override
  public void repaint() {
    if (SyncRenderingManager.logger.isTraceEnabled()) {
      SyncRenderingManager.logger.trace(this.getRenderers().size() + " renderers"); //$NON-NLS-1$
    }
    // then we check if there is still a renderer working
    for (LayerRenderer r : this.getRenderers()) {
      if (r.isRendering() || !r.isRendered()) {
        //        SyncRenderingManager.logger.debug("Renderer " + r.isRendering() + " - " //$NON-NLS-1$ //$NON-NLS-2$
        // + r.isRendered());
        return;
      }
    }
    if (this.selectionRenderer != null && (this.selectionRenderer.isRendering() || !this.selectionRenderer.isRendered())) {
      if (SyncRenderingManager.logger.isTraceEnabled()) {
        SyncRenderingManager.logger.trace("Renderer " //$NON-NLS-1$
            + this.selectionRenderer.isRendering() + " - " //$NON-NLS-1$
            + this.selectionRenderer.isRendered());
      }
      return;
    }
    if (SyncRenderingManager.logger.isTraceEnabled()) {
      SyncRenderingManager.logger.trace("Repaint"); //$NON-NLS-1$
    }
    // nothing is being rendered, we can actually repaint the panel
    SyncRenderingManager.this.getLayerViewPanel().superRepaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#isRendering()
   */
  @Override
  public boolean isRendering() {
    // rendering is not asynchronous
    return false;
  }
}
