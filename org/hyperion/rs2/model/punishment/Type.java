package org.hyperion.rs2.model.punishment;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.misc2.Zanaris;

public enum Type {

    JAIL{
        public void apply(final Player player){
            player.setTeleportTarget(Jail.LOCATION);
        }

        public boolean isApplied(final Player player){
            return Jail.inJail(player);
        }

        public void unapply(final Player player){
            player.setTeleportTarget(Zanaris.LOCATION);
        }
    },
    YELL_MUTE{
        public void apply(final Player player){
            player.yellMuted = true;
        }

        public boolean isApplied(final Player player){
            return player.yellMuted;
        }

        public void unapply(final Player player){
            player.yellMuted = false;
        }
    },
    MUTE{
        public void apply(final Player player){
            player.isMuted = true;
        }

        public boolean isApplied(final Player player){
            return player.isMuted;
        }

        public void unapply(final Player player){
            player.isMuted = false;
        }
    },
    BAN{
        public void apply(final Player player){
            player.getSession().close();
        }

        public boolean isApplied(final Player player){
            //empty impl
            return true;
        }

        public void unapply(final Player player){
            //empty implementation
        }
    };

    public void apply(final Player player){
        throw new AbstractMethodError("will never happen");
    }

    public boolean isApplied(final Player player){
        throw new AbstractMethodError("will never happen");
    }

    public void unapply(final Player player){
        throw new AbstractMethodError("will never happen");
    }
}
