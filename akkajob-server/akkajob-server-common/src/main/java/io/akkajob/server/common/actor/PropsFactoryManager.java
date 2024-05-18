package io.akkajob.server.common.actor;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;

public class PropsFactoryManager extends AbstractExtensionId<PropsFactory> {
    private static PropsFactoryManager provider = new PropsFactoryManager();

    public static PropsFactoryManager getFactory() {
        return provider;
    }

    @Override
    public PropsFactory createExtension(ExtendedActorSystem system) {
        return new PropsFactory();
    }
}
