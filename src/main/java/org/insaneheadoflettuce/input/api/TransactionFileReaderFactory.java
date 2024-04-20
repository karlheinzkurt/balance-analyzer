package org.insaneheadoflettuce.input.api;

import java.nio.file.Path;

public interface TransactionFileReaderFactory {
    TransactionFileReader create(Path path);
}
