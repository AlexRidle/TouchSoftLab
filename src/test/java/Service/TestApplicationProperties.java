package Service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TestApplicationProperties {

    private Properties properties;

    @Before
    public void init() {
        properties = MockDataService.getProperties();
    }

    @Test
    public void testGetProperties() {
        assertNotNull(ApplicationProperties.getProperties());
        assertEquals(ApplicationProperties.getProperties(), properties);
    }

}
