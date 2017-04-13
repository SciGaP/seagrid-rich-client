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

package cct.grid;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
public class TaskProviderPanel
    extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JComboBox providerComboBox = new JComboBox();
   JPanel providerPanel = new JPanel();
   JPanel jPanel2 = new JPanel();
   FlowLayout flowLayout1 = new FlowLayout();
   JLabel providerLabel = new JLabel();
   CardLayout cardLayout1 = new CardLayout();

   public TaskProviderPanel() {
      try {
         jbInit();
         setTaskProviders(TaskProvider.getAvailableTaskProviders());
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      providerPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      providerLabel.setToolTipText("");
      providerLabel.setText(" Provider: ");
      jPanel2.setLayout(cardLayout1);
      providerComboBox.addActionListener(new
                                         TaskProviderPanel_providerComboBox_actionAdapter(this));
      providerPanel.add(providerLabel);
      providerPanel.add(providerComboBox);
      this.add(jPanel2, BorderLayout.CENTER);
      this.add(providerPanel, BorderLayout.NORTH);
   }

   public void setTaskProviders(TaskProvider providers) {
      setTaskProviders(TaskProvider.getAvailableTaskProviders());
   }

   public String getTaskProvider() {
      return providerComboBox.getSelectedItem().toString();
   }

   public void setTaskProviders(Map providers) {
      providerComboBox.setEnabled(false);
      providerComboBox.removeAllItems();

      Set set = providers.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
         Map.Entry me = (Map.Entry) iter.next();
         String providerName = me.getKey().toString();
         providerComboBox.addItem(providerName);
         Component panel = (Component) me.getValue();

         jPanel2.add(panel, providerName);
      }

      providerComboBox.setSelectedIndex(0);

      CardLayout cl = (CardLayout) jPanel2.getLayout();
      cl.show(jPanel2, providerComboBox.getSelectedItem().toString());

      providerComboBox.setEnabled(true);
   }

   public void providerComboBox_actionPerformed(ActionEvent e) {
      if (!providerComboBox.isEnabled()) {
         return;
      }
      providerComboBox.setEnabled(false);

      CardLayout cl = (CardLayout) jPanel2.getLayout();
      cl.show(jPanel2, providerComboBox.getSelectedItem().toString());

      providerComboBox.setEnabled(true);

   }
}

class TaskProviderPanel_providerComboBox_actionAdapter
    implements ActionListener {
   private TaskProviderPanel adaptee;
   TaskProviderPanel_providerComboBox_actionAdapter(TaskProviderPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.providerComboBox_actionPerformed(e);
   }
}
