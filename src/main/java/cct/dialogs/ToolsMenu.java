/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import cct.GlobalSettings;
import cct.amber.Sander8Frame;
import cct.amber.Sander8JobControl;
import cct.interfaces.JamberooCoreInterface;
import cct.tools.ui.JShowText;
import cct.tools.ui.MemoryMonitorFrame;

/**
 *
 * @author vvv900
 */
public class ToolsMenu extends JMenu {

  private Sander8Frame sander8InputEditor = null;
  private MemoryMonitorFrame memoryMonitorFrame = null;
  private JamberooCoreInterface jamberooCore;
  private Component parentComponent;

  public ToolsMenu(JamberooCoreInterface core) {
    super();
    jamberooCore = core;
  }

  public void createMenu() {
    JMenuItem jMenuSFTBrowser = new JMenuItem("Sftp Browser");
    jMenuSFTBrowser.setIcon(GlobalSettings.ICON_16x16_SERVER_CLIENT);
    jMenuSFTBrowser.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        jamberooCore.getShadowSFTPManager().startFileBrowser();
      }
    });
    add(jMenuSFTBrowser);

    JMenuItem startSander8 = new JMenuItem("Amber: input file for Sander-8");
    startSander8.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_GEAR);
    startSander8.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (sander8InputEditor == null) {
          sander8InputEditor = new Sander8Frame(new Sander8JobControl());
          sander8InputEditor.setLocationByPlatform(true);
          sander8InputEditor.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
        sander8InputEditor.setVisible(true);
      }
    });
    add(startSander8);

    // --- Information stuff

    JMenuItem systemInfo = new JMenuItem("System Info");
    systemInfo.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_INFO);
    systemInfo.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JShowText showResume = new JShowText("System Info");
        showResume.enableTextEditing(false);
        showResume.setSize(600, 640);
        showResume.setLocationByPlatform(true);
        showResume.setText(cct.tools.Utils.getSystemInfoAsString());
        showResume.setVisible(true);
      }
    });

    JMenuItem memoryMonitor = new JMenuItem("Memory Usage");
    memoryMonitor.setIcon(GlobalSettings.ICON_16x16_MEMORY_MONITOR);
    memoryMonitor.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (memoryMonitorFrame == null) {
          memoryMonitorFrame = new MemoryMonitorFrame();
          memoryMonitorFrame.setSize(300, 200);
          memoryMonitorFrame.setLocationRelativeTo(getParentComponent());
        }
        memoryMonitorFrame.setVisible(true);
      }
    });

    JMenuItem garbageCollection = new JMenuItem("Free Unused Memory");
    garbageCollection.setIcon(GlobalSettings.ICON_16x16_RECYCLE);
    garbageCollection.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Runtime.getRuntime().gc();
      }
    });

    addSeparator();
    add(systemInfo);
    add(memoryMonitor);
    add(garbageCollection);

  }


  public Component getParentComponent() {
    return parentComponent == null ? this : parentComponent;
  }

  public void setParentComponent(Component parentComponent) {
    this.parentComponent = parentComponent;
  }

}
