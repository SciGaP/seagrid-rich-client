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

package cct.dialogs;

import cct.resources.images.ImageResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
public class JAtomTypesPanel
    extends JPanel {

  static ImageIcon emptyIcon = new ImageIcon(ImageResources.class.getResource(
      "icons48x48/emptyTransparent.png"));
  java.util.List buttons = new ArrayList();
  int activeButtons = 0;
  ActionListener listener;
  FlowLayout flowLayout1 = new FlowLayout(FlowLayout.LEFT);
  static final Logger logger = Logger.getLogger(JAtomTypesPanel.class.getCanonicalName());

  public JAtomTypesPanel(ActionListener l) {
    listener = l;
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public JAtomTypesPanel() {

  }

  public void setAtomTypes(Map atomTypes) {
    activeButtons = 0;

    if (buttons.size() > atomTypes.size()) {
      for (int i = buttons.size() - 1; i >= atomTypes.size(); i--) {
        JButton butt = (JButton) buttons.get(i);
        butt.removeActionListener(listener);
        butt.setEnabled(false);
        butt.setVisible(false);
        butt.validate();
      }
    }

    int count = 0;
    Set set = atomTypes.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      ++activeButtons;
      Map.Entry me = (Map.Entry) iter.next();
      String type = me.getKey().toString();
      ImageIcon image = (ImageIcon) me.getValue();

      if (count >= buttons.size()) {
        logger.info("Adding new button: " + type);
        JButton temp = new JButton(type, image);
        temp.setHorizontalAlignment(SwingConstants.CENTER);
        temp.setHorizontalTextPosition(SwingConstants.CENTER);
        temp.setVerticalAlignment(SwingConstants.CENTER);
        temp.setVerticalTextPosition(SwingConstants.CENTER);
        temp.setMargin(new Insets(0, 0, 0, 0));
        temp.setSize(50, 50);
        temp.setPreferredSize(new Dimension(50, 50));
        temp.setFont(new Font("Monospaced", Font.BOLD, 12));
        temp.setActionCommand(type);
        temp.addActionListener(listener);
        buttons.add(temp);
        add(temp);
        temp.validate();
      }
      else {
        logger.info("Modifying old button: " + type);
        JButton butt = (JButton) buttons.get(count);
        butt.setIcon(image);
        butt.setText(type);
        butt.setActionCommand(type);
        butt.setSelected(false);
        butt.setEnabled(true);
        butt.setVisible(true);
        butt.addActionListener(listener);
        butt.validate();
      }
      ++count;
    }
    validate();
  }

  public void setSelectedButton(int n) {
    if (n >= buttons.size()) {
      System.err.println(getClass().getCanonicalName() +
                         ": n >= buttons.size():" + n + " & " + buttons.size() +
                         " Ignored");
      return;
    }

    for (int i = 0; i < activeButtons; i++) {
      JButton butt = (JButton) buttons.get(i);
      if (i == n) {
        butt.setSelected(true);
      }
      else {
        butt.setSelected(false);
      }
    }
  }

  public void setSelectedButton(String actionCom) {

    for (int i = 0; i < activeButtons; i++) {
      JButton butt = (JButton) buttons.get(i);
      if (actionCom.equalsIgnoreCase(butt.getActionCommand())) {
        butt.setSelected(true);
        butt.validate();
      }
      else {
        butt.setSelected(false);
        butt.validate();
      }
    }

  }

  private void jbInit() throws Exception {
    this.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);

    JButton temp = new JButton(emptyIcon);
    temp.setHorizontalAlignment(SwingConstants.CENTER);
    temp.setHorizontalTextPosition(SwingConstants.CENTER);
    temp.setMargin(new Insets(0, 0, 0, 0));
    temp.setVerticalAlignment(SwingConstants.CENTER);
    temp.setVerticalTextPosition(SwingConstants.CENTER);
    temp.setSize(50, 50);
    temp.setFont(new Font("Monospaced", Font.BOLD, 12));
    temp.setPreferredSize(new Dimension(50, 50));
    //temp.setActionCommand(type);
    temp.addActionListener(listener);
    buttons.add(temp);
    this.add(temp);

    for (int i = 0; i < 5; i++) {
      temp = new JButton(emptyIcon);
      temp.setHorizontalAlignment(SwingConstants.CENTER);
      temp.setHorizontalTextPosition(SwingConstants.CENTER);
      temp.setMargin(new Insets(0, 0, 0, 0));
      temp.setVerticalAlignment(SwingConstants.CENTER);
      temp.setVerticalTextPosition(SwingConstants.CENTER);
      temp.setSize(50, 50);
      temp.setFont(new Font("Monospaced", Font.BOLD, 12));
      temp.setPreferredSize(new Dimension(50, 50));
      //temp.setActionCommand(type);
      temp.addActionListener(listener);
      temp.setVisible(false);
      buttons.add(temp);
      add(temp);

    }
    //validate();

  }
}
