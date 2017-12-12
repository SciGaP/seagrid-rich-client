package cct.j3d;

/*
 *	@(#)OffScreenCanvas3D.java 1.4 02/04/01 15:04:10
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

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.ImageComponent;
import org.scijava.java3d.ImageComponent2D;

class OffScreenCanvas3D
    extends Canvas3D {
   OffScreenCanvas3D(GraphicsConfiguration graphicsConfiguration,
                     boolean offScreen) {

      super(graphicsConfiguration, offScreen);
   }

   BufferedImage doRender(int width, int height) {

      BufferedImage bImage =
          new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

      ImageComponent2D buffer =
          new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);

      setOffScreenBuffer(buffer);
      renderOffScreenBuffer();
      waitForOffScreenRendering();
      bImage = getOffScreenBuffer().getImage();

      return bImage;
   }

   @Override
  public void postSwap() {
      // No-op since we always wait for off-screen rendering to complete
   }
}
