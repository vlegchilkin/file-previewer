package org.vlegchilkin.filepreviewer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.ui.MainFrame;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * App entry point
 */
public class Main {
    final static Logger log = LoggerFactory.getLogger(Main.class);
    public final static ResourceBundle PROPERTIES = ResourceBundle.getBundle("filepreviewer");
    private static final String[] LAF_CLASSES = PROPERTIES.getString("main.laf.classes").split(",");

    private static void setupLookAndFeel()  {
        for (var lookAndFeel : Main.LAF_CLASSES) {
            try {
                UIManager.setLookAndFeel(lookAndFeel);
                log.info("LookAndFeel {} was selected", lookAndFeel);
                return;
            } catch (UnsupportedLookAndFeelException e) {
                log.warn("LookAndFeel {} is not supported, trying another one", lookAndFeel);
            } catch (Exception e) {
                log.error("failure on LookAndFeel {} setup, trying another one", lookAndFeel, e);
            }
        }
        log.warn("Supported LookAndFeel wasn't found, default is in use");
    }

    public static void main(String[] args) {
        setupLookAndFeel();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

