package org.insaneheadoflettuce.input.common;

import org.insaneheadoflettuce.input.api.ChecksumProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleChecksumProvider implements ChecksumProvider
{
    private MessageDigest messageDigest;

    public SimpleChecksumProvider()
    {
        try
        {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("Cannot instantiate checksum provider", e);
        }
    }

    public String calculatePurged(String... args)
    {
        return calculatePurged(String.join("", args));
    }

    public String calculatePurged(String string)
    {
        messageDigest.reset();
        try (DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(string.replace("\\s+", "").trim().getBytes(StandardCharsets.UTF_8)), messageDigest))
        {
            while (dis.read() != -1) ;
            messageDigest = dis.getMessageDigest();
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Checksum creation failed", e);
        }

        StringBuilder result = new StringBuilder();
        for (byte b : messageDigest.digest())
        {
            result.append(String.format("%02x", b));
        }
        return result.toString().toUpperCase();
    }
}
