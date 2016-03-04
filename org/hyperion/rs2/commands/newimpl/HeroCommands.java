package org.hyperion.rs2.commands.newimpl;

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class HeroCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList();
    }

}
