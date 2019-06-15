package Service;

import Utils.ApplicationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestApplicationUtils {

    private HashMap<String, String> hashMap;
    private String string;

    @Before
    public void init(){
        hashMap = MockDataService.getHashMap();
        string = MockDataService.getString();
    }

    @Test
    public void testConvertStringToHashMap() {
        assertEquals(ApplicationUtils.convertStringToHashMap(string), hashMap);
    }

    @Test
    public void testConvertThrowableToString(){
        Throwable throwable = mock(Throwable.class);
        assertNotNull(ApplicationUtils.convertThrowableToString(throwable));
    }

}
