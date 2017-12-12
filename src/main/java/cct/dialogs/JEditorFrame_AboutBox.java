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

import cct.GlobalSettings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class JEditorFrame_AboutBox
        extends JDialog implements ActionListener, FocusListener, MouseListener {

  private JPanel panel1 = new JPanel();
  private JPanel panel2 = new JPanel();
  private JPanel insetsPanel1 = new JPanel();
  private JPanel insetsPanel2 = new JPanel();
  private JPanel insetsPanel3 = new JPanel();
  private JButton button1 = new JButton();
  private JLabel imageLabel = new JLabel();
  private JLabel label1 = new JLabel();
  private JLabel label2 = new JLabel();
  private JLabel homePageLabel = new JLabel();
  private JLabel emailLabel = new JLabel();
  private ImageIcon image1 = new ImageIcon();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private FlowLayout flowLayout1 = new FlowLayout();
  private GridLayout gridLayout1 = new GridLayout();
  private String product = "Jamberoo - open-source project since 2005";
  private String version = "Version 0.7 build 0626";
  private String copyright = "Jamberoo Home Page";
  private String comments = "Send e-mail for help";
  private String Version, Build;

  public JEditorFrame_AboutBox(Frame parent, String version, String build) {
    super(parent);
    Version = version;
    Build = build;
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JEditorFrame_AboutBox() {
    this(null, "07", "0526");
  }

  /**
   * Component initialization.
   *
   * @throws Exception
   */
  private void jbInit() throws Exception {
    version = "Version " + Version + " build " + Build;
    image1 = new ImageIcon(JamberooFrame.class.getResource(
            "cct/images/jmoleditor-128x128.png"));
    //"images/about-32x32.gif"));
    imageLabel.setIcon(image1);
    setTitle("About");
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setToolTipText("");
    label1.setText(product);
    label2.setText(version);

    homePageLabel.setText(copyright);
    homePageLabel.setToolTipText("Go to the Jamberoo Home Page using default browser");
    homePageLabel.addMouseListener(this);

    emailLabel.setToolTipText("Send e-mail request using default e-mail client");
    emailLabel.setText(comments);
    emailLabel.addMouseListener(this);

    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText("OK");
    button1.addActionListener(this);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(homePageLabel, null);
    insetsPanel3.add(emailLabel, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
    setResizable(true);
  }

  /**
   * Close the dialog on a button event.
   *
   * @param actionEvent ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    if (actionEvent.getSource() == button1) {
      dispose();
    }
  }

  /**
   * Invoked when a component gains the keyboard focus.
   * @param e 
   */
  public void focusGained(FocusEvent e) {
    System.out.println("Got focus");
    if (e.getSource() == homePageLabel) {
      homePageLabel.setForeground(Color.BLUE);
    }
  }

  public void focusLost(FocusEvent e) {
    if (e.getSource() == homePageLabel) {
      homePageLabel.setForeground(Color.BLACK);
    }
  }

  /**
   * Invoked when the mouse button has been clicked (pressed and released) on a component.
   * @param e 
   */
  public void mouseClicked(MouseEvent e) {
  }

  /**
   * Invoked when the mouse enters a component.
   * @param e 
   */
  public void mouseEntered(MouseEvent e) {
    //System.out.println("Got focus");
    if (e.getSource() == homePageLabel) {
      homePageLabel.setForeground(Color.BLUE);
    } else if (e.getSource() == emailLabel) {
      emailLabel.setForeground(Color.BLUE);
    }
  }

  /**
   * Invoked when the mouse exits a component.
   * @param e 
   */
  public void mouseExited(MouseEvent e) {
    if (e.getSource() == homePageLabel) {
      homePageLabel.setForeground(Color.BLACK);
    } else if (e.getSource() == emailLabel) {
      emailLabel.setForeground(Color.BLACK);
    }
  }

  /**
   * Invoked when a mouse button has been pressed on a component.
   * @param e 
   */
  public void mousePressed(MouseEvent e) {
    if (e.getSource() == homePageLabel) {
      try {
        GlobalSettings.showInDefaultBrowser(GlobalSettings.getProperty(GlobalSettings.URL_HOME_PAGE));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if (e.getSource() == emailLabel) {
      try {
        GlobalSettings.mailUsingDefaultClient(GlobalSettings.getProperty(GlobalSettings.HELP_EMAIL), "Jamberoo request", "");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.err.println(ex.getMessage());
      }
    }

  }

  public void mouseReleased(MouseEvent e) {
  }
}
