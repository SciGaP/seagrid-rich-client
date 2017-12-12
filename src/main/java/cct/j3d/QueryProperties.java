/*
 *	@(#)QueryProperties.java 1.11 02/05/29 16:06:47
 *
 * Copyright (c) 1996-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */
package cct.j3d;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.GraphicsConfigTemplate3D;
import org.scijava.java3d.VirtualUniverse;

public class QueryProperties {

  static VirtualUniverse vu = new VirtualUniverse();

  static Map vuMap = VirtualUniverse.getProperties();

  static Map c3dMap = null;

  static GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

  static private QueryProperties props = new QueryProperties();
  static final Logger logger = Logger.getLogger(QueryProperties.class.getCanonicalName());

  public static Map getVirtualUniverseProperties() {
    return vuMap;
  }

  private QueryProperties() {
    template.setStereo(GraphicsConfigTemplate.PREFERRED);
    template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);
    GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
        template);

    c3dMap = new Canvas3D(config).queryProperties();
  }

  public static String getJava3dPropsAsString() {
    StringWriter sWriter = new StringWriter();

    Set set = vuMap.entrySet();
    Iterator i = set.iterator();

    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      sWriter.write(me.getKey().toString() + " = " + me.getValue().toString() + "\n");
    }

    set = c3dMap.entrySet();
    i = set.iterator();
    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      sWriter.write(me.getKey().toString() + " = " + me.getValue().toString() + "\n");
    }

    return sWriter.toString();
  }

  public static void main(String[] args) {

    logger.info("version = " +
                       vuMap.get("j3d.version"));
    logger.info("vendor = " +
                       vuMap.get("j3d.vendor"));
    logger.info("specification.version = " +
                       vuMap.get("j3d.specification.version"));
    logger.info("specification.vendor = " +
                       vuMap.get("j3d.specification.vendor"));
    logger.info("renderer = " +
                       vuMap.get("j3d.renderer") + "\n");

    /* We need to set this to force choosing a pixel format
       that support the canvas.
     */

    logger.info("Renderer version = " +
                       c3dMap.get("native.version"));
    logger.info("doubleBufferAvailable = " +
                       c3dMap.get("doubleBufferAvailable"));
    logger.info("stereoAvailable = " +
                       c3dMap.get("stereoAvailable"));
    logger.info("sceneAntialiasingAvailable = " +
                       c3dMap.get("sceneAntialiasingAvailable"));
    logger.info("sceneAntialiasingNumPasses = " +
                       c3dMap.get("sceneAntialiasingNumPasses"));
    logger.info("textureColorTableSize = " +
                       c3dMap.get("textureColorTableSize"));
    logger.info("textureEnvCombineAvailable = " +
                       c3dMap.get("textureEnvCombineAvailable"));
    logger.info("textureCombineDot3Available = " +
                       c3dMap.get("textureCombineDot3Available"));
    logger.info("textureCombineSubtractAvailable = " +
                       c3dMap.get("textureCombineSubtractAvailable"));
    logger.info("texture3DAvailable = " +
                       c3dMap.get("texture3DAvailable"));
    logger.info("textureCubeMapAvailable = " +
                       c3dMap.get("textureCubeMapAvailable"));
    logger.info("textureSharpenAvailable = " +
                       c3dMap.get("textureSharpenAvailable"));
    logger.info("textureDetailAvailable = " +
                       c3dMap.get("textureDetailAvailable"));
    logger.info("textureFilter4Available = " +
                       c3dMap.get("textureFilter4Available"));
    logger.info("textureAnisotropicFilterDegreeMax = " +
                       c3dMap.get("textureAnisotropicFilterDegreeMax"));
    logger.info("textureBoundaryWidthMax = " +
                       c3dMap.get("textureBoundaryWidthMax"));
    logger.info("textureWidthMax = " +
                       c3dMap.get("textureWidthMax"));
    logger.info("textureHeightMax = " +
                       c3dMap.get("textureHeightMax"));
    logger.info("textureLodOffsetAvailable = " +
                       c3dMap.get("textureLodOffsetAvailable"));
    logger.info("textureLodRangeAvailable = " +
                       c3dMap.get("textureLodRangeAvailable"));
    logger.info("textureUnitStateMax = " +
                       c3dMap.get("textureUnitStateMax"));
    logger.info("compressedGeometry.majorVersionNumber = " +
                       c3dMap.get("compressedGeometry.majorVersionNumber"));
    logger.info("compressedGeometry.minorVersionNumber = " +
                       c3dMap.get("compressedGeometry.minorVersionNumber"));
    logger.info("compressedGeometry.minorMinorVersionNumber = " +
                       c3dMap.get("compressedGeometry.minorMinorVersionNumber"));

    System.exit(0);
  }
}
