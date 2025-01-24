package com.loficostudios.marketplacePlugin.market.api;

import java.util.function.Consumer;

public abstract class AbstractMarket implements IMarket {
    protected Consumer<IMarket> onUpdate;
    @Override
    public IMarket onUpdate(Consumer<IMarket> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }
}
