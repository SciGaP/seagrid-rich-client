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

import cct.modelling.StructureManagerInterface;
import cct.modelling.TrajectoryClientInterface;
import cct.resources.images.ImageResources;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class AnimationPanel
    extends JPanel implements TrajectoryClientInterface, ActionListener {

  public static final int ANIMATION_STARTED = 1;
  public static final int ANIMATION_FINISHED = ANIMATION_STARTED + 1;

  private static final String[] MOVIE_MODES = {
    "Once", "Loop", "Rock"};

  private int minStep = 1, maxStep = 100, stepStep = 1;
  private int Step = 1;
  private int animDirection = 1;
  private int framesPerSeconds = 25;
  private int delay;
  private int animationMode;
  private Timer timer;
  private boolean animationInProgress = false;

  private StructureManagerInterface structureManagerInterface = null;
  private Set<ActionListener> actionListeners = new HashSet<ActionListener>();

  protected SpinnerModel stepModel = new SpinnerNumberModel(Step, minStep, maxStep, stepStep);
  protected SpinnerModel fpsModel = new SpinnerNumberModel(framesPerSeconds, 1, 200, 1);

  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JButton startMediaButton = new JButton();

  protected JSpinner stepSpinner = new JSpinner(stepModel);
  protected JPanel sliderPanel = new JPanel();
  protected JLabel maxLabel = new JLabel();
  protected JTextField currentFrameTextField = new JTextField();

  private ImageIcon mediaBeginningImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_beginning.png"));
  private ImageIcon mediaEndImage = new ImageIcon(ImageResources.class.getResource("icons16x16/media_end.png"));
  private ImageIcon mediaPauseImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_pause.png"));
  private ImageIcon mediaPlayImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_play.png"));
  private ImageIcon mediaPlayBackImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_play_back.png"));
  private ImageIcon mediaStepBackImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_step_back.png"));
  private ImageIcon mediaStepForwardImage = new ImageIcon(ImageResources.class.getResource(
      "icons16x16/media_step_forward.png"));

  protected JButton mediaEndButton = new JButton();
  protected JSlider trajSlider = new JSlider();
  protected JButton pauseButton = new JButton();
  protected JButton playBackButton = new JButton();
  protected JButton playButton = new JButton();
  protected JLabel minLabel = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  protected JButton stepBackButton = new JButton();
  protected JButton stepForwardButton = new JButton();
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected JPanel jPanel2 = new JPanel();
  protected GridBagLayout gridBagLayout2 = new GridBagLayout();
  protected JLabel jLabel4 = new JLabel();
  protected JSpinner fpsSpinner = new JSpinner(fpsModel);
  protected JPanel jPanel3 = new JPanel();
  protected JPanel jPanel4 = new JPanel();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected JComboBox animationComboBox = new JComboBox();
  static final Logger logger = Logger.getLogger(AnimationPanel.class.getCanonicalName());

  public AnimationPanel() {
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public AnimationPanel(StructureManagerInterface smi) {
    structureManagerInterface = smi;
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    startMediaButton.setMaximumSize(new Dimension(24, 24));
    startMediaButton.setMinimumSize(new Dimension(24, 24));
    startMediaButton.setPreferredSize(new Dimension(24, 24));
    startMediaButton.setToolTipText("Beginning of Trajectory");
    startMediaButton.setIcon(mediaBeginningImage);
    startMediaButton.setMargin(new Insets(0, 0, 0, 0));
    startMediaButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startMediaButton_actionPerformed(e);
      }
    });
    maxLabel.setToolTipText("");
    maxLabel.setText("      0");
    currentFrameTextField.setToolTipText("Enter New Value and Press Enter");
    currentFrameTextField.setText("0");
    currentFrameTextField.setColumns(7);
    currentFrameTextField.setHorizontalAlignment(SwingConstants.CENTER);
    currentFrameTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        currentFrameTextField_actionPerformed(e);
      }
    });
    mediaEndButton.setMaximumSize(new Dimension(24, 24));
    mediaEndButton.setMinimumSize(new Dimension(24, 24));
    mediaEndButton.setPreferredSize(new Dimension(24, 24));
    mediaEndButton.setToolTipText("End of Trajectory");
    mediaEndButton.setIcon(mediaEndImage);
    mediaEndButton.setMargin(new Insets(0, 0, 0, 0));
    mediaEndButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mediaEndButton_actionPerformed(e);
      }
    });
    pauseButton.setMaximumSize(new Dimension(24, 24));
    pauseButton.setMinimumSize(new Dimension(24, 24));
    pauseButton.setPreferredSize(new Dimension(24, 24));
    pauseButton.setToolTipText("Stop/Pause Animation");
    pauseButton.setIcon(mediaPauseImage);
    pauseButton.setMargin(new Insets(0, 0, 0, 0));
    pauseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pauseButton_actionPerformed(e);
      }
    });
    playBackButton.setMaximumSize(new Dimension(24, 24));
    playBackButton.setMinimumSize(new Dimension(24, 24));
    playBackButton.setPreferredSize(new Dimension(24, 24));
    playBackButton.setToolTipText("Play Back");
    playBackButton.setIcon(mediaPlayBackImage);
    playBackButton.setMargin(new Insets(0, 0, 0, 0));
    playBackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playBackButton_actionPerformed(e);
      }
    });
    playButton.setMaximumSize(new Dimension(24, 24));
    playButton.setMinimumSize(new Dimension(24, 24));
    playButton.setPreferredSize(new Dimension(24, 24));
    playButton.setToolTipText("Play Forward");
    playButton.setIcon(mediaPlayImage);
    playButton.setMargin(new Insets(0, 0, 0, 0));
    playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playButton_actionPerformed(e);
      }
    });
    minLabel.setToolTipText("");
    minLabel.setText("0      ");
    jLabel3.setText("Step: ");
    stepBackButton.setMaximumSize(new Dimension(24, 24));
    stepBackButton.setMinimumSize(new Dimension(24, 24));
    stepBackButton.setPreferredSize(new Dimension(24, 24));
    stepBackButton.setToolTipText("Step Back");
    stepBackButton.setIcon(mediaStepBackImage);
    stepBackButton.setMargin(new Insets(0, 0, 0, 0));
    stepBackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stepBackButton_actionPerformed(e);
      }
    });
    stepForwardButton.setMaximumSize(new Dimension(24, 24));
    stepForwardButton.setMinimumSize(new Dimension(24, 24));
    stepForwardButton.setPreferredSize(new Dimension(24, 24));
    stepForwardButton.setToolTipText("Step Forward");
    stepForwardButton.setIcon(mediaStepForwardImage);
    stepForwardButton.setMargin(new Insets(0, 0, 0, 0));
    stepForwardButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stepForwardButton_actionPerformed(e);
      }
    });
    sliderPanel.setLayout(gridBagLayout1);
    jPanel2.setLayout(gridBagLayout2);
    jLabel4.setToolTipText("");
    jLabel4.setText("Frames per second: ");
    jPanel1.setLayout(borderLayout2);
    trajSlider.setMinimum(1);
    stepSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        stepSpinner_stateChanged(e);
      }
    });
    fpsSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        fpsSpinner_stateChanged(e);
      }
    });
    animationComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        animationComboBox_itemStateChanged(e);
      }
    });
    jPanel4.add(jLabel3);
    jPanel4.add(stepSpinner);
    jPanel4.add(jLabel4);
    jPanel4.add(fpsSpinner);
    jPanel4.add(animationComboBox);
    jPanel3.add(stepBackButton);
    jPanel3.add(playBackButton);
    jPanel3.add(pauseButton);
    jPanel3.add(playButton);
    jPanel3.add(stepForwardButton);
    this.add(jPanel1, BorderLayout.SOUTH);
    sliderPanel.add(startMediaButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    sliderPanel.add(mediaEndButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(minLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    jPanel2.add(maxLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    sliderPanel.add(jPanel2, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(currentFrameTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    sliderPanel.add(trajSlider, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 1, 0));
    jPanel1.add(jPanel4, BorderLayout.SOUTH);
    jPanel1.add(jPanel3, BorderLayout.NORTH);
    this.add(sliderPanel, BorderLayout.NORTH);

    trajSlider.setValue(Step);

    //Set up a timer that calls this object's action handler.
    delay = 1000 / framesPerSeconds;
    //timer.setDelay(delay);
    //timer.setInitialDelay(delay * 10);

    timer = new Timer(delay, this);
    //timer.setInitialDelay(delay * 7); //We pause animation twice per cycle  by restarting the timer
    timer.setCoalesce(true);

    for (int i = 0; i < MOVIE_MODES.length; i++) {
      animationComboBox.addItem(MOVIE_MODES[i]);
    }
    animationComboBox.setSelectedIndex(0);
    animationMode = animationComboBox.getSelectedIndex();

    trajSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        trajSlider_stateChanged(e);
      }
    });

  }

  public void setStructureManagerInterface(StructureManagerInterface smi) {
    structureManagerInterface = smi;
  }

  public void setMaxSnapshotCount(int new_max) {
    trajSlider.setEnabled(false);
    currentFrameTextField.setEnabled(false);

    //int value = trajSlider.getValue();
    trajSlider.setMaximum(new_max);
    boolean update = false;
    if (Step <= new_max) {
      trajSlider.setValue(Step);
    } else {
      update = true;
      Step = new_max;
    }

    currentFrameTextField.setText(String.valueOf(Step));

    maxLabel.setText(String.format("%-7s", new_max));
    if (new_max > 0) {
      minLabel.setText(String.format("%7s", 1));
    } else {
      minLabel.setText(String.format("%7s", 0));
    }
    trajSlider.setEnabled(true);
    currentFrameTextField.setEnabled(true);

    if (update) {
      trajSlider.setValue(Step);
    }
    //else {
    //   trajSlider.setValue(Step);
    //}
  }

  public void setSnapshotValue(int new_value) {
    /*
     try {
     (new_value);
     }
     catch (Exception ex) {
     JOptionPane.showMessageDialog(this, "Cannot select structure # " + new_value + " : " + ex.getMessage(), "Error",
     JOptionPane.ERROR_MESSAGE);
     return;
     }
     */

    trajSlider.setEnabled(false);
    currentFrameTextField.setEnabled(false);

    Step = new_value;
    trajSlider.setValue(Step);
    currentFrameTextField.setText(String.valueOf(Step));

    trajSlider.setEnabled(true);
    currentFrameTextField.setEnabled(true);
  }

  public void trajSlider_stateChanged(ChangeEvent e) {
    if (!trajSlider.isEnabled()) {
      return;
    }

    int value = trajSlider.getValue();
    if (trajSlider.getValueIsAdjusting()) {
      currentFrameTextField.setEnabled(false);
      currentFrameTextField.setText(String.valueOf(value));
      currentFrameTextField.setEnabled(true);
      return;
    }

    trajSlider.setEnabled(false);

    Step = value;
    currentFrameTextField.setEnabled(false);
    currentFrameTextField.setText(String.valueOf(value));
    currentFrameTextField.setEnabled(true);

    try {
      selectStructure(Step);
      setSnapshotValue(Step);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    trajSlider.setEnabled(true);
  }

  void selectStructure(int n) throws Exception {

    if (trajSlider.getMaximum() < 1) {
      return;
    }

    if (structureManagerInterface == null) {
      JOptionPane.showMessageDialog(this, "Structure Manager Interface is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      structureManagerInterface.selectStructure(n - 1);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Cannot select structure # " + n + " : " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  @Override
  public void setSnapshotsCount(int new_max) {
    setMaxSnapshotCount(new_max);
  }

  public void startMediaButton_actionPerformed(ActionEvent e) {
    try {
      selectStructure(1);
      setSnapshotValue(1);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  public void mediaEndButton_actionPerformed(ActionEvent e) {
    try {
      selectStructure(trajSlider.getMaximum());
      setSnapshotValue(trajSlider.getMaximum());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  /**
   * Called when the Timer fires.
   *
   * @param e ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (animationInProgress) {
      logger.info("Lost frame...");
      return;
    }
    animationInProgress = true;

    int value = Step + stepStep * animDirection;
    if (value < 1) {
      value = 1;
    } else if (value > trajSlider.getMaximum()) {
      value = trajSlider.getMaximum();
    }

    Step = value;

    try {
      long start = System.currentTimeMillis();
      selectStructure(Step);
      //structureManagerInterface.selectStructure(Step - 1);
      float secs = (System.currentTimeMillis() - start) / 1000.0f;
      logger.info("Elapsed time: " + secs);
      setSnapshotValue(Step);
    } catch (Exception ex) {
      animationInProgress = false;
      timer.stop();
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (animationMode == 0) { // Animate once
      if (Step == 1 && animDirection == -1) {
        stopAnimation();
      } else if (Step == trajSlider.getMaximum() && animDirection == 1) {
        stopAnimation();
      }
    } else if (animationMode == 1) { // Animate in loop
      if (Step == 1 && animDirection == -1) {
        Step = trajSlider.getMaximum();
      } else if (Step == trajSlider.getMaximum() && animDirection == 1) {
        Step = 1;
      }
    } else if (animationMode == 2) { // Animate back & forth
      if (Step == 1 && animDirection == -1) {
        if (trajSlider.getMaximum() > 1) {
          Step = 1;
          animDirection = 1;
        }
      } else if (Step == trajSlider.getMaximum() && animDirection == 1) {
        if (trajSlider.getMaximum() > 1) {
          Step = trajSlider.getMaximum() - 1;
          animDirection = -1;
        }
      }
    }

    animationInProgress = false;

  }

  public void playButton_actionPerformed(ActionEvent e) {
    if (animationInProgress && animDirection == 1) {
      return;
    }

    animDirection = 1;

    startAnimation();
  }

  public void startAnimation() {
    //Start (or restart) animating!
    this.sendActionEvent(ANIMATION_STARTED);
    timer.start();
  }

  public void stopAnimation() {
    //Stop animation
    timer.stop();
    animationInProgress = false;
    this.sendActionEvent(ANIMATION_FINISHED);
  }

  public void pauseButton_actionPerformed(ActionEvent e) {
    stopAnimation();
  }

  public void playBackButton_actionPerformed(ActionEvent e) {
    if (animationInProgress && animDirection == -1) {
      return;
    }

    animDirection = -1;

    startAnimation();

  }

  public void stepSpinner_stateChanged(ChangeEvent e) {
    Integer value = (Integer) stepSpinner.getValue();
    stepStep = value;
  }

  public void fpsSpinner_stateChanged(ChangeEvent e) {
    Integer value = (Integer) fpsSpinner.getValue();
    framesPerSeconds = value;
    delay = 1000 / framesPerSeconds;
    timer.setDelay(delay);
  }

  public void stepForwardButton_actionPerformed(ActionEvent e) {
    if (animationInProgress) {
      timer.stop();
      animationInProgress = false;
    }

    animDirection = 1;
    int value = Step + stepStep * animDirection;
    if (value > trajSlider.getMaximum()) {
      value = trajSlider.getMaximum();
    }

    Step = value;

    try {
      selectStructure(Step);
      setSnapshotValue(Step);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  public void stepBackButton_actionPerformed(ActionEvent e) {
    if (animationInProgress) {
      timer.stop();
      animationInProgress = false;
    }

    animDirection = -1;
    int value = Step + stepStep * animDirection;
    if (value < 1) {
      value = 1;
    }

    Step = value;

    try {
      selectStructure(Step);
      setSnapshotValue(Step);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

  public void animationComboBox_itemStateChanged(ItemEvent e) {
    if (!animationComboBox.isEnabled()) {
      return;
    }
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      return;
    } else if (e.getStateChange() == ItemEvent.SELECTED) {
      animationMode = animationComboBox.getSelectedIndex();
    }
  }

  public void currentFrameTextField_actionPerformed(ActionEvent e) {
    if (animationInProgress) {
      timer.stop();
      animationInProgress = false;
    }

    try {
      int n = Integer.parseInt(currentFrameTextField.getText().trim());
      if (n < 1 || n > trajSlider.getMaximum()) {
        JOptionPane.showMessageDialog(this,
            "MD Snapshot should be within range " + trajSlider.getMinimum() + " to "
            + trajSlider.getMaximum() + " Got: " + n, "Error", JOptionPane.ERROR_MESSAGE);
        currentFrameTextField.setText(String.valueOf(Step));
        return;
      }

      Step = n;
      try {
        selectStructure(Step);
        setSnapshotValue(Step);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Cannot select snapshot # " + Step + " : " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Wrong value for MD Snapshot: " + currentFrameTextField.getText().trim() + " : "
          + ex.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
      currentFrameTextField.setText(String.valueOf(Step));
    }
  }

  public void addActionListener(ActionListener a) {
    actionListeners.add(a);
  }

  public void removeActionListener(ActionListener a) {
    actionListeners.remove(a);
  }

  public void removeActionListeners() {
    actionListeners.clear();
  }

  public void sendActionEvent(int eventId) {
    if (actionListeners == null || actionListeners.size() < 1) {
      return;
    }
    ActionEvent event = new ActionEvent(this, eventId, "");
    for (ActionListener a : actionListeners) {
      a.actionPerformed(event);
    }
  }
}
