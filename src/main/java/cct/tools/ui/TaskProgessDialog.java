/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.tools.ui;

import javax.swing.JLabel;

/**
 *
 * @author Vlad
 */
public class TaskProgessDialog extends javax.swing.JDialog implements JobProgressInterface {

  private boolean canceled = false;

  /**
   * Creates new form TaskProgessDialog
   */
  public TaskProgessDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  public void setProgress(double p) {
    setProgress((int) p);
  }

  public void setProgress(float p) {
    setProgress((int) p);
  }

  public void setProgress(int p) {
    taskProgressBar.setIndeterminate(false);
    taskProgressBar.setValue(p);
    taskProgressBar.setStringPainted(true);
  }

  public void setProgressText(String text) {
    taskProgressBar.setIndeterminate(false);
    taskProgressBar.setString(text);
    this.validate();
    this.pack();
  }

  public String getTaskDescription() {
    return taskDescriptionLabel.getText();
  }

  public void setTaskDescription(String taskDescription) {
    this.taskDescriptionLabel.setText(taskDescription);
    this.validate();
    this.pack();
  }

  public int getMinimumValue() {
    return taskProgressBar.getMinimum();
  }

  public int getMaximumValue(int n) {
    return taskProgressBar.getMaximum();
  }

  public void setMinimumValue(int n) {
    taskProgressBar.setMinimum(n);
  }

  public void setMaximumValue(int n) {
    taskProgressBar.setMaximum(n);
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean value) {
    canceled = value;
  }

  public void showCancelButton(boolean show) {
    cancelButton.setVisible(show);
  }

  public void showHelpButton(boolean show) {
    helpButton.setVisible(show);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
   * content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        taskProgressBar = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        taskDescriptionLabel = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        taskProgressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        taskProgressBar.setIndeterminate(true);
        getContentPane().add(taskProgressBar, java.awt.BorderLayout.CENTER);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        helpButton.setText("Help");
        jPanel1.add(helpButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        taskDescriptionLabel.setText("Task Progress...");
        jPanel2.add(taskDescriptionLabel);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    canceled = true;
    taskProgressBar.setString("Cancel signal was sent... ");
  }//GEN-LAST:event_cancelButtonActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /*
     * Set the Nimbus look and feel
     */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(TaskProgessDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(TaskProgessDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(TaskProgessDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(TaskProgessDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /*
     * Create and display the dialog
     */
    java.awt.EventQueue.invokeLater(new Runnable() {

      public void run() {
        TaskProgessDialog dialog = new TaskProgessDialog(new javax.swing.JFrame(), true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {

          @Override
          public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
          }
        });
        dialog.setVisible(true);
      }
    });
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private JLabel taskDescriptionLabel;
    private javax.swing.JProgressBar taskProgressBar;
    // End of variables declaration//GEN-END:variables
}
