/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cct.interfaces.JamberooCoreInterface;
import cct.j3d.ChemicalElementsColors;
import cct.j3d.Java3dUniverse;
import cct.tools.SwingLookAndFeel;

/**
 *
 * @author vvv900
 */
public class SetupMenu extends JMenu implements ActionListener {

    public enum SETUP_MENU_ACTIONS {

        UNKNOWN, ATOM_COLOR_SCHEME, LOOK_AND_FEEL, BACKGROUND_COLOR
    }
    private JamberooCoreInterface jamberooCore;
    private Java3dUniverse java3dUniverse;
    private Component parentComponent;
    private JMenu jSubmenuAtomColorScheme = new JMenu();
    private JMenu jSubmenuLookAndFeel = new JMenu();
    private JMenu projectionMode = new JMenu("Projection Mode");
    private JMenuItem jBackgroundColor = new JMenuItem("Background Color");
    static final Logger logger = Logger.getLogger(SetupMenu.class.getCanonicalName());

    public SetupMenu(JamberooCoreInterface core) {
        super();
        jamberooCore = core;
        java3dUniverse = jamberooCore.getJamberooRenderer();
        parentComponent = this;
    }

    public void createMenu() {

        // --- Atom color scheme submenu

        ColorSchemeHamdler colorSchemeHamdler = new ColorSchemeHamdler();
        jSubmenuAtomColorScheme.setText("Atom Color Scheme");
        ChemicalElementsColors.retrieveAtomColorSchemePrefs(this.getClass());
        String atomScheme[] = ChemicalElementsColors.getAtomColorSchemeNames();
        String currentColorScheme = ChemicalElementsColors.getCurrentAtomColorScheme();
        ButtonGroup colorSchemeGroup = new ButtonGroup();
        for (int i = 0; i < atomScheme.length; i++) {
            JRadioButtonMenuItem jRadio = new JRadioButtonMenuItem(atomScheme[i]);
            //jRadio.setActionCommand(SETUP_MENU_ACTIONS.ATOM_COLOR_SCHEME.name());
            jRadio.setSelected(currentColorScheme.equals(atomScheme[i]));
            jRadio.addActionListener(colorSchemeHamdler);
            colorSchemeGroup.add(jRadio);
            jSubmenuAtomColorScheme.add(jRadio);
        }

        // --- Look and feel ubmenu

        LookAndFeelHamdler lookAndFeelHamdler = new LookAndFeelHamdler();
        jSubmenuLookAndFeel.setText("Look & Feel");
        String look = SwingLookAndFeel.retrieveLookAndFeelPrefs(this.getClass());
        String feels[] = SwingLookAndFeel.getAvailableLookAndFeels();
        ButtonGroup lookAndFeelGroup = new ButtonGroup();
        for (int i = 0; i < feels.length; i++) {
            JRadioButtonMenuItem jRadio = new JRadioButtonMenuItem();

            jRadio.setText(feels[i]);

            if (look != null && look.equalsIgnoreCase(feels[i])) {
                jRadio.setSelected(true);
                try {
                    SwingLookAndFeel.setLookAndFeel(look);
                    SwingUtilities.updateComponentTreeUI(this);
                } catch (Exception ex) {
                }
            } else {
                jRadio.setSelected(false);
            }
            //jRadio.setActionCommand(SETUP_MENU_ACTIONS.LOOK_AND_FEEL.name());
            jRadio.addActionListener(lookAndFeelHamdler);
            lookAndFeelGroup.add(jRadio);
            jSubmenuLookAndFeel.add(jRadio);

            if (look == null) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
        }

        // --- Projection mode

        String[] prModes = java3dUniverse.getProjectionModes();
        ButtonGroup projectionModeGroup = new ButtonGroup();
        for (int i = 0; i < prModes.length; i++) {
            JRadioButtonMenuItem jRadio = new JRadioButtonMenuItem(prModes[i]);
            jRadio.setSelected(java3dUniverse.getProjectionModeAsString().
                    equals(prModes[i]));
            jRadio.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JRadioButtonMenuItem mode = (JRadioButtonMenuItem) e.getSource();
                    java3dUniverse.setProjection(mode.getText());
                }
            });
            projectionModeGroup.add(jRadio);
            projectionMode.add(jRadio);
        }

        // --- Setup Background color

        jBackgroundColor.setActionCommand(SETUP_MENU_ACTIONS.BACKGROUND_COLOR.name());
        jBackgroundColor.addActionListener(this);

        // --- Setup atominfo popup

        JRadioButtonMenuItem atomInfo = new JRadioButtonMenuItem();
        atomInfo.setSelected(java3dUniverse.atomInfoPopupEnabled());
        if (atomInfo.isSelected()) {
            atomInfo.setText("Atom Info Popup Enabled");
        } else {
            atomInfo.setText("Atom Info Popup Disabled");
        }
        atomInfo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem atomInfo = (JRadioButtonMenuItem) e.getSource();
                java3dUniverse.enableAtomInfoPopup(atomInfo.isSelected());
                if (atomInfo.isSelected()) {
                    atomInfo.setText("Atom Info Popup Enabled");
                } else {
                    atomInfo.setText("Atom Info Popup Disabled");
                }
            }
        });

        // --- Setup global logging level

        JRadioButtonMenuItem globalLogging = new JRadioButtonMenuItem();
        Level level = Logger.getLogger("").getLevel();
        globalLogging.setSelected(level.intValue() <= 800); // 800 is for the INFO level
        if (globalLogging.isSelected()) {
            globalLogging.setText("Verbose Logging");
        } else {
            globalLogging.setText("Warnings and errors Logging");
        }
        globalLogging.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem globalLogging = (JRadioButtonMenuItem) e.getSource();
                if (globalLogging.isSelected()) {
                    globalLogging.setText("Verbose Logging");
                    Logger.getLogger("").setLevel(Level.INFO);
                } else {
                    globalLogging.setText("Warnings and errors Logging");
                    Logger.getLogger("").setLevel(Level.WARNING);
                }
            }
        });

        add(jSubmenuAtomColorScheme);
        add(jSubmenuLookAndFeel);
        add(projectionMode);
        add(jBackgroundColor);
        add(atomInfo);
        add(globalLogging);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();

        // --- Get action command

        SETUP_MENU_ACTIONS action = SETUP_MENU_ACTIONS.UNKNOWN;
        try {
            action = SETUP_MENU_ACTIONS.valueOf(actionCommand);
        } catch (Exception ex) {
            logger.warning("Unknown setup menu action: " + actionCommand);
            return;
        }
        String arg;

        switch (action) {
            /*
            case ATOM_COLOR_SCHEME:
            jSubmenuAtomColorScheme.setEnabled(false);
            arg = (String) actionEvent.getActionCommand();
            ChemicalElementsColors.setCurrentAtomColorScheme(arg);
            ChemicalElementsColors.saveAtomColorSchemePrefs(this.getClass());
            if (java3dUniverse.getMoleculeInterface() == null) {
            return;
            }
            java3dUniverse.updateAtomColorScheme();
            jSubmenuAtomColorScheme.setEnabled(true);
            break;
             *
             */
            /*
            case LOOK_AND_FEEL:
            jSubmenuLookAndFeel.setEnabled(false);
            arg = (String) actionEvent.getActionCommand();
            //arg = arg.substring(0, arg.indexOf("(")).trim();

            try {
            SwingLookAndFeel.setLookAndFeel(arg);
            SwingUtilities.updateComponentTreeUI(this);
            SwingLookAndFeel.saveLookAndFeelPrefs(this.getClass());
            } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Changing L&F", JOptionPane.ERROR_MESSAGE);
            }
            jSubmenuLookAndFeel.setEnabled(true);
            break;
             *
             */

            case BACKGROUND_COLOR:
                if (java3dUniverse == null) {
                    return;
                }
                Color bgColor = java3dUniverse.getBackgroundColor();
                if (bgColor == null) {
                    JOptionPane.showMessageDialog(this, "Cannot setup background color", "Error getting background color",
                            JOptionPane.ERROR_MESSAGE);
                    jBackgroundColor.setEnabled(false);
                    return;
                }
                Color c = JColorChooser.showDialog(this.getParent(), "Choose Background Color", bgColor);
                if (c != null) {
                    java3dUniverse.setBackgroundColor(c.getRed(), c.getGreen(), c.getBlue());
                }
                break;
        }
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    class ColorSchemeHamdler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (!(actionEvent.getSource() instanceof JRadioButtonMenuItem)) {
                return;
            }

            jSubmenuAtomColorScheme.setEnabled(false);
            String arg = actionEvent.getActionCommand();
            ChemicalElementsColors.setCurrentAtomColorScheme(arg);
            ChemicalElementsColors.saveAtomColorSchemePrefs(this.getClass());
            if (java3dUniverse.getMoleculeInterface() == null) {
                return;
            }
            java3dUniverse.updateAtomColorScheme();
            jSubmenuAtomColorScheme.setEnabled(true);
        }
    }

    class LookAndFeelHamdler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (!(actionEvent.getSource() instanceof JRadioButtonMenuItem)) {
                return;
            }

            jSubmenuLookAndFeel.setEnabled(false);
            String arg = actionEvent.getActionCommand();

            try {
                SwingLookAndFeel.setLookAndFeel(arg);
                SwingUtilities.updateComponentTreeUI(parentComponent);
                SwingLookAndFeel.saveLookAndFeelPrefs(this.getClass());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Changing L&F", JOptionPane.ERROR_MESSAGE);
            }
            jSubmenuLookAndFeel.setEnabled(true);
        }
    }
}
