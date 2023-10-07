package org.vlegchilkin.filepreviewer.ui;

import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Application's main frame.
 * Contains only FileBrowser component, starts at the middle of a screen with a min/preferred size 800 x 500.
 */
public class MainFrame extends JFrame implements ActionListener {
    private static final int DEFAULT_WIDTH = Integer.parseInt(Main.PROPERTIES.getString("mainframe.default.width"));
    private static final int DEFAULT_HEIGHT = Integer.parseInt(Main.PROPERTIES.getString("mainframe.default.height"));
    private static final String TITLE = Main.PROPERTIES.getString("mainframe.title");


    public MainFrame() throws HeadlessException {
        super(MainFrame.TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT));
        setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT));

        FileBrowser fb = (FileBrowser) add(new FileBrowser());
        fb.addActionListener(this);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            dispatchEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
        }
    }

}
