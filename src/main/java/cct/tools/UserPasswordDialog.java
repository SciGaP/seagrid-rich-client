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
package cct.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
public class UserPasswordDialog
    extends JDialog {

  boolean OKpressed = false;
  JPanel panel1 = new JPanel();
  JPanel buttonPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JTextField userTextField = new JTextField();
  JPasswordField jPasswordField1 = new JPasswordField();
  JLabel jLabel2 = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel messageLabel = new JLabel();

  public UserPasswordDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public UserPasswordDialog() {
    this(new Frame(), "Enter your username and password", true);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    this.addKeyListener(new UserPasswordDialog_this_keyAdapter(this));
    this.getContentPane().setLayout(borderLayout2);
    cancelButton.setToolTipText("Cancel dialog");
    cancelButton.setText("Cancel");
    cancelButton.addKeyListener(new UserPasswordDialog_cancelButton_keyAdapter(this));
    cancelButton.addActionListener(new UserPasswordDialog_cancelButton_actionAdapter(this));
    okButton.setMaximumSize(new Dimension(65, 23));
    okButton.setMinimumSize(new Dimension(65, 23));
    okButton.setPreferredSize(new Dimension(65, 23));
    okButton.setToolTipText("Confirm Username and Password");
    okButton.setText("OK");
    okButton.addKeyListener(new UserPasswordDialog_okButton_keyAdapter(this));
    okButton.addActionListener(new UserPasswordDialog_okButton_actionAdapter(this));
    jLabel1.setToolTipText("");
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Password:");
    jLabel2.setToolTipText("");
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("User Name: ");
    userTextField.setColumns(15);
    userTextField.addActionListener(new UserPasswordDialog_jTextField1_actionAdapter(this));
    jPasswordField1.setColumns(15);
    jPasswordField1.addKeyListener(new UserPasswordDialog_jPasswordField1_keyAdapter(this));
    jPasswordField1.addActionListener(new UserPasswordDialog_jPasswordField1_actionAdapter(this));
    messageLabel.setToolTipText("");
    messageLabel.setText("Enter username & password:");
    messageLabel.setVisible(false);
    this.getContentPane().add(panel1, BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    this.getContentPane().add(messageLabel, BorderLayout.NORTH);
    panel1.add(jLabel2,
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),
        0, 0));
    panel1.add(userTextField,
        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(jPasswordField1,
        new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(jLabel1,
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
  }

  public void setUsername(String username) {
    userTextField.setText(username);
  }

  public void setUsernameEnabled(boolean enable) {
    userTextField.setEnabled(enable);
  }

  public void setMessage(String message) {
    messageLabel.setText(message);
  }

  public void setMessageVisible(boolean visible) {
    messageLabel.setVisible(visible);
  }

  public boolean isOKPressed() {
    return OKpressed;
  }

  public String getUsername() {
    return userTextField.getText();
  }

  public char[] getPassword() {
    //char[] pass = jPasswordField1.getPassword();
    //String password = String.copyValueOf(pass);
    //return password;
    return jPasswordField1.getPassword();
  }

  public void okButton_actionPerformed(ActionEvent e) {
    OKpressed = true;
    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    OKpressed = false;
    setVisible(false);
  }

  class UserPasswordDialog_okButton_actionAdapter
      implements ActionListener {

    private UserPasswordDialog adaptee;

    UserPasswordDialog_okButton_actionAdapter(UserPasswordDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.okButton_actionPerformed(e);
    }
  }

  class UserPasswordDialog_cancelButton_actionAdapter
      implements ActionListener {

    private UserPasswordDialog adaptee;

    UserPasswordDialog_cancelButton_actionAdapter(UserPasswordDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.cancelButton_actionPerformed(e);
    }
  }

  public void jTextField1_actionPerformed(ActionEvent e) {
    jPasswordField1.grabFocus();
  }

  public void jPasswordField1_actionPerformed(ActionEvent e) {
    okButton.grabFocus();
  }

  public void okButton_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      OKpressed = true;
      setVisible(false);
    }
  }

  public void cancelButton_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      OKpressed = false;
      setVisible(false);
    }
  }

  public void this_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      OKpressed = false;
      setVisible(false);
    }
  }

  class UserPasswordDialog_jTextField1_actionAdapter
      implements ActionListener {

    private UserPasswordDialog adaptee;

    UserPasswordDialog_jTextField1_actionAdapter(UserPasswordDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jTextField1_actionPerformed(e);
    }
  }

  class UserPasswordDialog_jPasswordField1_actionAdapter
      implements ActionListener {

    private UserPasswordDialog adaptee;

    UserPasswordDialog_jPasswordField1_actionAdapter(UserPasswordDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jPasswordField1_actionPerformed(e);
    }
  }

  public void jPasswordField1_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      OKpressed = true;
      setVisible(false);
    }
  }
}

class UserPasswordDialog_jPasswordField1_keyAdapter
    extends KeyAdapter {

  private UserPasswordDialog adaptee;

  UserPasswordDialog_jPasswordField1_keyAdapter(UserPasswordDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    adaptee.jPasswordField1_keyPressed(e);
  }
}

class UserPasswordDialog_cancelButton_keyAdapter
    extends KeyAdapter {

  private UserPasswordDialog adaptee;

  UserPasswordDialog_cancelButton_keyAdapter(UserPasswordDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    adaptee.cancelButton_keyPressed(e);
  }
}

class UserPasswordDialog_this_keyAdapter
    extends KeyAdapter {

  private UserPasswordDialog adaptee;

  UserPasswordDialog_this_keyAdapter(UserPasswordDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    adaptee.this_keyPressed(e);
  }
}

class UserPasswordDialog_okButton_keyAdapter
    extends KeyAdapter {

  private UserPasswordDialog adaptee;

  UserPasswordDialog_okButton_keyAdapter(UserPasswordDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    adaptee.okButton_keyPressed(e);
  }
}
