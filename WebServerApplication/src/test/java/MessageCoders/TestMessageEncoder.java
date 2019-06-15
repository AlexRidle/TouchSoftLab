package MessageCoders;

import Entity.Message;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestMessageEncoder {

    @Test
    public void testEncode() {
        JsonObject jsonObject = MockDataMessageCoders.getJsonObject();
        Message message = MockDataMessageCoders.getMessage();

        MessageEncoder messageEncoder = new MessageEncoder();
        assertEquals(messageEncoder.encode(message), jsonObject.toString());
    }


}
