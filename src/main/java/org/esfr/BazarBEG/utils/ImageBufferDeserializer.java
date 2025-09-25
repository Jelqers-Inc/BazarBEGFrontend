package org.esfr.BazarBEG.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.esfr.BazarBEG.modelos.dtos.usuarios.ImageBuffer;

import java.io.IOException;
import java.util.Base64;

public class ImageBufferDeserializer extends JsonDeserializer<ImageBuffer> {
    @Override
    public ImageBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // Get the incoming value as a string (the Base64 data)
        String base64String = p.getValueAsString();
        if (base64String == null) {
            return null;
        }

        // Decode the Base64 string into a byte array
        byte[] data = Base64.getDecoder().decode(base64String);

        // Create and return the ImageBuffer object
        ImageBuffer imageBuffer = new ImageBuffer();
        imageBuffer.setType("Buffer"); // Set the type manually
        imageBuffer.setData(data);
        return imageBuffer;
    }
}
