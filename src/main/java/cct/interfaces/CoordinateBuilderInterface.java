package cct.interfaces;

import java.io.Writer;

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
public interface CoordinateBuilderInterface {

   void getCoordinates(MoleculeInterface mol, boolean inAngstroms, Writer writer) throws Exception;

   String getCoordinatesAsString(MoleculeInterface molec, boolean inAngstroms) throws Exception;
}
