package artofillusion.tools;

import artofillusion.LayoutWindow;
import buoy.widget.BMenu;

public final class TracksMenu extends BMenu {
    private LayoutWindow layout;
    public TracksMenu(LayoutWindow layout) {
        super("Tracks");
        this.layout = layout;
    }
}
