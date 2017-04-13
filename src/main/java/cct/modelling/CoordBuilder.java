package cct.modelling;

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
public class CoordBuilder {
   String name, description;
   Object builder;
   public CoordBuilder(String name, String description, Object builder) {
      this.name = name;
      this.description = description;
      this.builder = builder;
   }
}
