package com.loficostudios.marketplacePlugin.exceptions;

public class VaultNotFoundException extends RuntimeException {
    public VaultNotFoundException(String message) {
        super(message);
    }
}
