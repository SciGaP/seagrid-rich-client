/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.j3d;

import java.util.ArrayList;
import java.util.List;

import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class ColorRangeScheme {
  float fMin, fMax;
  private float funAbsMax;
  private float minClipValue, maxClipValue;
  boolean useAbsMax = false;

  color3fPalette defaultPalette = new color3fPalette();

  /**
   *
   * @param min float -  min function value on vertices
   * @param max float -  max function value on vertices
   */
  public ColorRangeScheme(float min, float max) {
    fMin = min;
    fMax = max;

    if (useAbsMax) {
      if (fMin < 0 && fMax > 0) {
        funAbsMax = Math.max(Math.abs(fMin), fMax);
        minClipValue = -funAbsMax;
        maxClipValue = funAbsMax;
      } else if (fMin >= 0) {
        funAbsMax = fMax;
        minClipValue = fMin;
        maxClipValue = fMax;
      } else if (fMax <= 0) {
        funAbsMax = -fMin;
        minClipValue = fMin;
        maxClipValue = fMax;
      }
    } else {
      if (fMin < 0 && fMax > 0) {
        funAbsMax = Math.min(Math.abs(fMin), fMax);
        minClipValue = -funAbsMax;
        maxClipValue = funAbsMax;
      } else if (fMin >= 0) {
        funAbsMax = fMax;
        minClipValue = fMin;
        maxClipValue = fMax;
      } else if (fMax <= 0) {
        funAbsMax = -fMin;
        minClipValue = fMin;
        maxClipValue = fMax;
      }

    }

    defaultPalette.addColor(new Color3f(0, 0, 1)); // Blue
    defaultPalette.addColor(new Color3f(0, 0.5f, 1)); //
    defaultPalette.addColor(new Color3f(0, 1, 0)); // Green
    defaultPalette.addColor(new Color3f(0.5f, 1, 0)); // greenyellow(SVG) rgb(173, 255, 47)
    defaultPalette.addColor(new Color3f(1, 0, 0)); // Red
  }

  public float getAbsRange() {
    return funAbsMax;
  }

  public float getClipMin() {
    return minClipValue;
  }

  public float getClipMax() {
    return maxClipValue;
  }

  public void setClipMin(float value) {
    minClipValue = value;
  }

  public void setClipMax(float value) {
    maxClipValue = value;
  }

  public float getMinFunValue() {
    return fMin;
  }

  public float getMaxFunValue() {
    return fMax;
  }

  public void getColor3f(float fValue, Color3f color) {

    //float range = (fValue + funAbsMax) / (2.0f * funAbsMax);
    float range = (fValue - minClipValue) / (maxClipValue - minClipValue);
    Color3f intepolated = defaultPalette.getColor3fInRange(range);

    color.x = intepolated.x;
    color.y = intepolated.y;
    color.z = intepolated.z;
  }

  public int getColorRRGGBB(float fValue) {

    //float range = (fValue + funAbsMax) / (2.0f * funAbsMax);
    float range = (fValue - minClipValue) / (maxClipValue - minClipValue);
    Color3f intepolated = defaultPalette.getColor3fInRange(range);

    int color = intepolated.get().getRed();
    color <<= 8;
    color |= intepolated.get().getGreen();
    color <<= 8;
    color |= intepolated.get().getBlue();
    return color;
  }

  public int getColorAARRGGBB(float fValue) {
    float factor = 75.0f;
    int color = 0;
    float absMax = Math.min(Math.abs(fMin), Math.abs(fMax));

    if (fMin < 0 && fMax > 0) {
      if (fValue >= 0) {
        color = (int) (fValue / absMax * 255.0f / factor);
        if (color > 255) {
          color = 255;
        }
        color <<= 8;
        color |= 255;
        color <<= 16;
      } else {
        color = (int) ( -fValue / absMax * 255.0f / factor);
        if (color > 255) {
          color = 255;
        }
        color <<= 24;
        color |= 255;
      }
    }

    else if (fMin < 0) {
      color = (int) (fValue / absMax * 255.0f / factor);
      if (color > 255) {
        color = 255;
      }
      color <<= 24;
      color |= 255;
    } else {
      color = (int) (fValue / absMax * 255.0f / factor);
      if (color > 255) {
        color = 255;
      }
      color <<= 8;
      color |= 255;
      color <<= 16;
    }

    if (true) {
      return color;
    }

    float range = (fValue - minClipValue) / (maxClipValue - minClipValue);
    Color3f intepolated = defaultPalette.getColor3fInRange(range);

    color = intepolated.get().getRed();
    color <<= 8;
    color |= intepolated.get().getGreen();
    color <<= 8;
    color |= intepolated.get().getBlue();
    return color;
  }


  public void getColor4f(float fValue, Color4f color) {

    //float range = (fValue + funAbsMax) / (2.0f * funAbsMax);
    float range = (fValue - minClipValue) / (maxClipValue - minClipValue);
    Color3f intepolated = defaultPalette.getColor3fInRange(range);

    color.x = intepolated.x;
    color.y = intepolated.y;
    color.z = intepolated.z;
  }

  public List<Color3f> getColor3fPalette() {
    return defaultPalette.getColor3fPalette();
  }

}


class color3fPalette {
  List<Color3f> Colors = new ArrayList<Color3f>();

  public void addColor(Color3f color) {
    Colors.add(color);
  }

  public Color3f getColor3fInRange(float range) {
    if (range < 0) {
      range = 0;
    } else if (range > 1) {
      range = 1;
    }

    float step = 1.0f / (Colors.size() - 1);
    int start_index = (int) (range / step);
    int end_index = start_index;
    if (end_index < Colors.size() - 2) {
      ++end_index;
    }

    Color3f start_color = Colors.get(start_index);
    Color3f end_color = Colors.get(end_index);

    Color3f interpolatedColor = new Color3f();

    float delta = range - start_index * step;
    interpolatedColor.x = start_color.x + delta / step * (end_color.x - start_color.x);
    interpolatedColor.y = start_color.y + delta / step * (end_color.y - start_color.y);
    interpolatedColor.z = start_color.z + delta / step * (end_color.z - start_color.z);

    return interpolatedColor;
  }

  public List<Color3f> getColor3fPalette() {
    return Colors;
  }
}
