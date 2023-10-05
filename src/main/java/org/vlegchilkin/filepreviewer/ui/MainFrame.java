package org.vlegchilkin.filepreviewer.ui;

import org.apache.commons.io.FilenameUtils;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.ImagePreview;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewDialog;
import org.vlegchilkin.filepreviewer.ui.preview.TextPreview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

public class MainFrame extends JFrame implements ActionListener {
    private static final int DEFAULT_WIDTH = Integer.parseInt(Main.PROPERTIES.getString("mainframe.default.width"));
    private static final int DEFAULT_HEIGHT = Integer.parseInt(Main.PROPERTIES.getString("mainframe.default.height"));
    private static final String TITLE = Main.PROPERTIES.getString("mainframe.title");
    private final FileBrowser fileBrowser;


    public MainFrame() throws HeadlessException {
        super(MainFrame.TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT));

        this.fileBrowser = (FileBrowser) add(new FileBrowser());
        this.fileBrowser.addActionListener(this);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case JFileChooser.CANCEL_SELECTION ->
                    dispatchEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
            case JFileChooser.APPROVE_SELECTION -> {
                PreviewDialog dialog = buildPreviewDialog(this.fileBrowser.getSelectedFile());
                if (dialog != null) {
                    dialog.setVisible(true);
                }
            }
        }
    }

    private PreviewDialog buildPreviewDialog(File selectedFile) {
        String fileExtension = FilenameUtils.getExtension(selectedFile.getName()).toLowerCase();
        final PreviewDialog dialog;
        if (ImagePreview.EXTENSIONS.contains(fileExtension)) {
            dialog = new ImagePreview(this, selectedFile);
        } else if (TextPreview.EXTENSIONS.contains(fileExtension)) {
            dialog = new TextPreview(this, selectedFile);
        } else {
            dialog = null;
        }
        return dialog;
    }
}
