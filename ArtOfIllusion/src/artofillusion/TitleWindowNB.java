/* Copyright (C) 2018 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion;

import artofillusion.ui.Translate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author maksim.khramov
 */
public class TitleWindowNB extends JDialog implements PropertyChangeListener {
    
    private static final Logger logger = Logger.getLogger(TitleWindowNB.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    public TitleWindowNB(JFrame owner) {
        super(owner, "Art OF Illusion", ModalityType.DOCUMENT_MODAL);
    }
    
    public TitleWindowNB() {
        this((JFrame)null);
    }

    @Override
    protected void dialogInit() {
        
        super.dialogInit();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                TitleWindowNB.this.setVisible(false);
                TitleWindowNB.this.dispose();
            }
            
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                logger.log(Level.INFO, "Window activated");
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                logger.log(Level.INFO, "Window deactivated");
            }
            
            
        });
        int num = new Random(System.currentTimeMillis()).nextInt(8);
        ImageIcon image = new ImageIcon(getClass().getResource("/artofillusion/titleImages/titleImage" + num + ".jpg"));

        Runtime runtime = Runtime.getRuntime();
        int cpuCount = runtime.availableProcessors();
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        
        StringBuilder extra = new StringBuilder();
        extra.append("<hr/>").append(Translate.text("about.java.version", javaVersion, javaVendor)).append("<br/>");
        extra.append(Translate.text("about.system.version", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"))).append("<br/>");
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 0x100000L;
        long allocated = runtime.totalMemory() / 0x100000L;
        long max =  runtime.maxMemory() / 0x100000L;
        extra.append(Translate.text("about.system.memory", used, allocated, max)).append("<br/>");
        extra.append(Translate.text("about.system.cpus", runtime.availableProcessors())).append("<br/>");
        Color background = num == 4 ? new Color(204, 204, 255) : (num == 6 ? new Color(232, 255, 232) : Color.WHITE);
        
        String text = "<html>"
                + "<div align='center'>"
                + "Art of Illusion version " + ArtOfIllusion.getVersion()
                + "<br>Copyright 1999-2018 by Peter Eastman and others"
                + "<br>(See the README file for details.)"
                + "<br>This program may be freely distributed under"
                + "<br>the terms of the accompanying license."
                + "</div>"
                + extra.toString()
                + "</html>";
        
        JLabel label = new JLabel(text,image,JLabel.CENTER);
        
        label.setBorder(new EmptyBorder(0,0,5,0));
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        
        JPanel labelContainerPanel = new JPanel();
        labelContainerPanel.setLayout(new BorderLayout(0, 0));        
        labelContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        labelContainerPanel.add(label);
        labelContainerPanel.setBackground(background);
      
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBackground(background);
        
        root.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        root.add(labelContainerPanel);

        this.getContentPane().add(root);
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("activeWindow", this);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
    }

    @Override
    public void dispose() {
        super.dispose();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("activeWindow", this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        //logger.log(Level.INFO, "Property change event fired: " + event);
    }
    
    
    
    
    
}
