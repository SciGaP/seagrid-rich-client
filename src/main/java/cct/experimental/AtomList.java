/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.experimental;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlad
 */
public class AtomList {

  public enum PREDEFINED_PROPERTY {

    ELEMENT, X, Y, Z, NAME, ATOMIC_MASS
  }
  private MoleculeInterface molecule;
  private Set<String> uniqueProperties;
  private List<String> uniquePropertiesList = new ArrayList<String>();
  private List<Map<String, AtomProperty>> atoms = new ArrayList<Map<String, AtomProperty>>();
  private boolean showAtomNumber = true;
  static final Logger logger = Logger.getLogger(AtomList.class.getCanonicalName());

  public AtomList() {
  }

  public AtomList(MoleculeInterface molecule) {
    this.molecule = molecule;
  }

  public void setMolecule(MoleculeInterface molecule) {
    this.molecule = molecule;
  }

  protected void addProperty(String name) {
    uniqueProperties.add(name);
    uniquePropertiesList.add(name);
  }

  protected void removeProperty(String name) {
    uniqueProperties.remove(name);
    uniquePropertiesList.remove(name);
  }

  public Set<String> getProperties() {
    return new LinkedHashSet<String>(uniqueProperties);
  }

  public List<String> getPropertiesList() {
    return new ArrayList<String>(uniquePropertiesList);
  }

  public void setPropertyValueAt(Object value, int atomIndex, int propIndex) {
    atoms.get(atomIndex).get(uniquePropertiesList.get(propIndex)).setValue(value);
  }

  public boolean isPropertyEditable(int atomIndex, int propIndex) {
    return atoms.get(atomIndex).get(uniquePropertiesList.get(propIndex)).isEditable();
  }

  public void buildAtomList() {

    uniqueProperties = countUniqueAtomsProperties(molecule);
    uniquePropertiesList.clear();
    uniquePropertiesList.addAll(uniqueProperties);

    atoms.clear();

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      Map<String, AtomProperty> props = new LinkedHashMap<String, AtomProperty>();
      AtomProperty ap = new AtomProperty(PREDEFINED_PROPERTY.ELEMENT.name(), atom.getAtomicNumber(), true);
      props.put(PREDEFINED_PROPERTY.ELEMENT.name(), ap);
      ap = new AtomProperty(PREDEFINED_PROPERTY.X.name(), atom.getX(), true);
      props.put(PREDEFINED_PROPERTY.X.name(), ap);
      ap = new AtomProperty(PREDEFINED_PROPERTY.Y.name(), atom.getY(), true);
      props.put(PREDEFINED_PROPERTY.Y.name(), ap);
      ap = new AtomProperty(PREDEFINED_PROPERTY.Z.name(), atom.getZ(), true);
      props.put(PREDEFINED_PROPERTY.Z.name(), ap);
      ap = new AtomProperty(PREDEFINED_PROPERTY.NAME.name(), atom.getName() == null ? "" : atom.getName(), true);
      props.put(PREDEFINED_PROPERTY.NAME.name(), ap);
      ap = new AtomProperty(PREDEFINED_PROPERTY.ATOMIC_MASS.name(), atom.getAtomicMass(), true);
      props.put(PREDEFINED_PROPERTY.ATOMIC_MASS.name(), ap);

      for (String prop : uniqueProperties) {
        if (atom.getProperty(prop) == null) {
          continue;
        }
        ap = new AtomProperty(prop, atom.getProperty(prop), true);
        props.put(prop, ap);
      }

      atoms.add(props);
    }

    if (logger.isLoggable(Level.INFO)) {
      logger.log(Level.INFO, toString());
    }
  }

  public Set<String> countUniqueAtomsProperties(MoleculeInterface molecule) {
    Set<String> uProperties = new LinkedHashSet<String>();

    uProperties.add(PREDEFINED_PROPERTY.ELEMENT.name());
    uProperties.add(PREDEFINED_PROPERTY.X.name());
    uProperties.add(PREDEFINED_PROPERTY.Y.name());
    uProperties.add(PREDEFINED_PROPERTY.Z.name());
    uProperties.add(PREDEFINED_PROPERTY.NAME.name());
    uProperties.add(PREDEFINED_PROPERTY.ATOMIC_MASS.name());

    Set<String> temp = new HashSet<String>();
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      Map< String, Object> props = atom.getProperties();
      for (Map.Entry<String, Object> entry : props.entrySet()) {
        if (!temp.contains(entry.getKey())) {
          temp.add(entry.getKey());
        }
      }
    }
    System.out.println("Found " + temp.size() + " non-predefined properties");

    Object[] obj = temp.toArray();
    Arrays.sort(obj);

    int n = 0;
    for (Object o : obj) {
      uProperties.add(o.toString());
      System.out.println((n++) + ": " + o.toString());
    }
    return uProperties;
  }

  public int getPropertiesCount() {
    if (uniqueProperties == null) {
      return 0;
    }
    return this.uniqueProperties.size();
  }

  public String getPropertyName(int index) {
    return uniquePropertiesList.get(index);
  }

  public int getAtomCount() {
    return atoms.size();
  }

  public Object getPropertyAt(int atomIndex, int propIndex) {
    return atoms.get(atomIndex).get(uniquePropertiesList.get(propIndex)).getValue();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Number of unique atomic properties: " + uniqueProperties.size() + "\n");
    int n = 1;
    for (String prop : uniqueProperties) {
      sb.append((n++) + ": " + prop + "\n");
    }
    sb.append("\nNumber of atoms: " + atoms.size() + "\n");
    //
    n = 1;
    for (Map<String, AtomProperty> atom : atoms) {

      sb.append(String.format("%-3d", n++) + "\n");

      for (Map.Entry<String, AtomProperty> entry : atom.entrySet()) {
        sb.append("  " + entry.getKey() + ": " + entry.getValue().getValue() + " " + entry.getValue().isEditable() + "\n");
      }

    }
    return sb.toString();
  }

  public boolean isShowAtomNumber() {
    return showAtomNumber;
  }

  public void setShowAtomNumber(boolean showAtomNumber) {
    this.showAtomNumber = showAtomNumber;
  }

  public class AtomProperty {

    String propertyTag;
    Object value;
    boolean editable;

    public AtomProperty(String propertyTag, Object value, boolean editable) {
      this.propertyTag = propertyTag;
      this.value = value;
      this.editable = editable;
    }

    public boolean isEditable() {
      return editable;
    }

    public void setEditable(boolean editable) {
      this.editable = editable;
    }

    public String getPropertyTag() {
      return propertyTag;
    }

    public void setPropertyTag(String propertyTag) {
      this.propertyTag = propertyTag;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }
  }
}
