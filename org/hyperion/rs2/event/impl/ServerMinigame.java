package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

public class ServerMinigame extends Event {
	
	public ServerMinigame() {
		super(Time.ONE_HOUR * 5);
	}

    public static final CountDownEventBuilder[] builders = new CountDownEventBuilder[]{
            new CountDownEventBuilder("Fight pits", "::fightpits", Location.create(2399, 5178, 0), () -> FightPits.startEvent()),
            new CountDownEventBuilder("Hybridding", "hybrid"),
            new CountDownEventBuilder("OldSchool PK", "ospk"),
            new CountDownEventBuilder("Pure Pking", "purepk")
    };

	@Override
	public void execute() {
		World.getWorld().submit(new CountDownEvent(builders[Misc.random(builders.length-1)]));
	}

    public static class CountDownEventBuilder {
        public final Runnable run;
        public final String command;
        public final String name;
        public final Location location;

        public CountDownEventBuilder(final String name, final String specialArea) {
            this(name, "::"+specialArea, SpecialAreaHolder.get(specialArea).get());
        }

        public CountDownEventBuilder(final String name, final String command, final SpecialArea area) {
            this(name, command, area.getDefaultLocation(), () -> area.createEvent());
        }

        public CountDownEventBuilder(final String name, final String command, final Location location, Runnable run) {
            this.name = name;
            this.command = command;
            this.location = location;
            this.run = run;
        }
    }

}
