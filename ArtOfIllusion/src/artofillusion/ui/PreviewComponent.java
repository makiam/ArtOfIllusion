package artofillusion.ui;

import artofillusion.RenderListener;
import artofillusion.Scene;
import artofillusion.image.ComplexImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SwingContainer(false)
public class PreviewComponent extends JComponent implements MouseListener, HierarchyListener, RenderListener {

    private boolean mouseInside;
    private Point clickPoint;
    private boolean renderInProgress;
    private Image image;
    private Scene scene;

    PreviewComponent() {
        this.addMouseListener(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
        this.addHierarchyListener(this);
    }

    public Scene getScene() {
        return this.scene;
    }

    @Override
    public void imageComplete(ComplexImage image) {
        this.image = image.getImage();
        renderInProgress = false;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // WIP
    }

    @Override
    public void mousePressed(MouseEvent event) {
        clickPoint = event.getPoint();
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

    @Override
    public void hierarchyChanged(HierarchyEvent event) {

    }
}
