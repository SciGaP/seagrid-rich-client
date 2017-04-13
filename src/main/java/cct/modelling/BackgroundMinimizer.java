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
package cct.modelling;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import cct.interfaces.MinimizeProgressInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.ParentInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.ui.MinimizationProgressDialog;
import cct.tools.ui.JShowText;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class BackgroundMinimizer
        extends cct.tools.SwingWorker implements MinimizeProgressInterface {

  ParentInterface parentProcess = null;
  MinimizeStructure mS = null;
  Object ID;
  MinimizationProgressDialog dial = null;
  Component component = null;
  Frame parentFrame = null;
  boolean minimizationCancelled = false;
  int frequencyUpdate_for_variables = 1;
  Java3dUniverse visualizer = null;

  public BackgroundMinimizer(Object id, ParentInterface parent, MinimizeStructure ms) {
    super();
    parentProcess = parent;
    component = parent.getComponent();
    parentFrame = parent.getParentFrame();
    if (parentFrame == null) {
      parentFrame = new Frame();
    }
    if (component == null) {
      component = parentFrame;
    }
    mS = ms;
    ID = id;
    dial = new MinimizationProgressDialog(parentFrame, "Optimization Progress...", false);
    dial.getCancelButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelMinimization();
      }
    });
    dial.setLocationByPlatform(true);
  }

  @Override
  public Object construct() {
    try {
      mS.minimize();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(parentFrame, ex.getMessage(),
              "Error minimizing Energy",
              JOptionPane.ERROR_MESSAGE);
      return ex.getMessage();
    }

    dial.setVisible(false);

    JShowText show = new JShowText("Minimization Info");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream store = System.out;
    System.setOut(new PrintStream(out));
    mS.printMinimizationSummary();
    show.setText(out);
    show.setSize(500, 300);
    //show.setLocationRelativeTo(this);
    show.setLocationByPlatform(true);
    show.setVisible(true);
    System.setOut(store);

    parentProcess.childFinished(ID);
    return this;
  }

  public void setVisualizer(Java3dUniverse v) {
    visualizer = v;
  }

  public MoleculeInterface getMolecule() {
    if (mS == null) {
      return null;
    }
    return mS.getMolecule();
  }

  public void cancelMinimization() {
    minimizationCancelled = true;
    //parentProcess.childFinished(ID);
    //dial.setVisible(false);
    //interrupt();
  }

  @Override
  public void minimizationStarted(String _started) {
    dial.setProgress(_started);
    dial.validate();
    dial.pack();
    dial.setVisible(true);
  }

  @Override
  public boolean isMinimizationCancelled() {
    return minimizationCancelled;
  }

  @Override
  public void minimizationProgressed(String _progress) {
    dial.setProgress(_progress);
    dial.validate();
    dial.pack();
  }

  @Override
  public void minimizationCompleted() {
    dial.setVisible(false);
  }

  @Override
  public int getVariablesFrequencyUpdate() {
    return frequencyUpdate_for_variables;
  }

  @Override
  public void setVariablesFrequencyUpdate(int n) {
    frequencyUpdate_for_variables = n;
  }

  @Override
  public void updateVariables(int n, float x[]) {
    if (visualizer == null) {
      return;
    }
    visualizer.updateMolecularGeometry();
    //float[][] coords = mS.getCoordinates(n,x);
  }
}
