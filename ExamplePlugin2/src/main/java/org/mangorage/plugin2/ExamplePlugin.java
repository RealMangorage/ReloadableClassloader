package org.mangorage.plugin2;

import org.mangorage.classloader.api.IPlugin;

public class ExamplePlugin implements IPlugin {
    public ExamplePlugin() {

    }

    @Override
    public void onLoad() {
        Test.init();
    }

    @Override
    public void unload() {

    }
}
