package artofillusion.tools;

import artofillusion.animation.PositionTrack;
import artofillusion.animation.Track;
import artofillusion.ui.Translate;

import java.util.List;

public class PositionTrackOneFactory implements TrackFactory {

    @Override
    public String getCategory() {
        return Translate.text("menu.positionTrack");
    }

    @Override
    public String getName() {
        return Translate.text("menu.xyzOneTrack");
    }

    @Override
    public List<Track> create() {
        List<Track> list = new java.util.ArrayList<Track>();
        list.add(new PositionTrack(0, 0, 0));
        return list;
    }
}
