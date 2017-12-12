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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cct.tools.MessagePost;
import cct.tools.PostMessageType;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */


public class PostMessageDialog
    extends JDialog implements ActionListener {

  public static final int EMAIL_IS_OPTIONAL = 0;
  public static final int EMAIL_IS_OPTIONAL_BUT_WARN = 2;
  public static final int EMAIL_IS_MANDATORY = 1;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel buttonsPanel = new JPanel();
  private PostMessagePanel postMessagePanel1 = new PostMessagePanel();
  private JButton cancelButton = new JButton();
  private JButton postButton = new JButton();

  private PostMessageType messageType = PostMessageType.SUGGESTION;

  private int emailOption = EMAIL_IS_OPTIONAL;
  static String URL_Address = "http://dc2.apac.edu.au/~vvv900/cct/appl/jmoleditor/leave-message.php";

  public PostMessageDialog(Frame owner, String title, boolean modal) {
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

  public void setEmailImportance(int type) {
    switch (type) {
      case EMAIL_IS_OPTIONAL:
      case EMAIL_IS_OPTIONAL_BUT_WARN:
      case EMAIL_IS_MANDATORY:
        emailOption = type;
        break;
      default:
        emailOption = EMAIL_IS_OPTIONAL;
    }
  }

  public void setDialogType(PostMessageType type) {
    messageType = type;
    switch (messageType) {
      case SUGGESTION:
        postMessagePanel1.setInitialText("Please, type your suggestion(s) here...");
        break;
      case BUG_REPORT:
        postMessagePanel1.setInitialText("Please, report a bug here.\nTell exactly what you did\n" +
                                         "Which GUI controls you selected and what order you selected them in.\n" +
                                         "If the program reads from a file, you will probably need to send a copy of the file.\n" +
                                         "Above all, be precise. Programmers like precision... :-)");
        break;
    }

  }

  public void setMessage(String message) {
    postMessagePanel1.setMessage(message);
  }

  public PostMessageDialog() {
    this(new Frame(), "PostMessageDialog", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    postButton.setToolTipText("Post Message");
    postButton.setText("Post");
    getContentPane().add(panel1);
    panel1.add(buttonsPanel, BorderLayout.SOUTH);
    buttonsPanel.add(postButton);
    buttonsPanel.add(cancelButton);
    panel1.add(postMessagePanel1, BorderLayout.CENTER);

    cancelButton.addActionListener(this);
    postButton.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancelButton) {

    }
    else if (e.getSource() == postButton) {

      boolean emailEntered = postMessagePanel1.getEmailAddress().length() > 0 && postMessagePanel1.getEmailAddress().contains("@");
      switch (emailOption) {
        case EMAIL_IS_OPTIONAL:
          break;
        case EMAIL_IS_OPTIONAL_BUT_WARN:
          int n = JOptionPane.showConfirmDialog(this, "Warning: You didn't specify your e-mail address.\n" +
                                                "Without your e-mail address it will be impossible to contact you to get more info about the bug\n" +
                                                "Continue to send bug report - Press Yes", "Warning", JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            return;
          }
          break;
        case EMAIL_IS_MANDATORY:
          JOptionPane.showMessageDialog(this, "E-mail address is mandatory", "Error", JOptionPane.ERROR_MESSAGE);
          return;
      }

      MessagePost post = new MessagePost(URL_Address);
      String string = postMessagePanel1.getMessage().trim();
      if (string.length() < 1) {
        JOptionPane.showMessageDialog(this, "Nothing to send...", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
      }

      String key = "";
      switch (messageType) {
        case SUGGESTION:
          key = "suggest";
          break;
        case BUG_REPORT:
          key = "bug";
          break;
      }

      try {
        post.addMessage(key, string);

        string = postMessagePanel1.getUserName();
        if (string.length() > 0) {
          post.addMessage("name", string);
        }
        string = postMessagePanel1.getEmailAddress();
        if (string.length() > 0) {
          post.addMessage("email", string);
        }

        post.postMessage();
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Posting Message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      postMessagePanel1.savePreferences();
      JOptionPane.showMessageDialog(this, post.getResponseMessage(), "Server Response", JOptionPane.INFORMATION_MESSAGE);
    }
    setVisible(false);
  }

}
