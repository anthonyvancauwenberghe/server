package org.hyperion.rs2.model.punishment.event;

import java.util.Iterator;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.util.Time;

public class PunishmentExpirationEvent extends Event {

    public PunishmentExpirationEvent(){
        super(Time.ONE_MINUTE / 2);
    }

    public void execute(){
        for(final PunishmentHolder holder : PunishmentManager.getInstance().getHolders()){
            final Iterator<Punishment> itr = holder.getPunishments().iterator();
            while(itr.hasNext()){
                final Punishment punishment = itr.next();
                if(!punishment.getTime().isExpired()){
                    if(!punishment.isApplied()){
                        if(punishment.apply() && punishment.getVictim() != null) {
                            punishment.getVictim().sendf("Your %s did not expire!", punishment.getCombination());
                            punishment.send(punishment.getVictim(), true);
                        }
                    }
                    continue;
                }
                if(punishment.unapply() && punishment.getVictim() != null)
                    punishment.send(punishment.getVictim(), true);
                itr.remove();
                punishment.setActive(false);
            }
        }
    }
}
