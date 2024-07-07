package artofillusion.ui;

import artofillusion.RenderListener;
import artofillusion.image.ComplexImage;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PreviewComponent extends JComponent implements MouseListener, RenderListener {

    boolean mouseInside;

    PreviewComponent() {
        this.addMouseListener(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                PreviewComponent.this.repaint(0,0,0, PreviewComponent.this.getWidth(), PreviewComponent.this.getHeight());
            }
        });
    }

    @Override
    public void imageComplete(ComplexImage image) {
        // WIP
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // WIP
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // WIP
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // WIP
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseInside = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseInside = false;
    }
}
