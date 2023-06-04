package org.joget.marketplace;

import java.util.ArrayList;
import java.util.Collection;
import org.joget.marketplace.datalist.lib.UTCDateDatalistFormatter;
import org.joget.marketplace.form.lib.UTCDatePicker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(UTCDatePicker.class.getName(), new UTCDatePicker(), null));
        registrationList.add(context.registerService(UTCDateDatalistFormatter.class.getName(), new UTCDateDatalistFormatter(), null));
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}