package com.loficostudios.marketplacePlugin.market;

import java.util.concurrent.CompletableFuture;

public interface ISavableLoadable {
    boolean save();
    CompletableFuture<Boolean> asyncSave();
    boolean load();
    CompletableFuture<Boolean> asyncLoad();
}
