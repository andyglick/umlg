package org.umlg.runtime.util;

import java.util.Properties;

/**
 * Date: 2013/01/02
 * Time: 3:08 PM
 */
public class TumlProperties {

    public static TumlProperties INSTANCE = new TumlProperties();
    private Properties properties;

    private TumlProperties() {
        this.properties = new Properties();
        try {
            this.properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("umlg.env.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Expecting \"umlg.env.properties\" file on the classpath with ");
        }
        validateRequiredProperties();
    }

    private void validateRequiredProperties() {
        if (!this.properties.containsKey("umlg.db.location")) {
            throw new IllegalStateException("umlg.env.properties must have a property \"umlg.db.location\"");
        }
        if (!this.properties.containsKey("tinker.implementation")) {
            throw new IllegalStateException("umlg.env.properties must have a property \"tinker.implementation\"");
        }
    }

    public String getTumlDbLocation() {
        return this.properties.getProperty("umlg.db.location");
    }

    public String getTinkerImplementation() {
        return this.properties.getProperty("tinker.implementation");
    }

    public boolean isStartAdminApplication() {
        return Boolean.parseBoolean(this.properties.getProperty("start.admin.application", "false"));
    }

    public boolean isClearDbOnStartUp() {
        return Boolean.parseBoolean(this.properties.getProperty("start.clear.db", "false"));
    }

    public boolean isCreateDefaultData() {
        return Boolean.parseBoolean(this.properties.getProperty("start.default.data", "false"));
    }

    public String getDefaultDataLoaderClass() {
        return this.properties.getProperty("default.data.class");
    }

    public boolean isTransactionsMutliThreaded() {
        return Boolean.parseBoolean(this.properties.getProperty("transaction.multithreaded", "false"));
    }
}