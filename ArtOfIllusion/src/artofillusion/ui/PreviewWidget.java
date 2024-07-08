package artofillusion.ui;

import artofillusion.Scene;
import buoy.widget.CustomWidget;

public class PreviewWidget extends CustomWidget {
    PreviewWidget() {
        this.component = new PreviewComponent();
    }

    public Scene getScene() {
        return ((PreviewComponent)component).getScene();
    }
}
