package cct.experimental;

import cct.experimental.AtomList;
import java.util.*;
import javax.swing.table.*;
import javax.swing.JOptionPane;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AtomListTableModel extends AbstractTableModel {

  public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
  private boolean showAtomNumber = false;
  private boolean debug = true;
  AtomList atomList = null;

  public AtomListTableModel() {
  }

  public AtomListTableModel(AtomList atom_list) {
    atomList = atom_list;
  }

  public boolean isShowAtomNumber() {
    return showAtomNumber;
  }

  public void setShowAtomNumber(boolean showAtomNumber) {
    this.showAtomNumber = showAtomNumber;
  }

  public void setZMatrix(AtomList atom_list) {
    atomList = atom_list;
    this.fireTableDataChanged();
  }

  public String getColumnName(int column) {
    if (showAtomNumber) {
      if (column == 0) {
        return "#";
      }
      --column;
    }
    return atomList.getPropertyName(column);
  }

  /**
   * Returns the number of columns in the model.
   *
   * @return the number of columns in the model @todo Implement this javax.swing.table.TableModel method
   */
  public int getColumnCount() {
    return atomList.getPropertiesCount() + (showAtomNumber ? 1 : 0);
  }

  /**
   * Returns the number of rows in the model.
   *
   * @return the number of rows in the model @todo Implement this javax.swing.table.TableModel method
   */
  public int getRowCount() {
    return atomList.getAtomCount();
  }

  public Class getColumnClass(int c) {
    try {
      if (showAtomNumber) {
        if (c == 0) {
          System.err.println("# Col: " + c + " Class: " + new Integer(0).getClass().toString());
          return new Integer(0).getClass();
        }
        --c;
      }
      System.err.println("Col: " + c + " Class: " + getValueAt(0, c).getClass().toString());
      return getValueAt(0, c).getClass();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new Object().getClass();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell @todo Implement this javax.swing.table.TableModel method
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (showAtomNumber) {
      if (columnIndex == 0) {
        System.err.println("# Col: " + columnIndex + " Row: " + rowIndex + " Value: " + new Integer(rowIndex + 1));
        return new Integer(rowIndex + 1);
      }
      --columnIndex;
    }
    System.err.println("Col: " + columnIndex + " Row: " + rowIndex + " Value: " + atomList.getPropertyAt(rowIndex, columnIndex));
    return atomList.getPropertyAt(rowIndex, columnIndex);
  }

  public boolean isCellEditable(int row, int col) {
    //Note that the data/cell address is constant,
    //no matter where the cell appears onscreen.
    if (showAtomNumber) {
      if (col == 0) {
        return false;
      }
      --col;
    }
    return atomList.isPropertyEditable(row, col);
  }

  /*
   * Don't need to implement this method unless your table's data can change.
   */
  public void setValueAt(Object value, int rowIndex, int col) {
    if (showAtomNumber) {
      if (col == 0) {
        return;
      }
      --col;
    }

    if (debug) {
      System.out.println("Setting value at " + rowIndex + "," + col + " to " + value + " (an instance of " + value.getClass()
              + ")");
    }
    atomList.setPropertyValueAt(value, rowIndex, col);

    fireTableCellUpdated(rowIndex, col);

    if (debug) {
      System.out.println("New value of data:" + value.toString());
      //printDebugData();
    }
  }
}
