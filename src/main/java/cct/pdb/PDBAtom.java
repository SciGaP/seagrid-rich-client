package cct.pdb;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDBAtom {
     String name;
     String cctAtomType;
     int element;

     public PDBAtom(String name, int element, String cct_atom_type) {
        this.name = name.toUpperCase();
        this.element = element;
        cctAtomType = cct_atom_type;
     }
   }
