package cct.interfaces;

import java.io.BufferedReader;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface CoordinateParserInterface {
   void parseCoordinates(BufferedReader in, MoleculeInterface molecule) throws Exception;

   double evaluateCompliance(BufferedReader in) throws Exception;
}
