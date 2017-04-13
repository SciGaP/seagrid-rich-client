/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.tools.ui;

/**
 *
 * @author vvv900
 */
public interface JobProgressInterface {

  void setProgress(int p);

  void setProgress(double p);

  void setProgress(float p);

  void setProgressText(String text);

  void setTaskDescription(String text);

  boolean isCanceled();
}
