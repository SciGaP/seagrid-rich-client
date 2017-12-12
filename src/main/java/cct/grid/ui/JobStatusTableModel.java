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
package cct.grid.ui;

import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import cct.grid.JobStatusInterface;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class JobStatusTableModel
    extends AbstractTableModel {

  String[] columnNames = null;
  boolean DEBUG = true;
  Object[][] data = null;
  JobStatusInterface jobStatusInterface = null;
  static final Logger logger = Logger.getLogger(JobStatusTableModel.class.getCanonicalName());

  public JobStatusTableModel(Object[][] new_data) {
    data = new_data;
  }

  public JobStatusInterface getJobStatusInterface() {
     return jobStatusInterface;
  }
  public JobStatusTableModel(java.util.List statuses) {
    if (statuses.size() < 1) {
      data = null;
      return;
    }
    columnNames = new String[]{
          "Handle", "Status", "Selection"};
    data = new Object[statuses.size()][];
    for (int i = 0; i < statuses.size(); i++) {
      java.util.List job = (java.util.List) statuses.get(i);
      data[i] = new Object[3];
      data[i][0] = job.get(0); // Job Handle
      data[i][1] = job.get(1); // Job Status as a String
      data[i][2] = new Boolean(false);
    }
  }

  public JobStatusTableModel(JobStatusInterface jsi) {
    jobStatusInterface = jsi;
    columnNames = jsi.getColumnNames();
    data = jsi.getData();
    for (int i = 0; i < data.length; i++) {
      if (columnNames.length != data[i].length) {
        System.err.println("columnNames.length != data[0].length");
      }
    }
  }

  public JobStatusTableModel(String[] cn, Object[][] dat, JobStatusInterface jsi) {
    jobStatusInterface = jsi;
    columnNames = cn;
    data = dat;
    for (int i = 0; i < data.length; i++) {
      if (columnNames.length != data[i].length) {
        System.err.println("columnNames.length != data[0].length");
      }
    }
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    if (data == null) {
      return 0;
    }
    return data.length;
  }

  @Override
  public String getColumnName(int col) {
    return columnNames[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
  @Override
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  /*
   * Don't need to implement this method unless your table's
   * editable.
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    //Note that the data/cell address is constant,
    //no matter where the cell appears onscreen.

    //if (col < 2) {
    return false;
    //}
    //else {
    // return true;
    //}
  }

  /*
   * Don't need to implement this method unless your table's
   * data can change.
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (DEBUG) {
      logger.info("Setting value at " + row + "," + col
          + " to " + value
          + " (an instance of "
          + value.getClass() + ")");
    }

    data[row][col] = value;
    fireTableCellUpdated(row, col);

    if (DEBUG) {
      logger.info("New value of data:");
      printDebugData();
    }
  }

  private void printDebugData() {
    int numRows = getRowCount();
    int numCols = getColumnCount();

    for (int i = 0; i < numRows; i++) {
      System.out.print("    row " + i + ":");
      for (int j = 0; j < numCols; j++) {
        System.out.print("  " + data[i][j]);
      }
      System.out.print("\n");
    }
    logger.info("--------------------------");
  }
}
