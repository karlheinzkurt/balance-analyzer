package org.insaneheadoflettuce.balanceAnalyzer.utility;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

public class IBANValidator {
    public static String validOrThrow(String iban) {
        if (!new IBANCheckDigit().isValid(iban)) {
            throw new IllegalArgumentException("Invalid IBAN, checksum validation failed: " + iban);
        }
        if (!iban.startsWith("DE")) {
            throw new IllegalArgumentException("Unsupported IBAN, currently, only german IBANs supported");
        }
        return iban;
    }
}
