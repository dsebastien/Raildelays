package be.raildelays.streamer;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {"/spring/streamer/raildelays-streamer-integration-context.xml"})
public class SpringContextIT extends AbstractJUnit4SpringContextTests {

    @Test
    public void testLoading() {
        assertTrue("The test should fail before this line", true);
    }

}
