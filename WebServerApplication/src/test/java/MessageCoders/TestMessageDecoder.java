package MessageCoders;

import Entity.Message;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestMessageDecoder {

    @Test
    public void testDecode() {
        JsonObject jsonObject = MockDataMessageCoders.getJsonObject();
        Message message = MockDataMessageCoders.getMessage();

        MessageDecoder messageDecoder = new MessageDecoder();
        assertEquals(message,messageDecoder.decode(jsonObject.toString()));
    }

    @Test
    public void testWillDecode() {
        MessageDecoder messageDecoder = mock(MessageDecoder.class);
        when(messageDecoder.willDecode(anyString())).thenReturn(true);
        assertTrue(messageDecoder.willDecode(anyString()));
    }

}
