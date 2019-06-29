package TouchSoftLabs.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestApplicationUtils {

    @Test
    public void testConvertThrowableToString(){
        Throwable throwable = mock(Throwable.class);
        assertNotNull(ApplicationUtils.convertThrowableToString(throwable));
    }

}
