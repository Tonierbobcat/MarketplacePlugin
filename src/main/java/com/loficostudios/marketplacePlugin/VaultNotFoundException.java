package com.loficostudios.marketplacePlugin;

public class VaultNotFoundException extends RuntimeException {
    public VaultNotFoundException(String message) {
        super(message);
    }
}
