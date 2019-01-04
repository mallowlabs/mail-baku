
package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import lifecycles.SubEthaSMTPLifecycle;

@Singleton
public class Module extends AbstractModule {

    protected void configure() {

        bind(SubEthaSMTPLifecycle.class);

    }

}
