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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;

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
public class ThreadExec
    extends Thread {

   ThreadInterface threadInterface = null;
   boolean done = false;
   Exception exception = null;
   boolean cancel = false;

   int MAX = 10;
   String winTitle = "Performing Task";
   JLabel msgLabel = new JLabel("Performing task...");
   JProgressBar progressBar = new JProgressBar(0, MAX);

   public ThreadExec(ThreadInterface ti) {
      threadInterface = ti;
   }

   public boolean isDone() {
      return done;
   }

   public void cancel() {
      cancel = true;
   }

   public boolean isCancelled() {
      return cancel;
   }

   @Override
  public void run() {

      progressBar.setValue(0);

      Object[] comp = {
          msgLabel, progressBar};
      Object[] options = {
          "Cancel"};

      JOptionPane pane = new JOptionPane(comp,
                                         JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.INFORMATION_MESSAGE,
                                         null,
                                         options,
                                         options[0]);

      final JDialog dialog = pane.createDialog(null, winTitle);

      Timer timer = new Timer(250, new TimerListener(this, dialog));

      try {
         threadInterface.performTask();
      }
      catch (Exception e) {
         exception = e;
         done = true;
         return;
      }

      if (cancel) {
         return;
      }

      // send the event....
      //if (proxyListener != null) {
      //  proxyListener.proxyCreated(proxy);
      //}

      done = true;

   }

   public Exception getException() {
      return exception;
   }

   class TimerListener
       implements ActionListener {
      ThreadExec task = null;
      JDialog dialog = null;
      public TimerListener(ThreadExec t, JDialog d) {
         task = t;
         dialog = d;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         if (task.isDone()) {
            dialog.setVisible(false);
         }
         else {
            progressBar.setValue( (progressBar.getValue() + 1) % MAX);
         }
      }

   }

}
