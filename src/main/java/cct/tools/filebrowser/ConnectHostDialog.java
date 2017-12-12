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

package cct.tools.filebrowser;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 * This dialog is used by other classes for the SSH connection
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class ConnectHostDialog
    extends JDialog implements ActionListener {

   public static final int CONNECT = 0;
   public static final int CANCEL = 1;
   int keyPressed = CONNECT;
   int portNumber = 22;

   JPanel buttonPanel = new JPanel();
   JButton helpButton = new JButton();
   JButton cancelButton = new JButton();
   JButton connectButton = new JButton();
   JPanel jPanel2 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JLabel jLabel1 = new JLabel();
   JTextField portTextField = new JTextField();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JLabel hostLabel = new JLabel();
   JPasswordField passwordTextField = new JPasswordField();
   JTextField userTextField = new JTextField();
   JTextField hostTextField = new JTextField();
   GridBagLayout gridBagLayout1 = new GridBagLayout();

   public ConnectHostDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public ConnectHostDialog() {
      this(new Frame(), "Connect to Host", true);
   }

   private void jbInit() throws Exception {
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      this.getContentPane().setLayout(borderLayout1);
      helpButton.setEnabled(false);
      helpButton.setText("Help");
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      connectButton.setToolTipText("");
      connectButton.setText("Connect");
      jLabel1.setToolTipText("");
      jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel1.setText("Port: ");
      jLabel1.setVerticalAlignment(SwingConstants.TOP);
      jLabel2.setToolTipText("");
      jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel2.setText("Password: ");
      jLabel3.setToolTipText("");
      jLabel3.setText("Username: ");
      hostLabel.setToolTipText("");
      hostLabel.setText("Host address: ");
      jPanel2.setLayout(gridBagLayout1);
      hostTextField.setToolTipText("");
      hostTextField.addActionListener(new ConnectHostDialog_hostTextField_actionAdapter(this));
      userTextField.setToolTipText("Enter your username here");
      userTextField.addActionListener(new ConnectHostDialog_userTextField_actionAdapter(this));
      portTextField.setColumns(6);
      passwordTextField.setToolTipText("Enter your password here");
      passwordTextField.addActionListener(new ConnectHostDialog_passwordTextField_actionAdapter(this));
      buttonPanel.add(connectButton);
      buttonPanel.add(cancelButton);
      buttonPanel.add(helpButton);
      this.getContentPane().add(jPanel2, BorderLayout.CENTER);
      this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
      jPanel2.add(portTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(hostTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(userTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(passwordTextField,
                  new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
                                         , GridBagConstraints.WEST,
                                         GridBagConstraints.HORIZONTAL,
                                         new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(jLabel1, new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0
                                                  ,
                                                  GridBagConstraints.NORTHEAST,
                                                  GridBagConstraints.VERTICAL,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(hostLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));

      connectButton.addActionListener(this);
      cancelButton.addActionListener(this);
   }

   public int showDialog() {
      hostTextField.setText("");
      userTextField.setText("");
      portTextField.setText(String.valueOf(portNumber));
      passwordTextField.setText("");

      hostTextField.grabFocus();
      setVisible(true);
      return keyPressed;
   }

   public int getKeyPressed() {
      return keyPressed;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == cancelButton) {
         keyPressed = CANCEL;
         setVisible(false);
         return;
      }

      try {
         portNumber = Integer.parseInt(portTextField.getText().trim());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       "Wrong port number: " + ex.getMessage(),
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (portNumber < 1) {
         JOptionPane.showMessageDialog(this,
                                       "Port number should be a positive number!",
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }

      keyPressed = CONNECT;
      setVisible(false);
   }

   public String getHost() {
      return hostTextField.getText().trim();
   }

   public String getUsername() {
      return userTextField.getText();
   }

   public char[] getPassword() {
      return passwordTextField.getPassword();
   }

   public int getPort() {
      return this.portNumber;
   }

   public void hostTextField_actionPerformed(ActionEvent e) {
      if (hostTextField.getText().trim().length() > 0) {
         this.userTextField.grabFocus();
      }
   }

   public void userTextField_actionPerformed(ActionEvent e) {
      if (userTextField.getText().trim().length() > 0) {
         this.passwordTextField.grabFocus();
      }
   }

   public void passwordTextField_actionPerformed(ActionEvent e) {
      this.connectButton.doClick();
   }

   private class ConnectHostDialog_passwordTextField_actionAdapter
       implements ActionListener {
      private ConnectHostDialog adaptee;
      ConnectHostDialog_passwordTextField_actionAdapter(ConnectHostDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.passwordTextField_actionPerformed(e);
      }
   }

   private class ConnectHostDialog_userTextField_actionAdapter
       implements ActionListener {
      private ConnectHostDialog adaptee;
      ConnectHostDialog_userTextField_actionAdapter(ConnectHostDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.userTextField_actionPerformed(e);
      }
   }

   private class ConnectHostDialog_hostTextField_actionAdapter
       implements ActionListener {
      private ConnectHostDialog adaptee;
      ConnectHostDialog_hostTextField_actionAdapter(ConnectHostDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.hostTextField_actionPerformed(e);
      }
   }
}
