package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class PictureDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String base64 = jp.getText().replace("data:image/jpeg;base64,", "");

        return Base64.decodeBase64(base64);
    }
}
