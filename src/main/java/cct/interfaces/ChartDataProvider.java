/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.interfaces;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Vlad
 */
public interface ChartDataProvider {

  double[] cdpGetDataAsDouble(String descriptor);

  float[] cdpGetDataAsFloat(String descriptor);

  int[] cdpGetDataAsInteger(String descriptor);

  String[] cdpGetDataAsString(String descriptor);

  Object[] cdpGetDataAsObject(String descriptor);

  String cdpGetDescription();

  Set<String> cdpGetDescriptors();

  void cdpParseData(String filename) throws Exception;

  /**
   * Checks format and returns score (0 to 1 range)
   * @param filename
   * @return 0 - wrong format, 1 - for sure it's this format
   * @throws Exception 
   */
  float cdpIsThisFormat(String filename) throws Exception;
}
