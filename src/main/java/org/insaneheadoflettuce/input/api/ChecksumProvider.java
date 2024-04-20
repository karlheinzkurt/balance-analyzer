package org.insaneheadoflettuce.input.api;

public interface ChecksumProvider {
    String calculatePurged(String... args);

    String calculatePurged(String string);
}
