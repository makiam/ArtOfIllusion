package artofillusion.animation;

import artofillusion.math.CoordinateSystem;
import artofillusion.object.Cube;
import artofillusion.object.ObjectInfo;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class ScoreTest extends TestCase {

    @Test
    public void testScoreSelectionFilterAllGood() {
        Object[] input = new Object[] {new ObjectInfo(new Cube(1d,1d,1d), new CoordinateSystem(), "Test"), new ObjectInfo(new Cube(1d,1d,1d), new CoordinateSystem(), "Test"), new ObjectInfo(new Cube(1d,1d,1d), new CoordinateSystem(), "Test")};

        Assert.assertEquals(3, Score.filterTargets(input).size());
    }

    public void testScoreSelectionFilterAllBad() {
        Object[] input = new Object[] {"String", "String", "String"};
        Assert.assertEquals(0, Score.filterTargets(input).size());
    }

    @Test
    public void testScoreSelectionFilterOneGood() {
        Object[] input = new Object[] {1, new ObjectInfo(new Cube(1d,1d,1d), new CoordinateSystem(), "Test"), "String", "String", "String"};
        Assert.assertEquals(1, Score.filterTargets(input).size());
    }
}
