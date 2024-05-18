package io.akkajob.server.common.actor;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import akka.actor.Props;

public class PropsFactory implements Extension {
    private ApplicationContext context;

    public void init(ApplicationContext context) {
        this.context = context;
    }

    public Props create(String beanName) {
        return Props.create(ActorProducer.class, this.context, beanName);
    }
}
