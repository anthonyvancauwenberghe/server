package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

public class ServerMinigame extends Event {
	
	public ServerMinigame() {
		super(Time.ONE_HOUR * 4);
	}

    public static final CountDownEventBuilder[] builders = new CountDownEventBuilder[]{
            new CountDownEventBuilder("Fight pits", "fightpits", Location.create(2399, 5178, 0),"3X PKP Game", () -> FightPits.startEvent()),
            new CountDownEventBuilder("Hybridding", "hybrid"),
            new CountDownEventBuilder("OldSchool PK", "ospk"),
            new CountDownEventBuilder("Pure Pking", "purepk"),
            new CountDownEventBuilder(8133, Location.create(2521,4647,0)),
            new CountDownEventBuilder(8549, Location.create(2660, 9634, 0)),
            new CountDownEventBuilder(50, Location.create(2270, 4687, 0))
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
        public final String message;

        public CountDownEventBuilder(final int npcId, final Location location) {
            this(NPCDefinition.forId(npcId).getName(), "the event tab", location, "2X Drop Rates for 30Min", () -> {
                NpcDeathEvent.npcIdForDoubleDrops = npcId;
                World.getWorld().submit(new Event(Time.THIRTY_MINUTES) {
                    public void execute() {
                        NpcDeathEvent.npcIdForDoubleDrops = -1;
                    }
                });
            });
        }

        public CountDownEventBuilder(final String name, final String specialArea) {
            this(name, "::"+specialArea, SpecialAreaHolder.get(specialArea).get());
        }

        public CountDownEventBuilder(final String name, final String command, final SpecialArea area) {
            this(name, command, area.getDefaultLocation(), "5X PKP for 30min", () -> area.createEvent());
        }

        public CountDownEventBuilder(final String name, final String command, final Location location, final String msg, Runnable run) {
            this.name = name;
            this.command = command;
            this.location = location;
            this.run = run;
            this.message = msg;
        }
    }

}
