package org.hyperion.rs2.model.punishment;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.sql.MySQLConnection;
import org.hyperion.rs2.util.TextUtils;

public class Punishment {

    private String issuer;
    private final String victim;
    private final String victimIp;
    private final int victimMac;
    private final Combination combination;
    private final Time time;
    private String reason;

    public Punishment(final String issuer, final String victim, final String victimIp, final int victimMac, final Combination combination, final Time time, final String reason){
        this.issuer = issuer;
        this.victim = victim;
        this.victimIp = victimIp;
        this.victimMac = victimMac;
        this.combination = combination;
        this.time = time;
        this.reason = reason;
    }

    public Punishment(final Player issuer, final String victim, final String victimIp, final int victimMac, final Combination combination, final Time time, final String reason){
        this(issuer.getName(), victim, victimIp, victimMac, combination, time, reason);
    }

    public Punishment(final Player issuer, final Player victim, final Combination combination, final Time time, final String reason){
        this(issuer, victim.getName(), victim.getShortIP(), victim.getUID(), combination, time, reason);
    }

    public PunishmentHolder getHolder(){
        return PunishmentManager.getInstance().get(getVictimName());
    }

    public void send(final Player player, final boolean alert){
        final Player issuer = getIssuer();
        final String issuerName = issuer != null ? issuer.getSafeDisplayName() : getIssuerName();
        if(alert){
            player.sendf(
                    "Alert##%s %s - Issued By %s (%s)##%s##Expires: %s",
                    TextUtils.titleCase(getVictimName()),
                    getCombination(),
                    TextUtils.titleCase(issuerName),
                    getTime(),
                    TextUtils.titleCase(getReason()),
                    getTime().isExpired() ? "NOW!" : getTime().getExpirationDateStamp()
            );
        }else{
            player.sendf("@red@----------------------------------------------------------------------------------------");
            player.sendf("@blu@%s %s@bla@ - Issued By @blu@%s@bla@ (@blu@%s@bla@)", TextUtils.titleCase(getVictimName()), getCombination(), TextUtils.titleCase(issuerName), getTime());
            player.sendf("Reason: @blu@%s", TextUtils.titleCase(getReason()));
            player.sendf("Issued: @blu@%s", getTime().getStartDateStamp());
            player.sendf("Expires: @blu@%s", getTime().isExpired() ? "NOW!" : getTime().getExpirationDateStamp());
            if(!getTime().isExpired())
                player.sendf("Remaining: @blu@%s", getTime().getRemainingTimeStamp());
            player.sendf("@red@----------------------------------------------------------------------------------------");
        }
    }

    public boolean apply(){
        final Player victim = getVictim();
        if(victim != null){
            getCombination().apply(victim);
            return true;
        }
        boolean applied = false;
        for(final Player p : World.getWorld().getPlayers()){
            if(p == null)
                continue;
            switch(getCombination().getTarget()){
                case IP:
                    if(!p.getShortIP().equals(getVictimIp()))
                        break;
                    getCombination().getType().apply(p);
                    applied = true;
                    break;
                case MAC:
                    if(p.getUID() != getVictimMac())
                        break;
                    getCombination().getType().apply(p);
                    applied = true;
                    break;
            }
        }
        return applied;
    }

    public boolean isApplied(){
        final Player victim = getVictim();
        if(victim != null)
            return getCombination().isApplied(victim);
        for(final Player p : World.getWorld().getPlayers()){
            if(p == null)
                continue;
            switch(getCombination().getTarget()){
                case IP:
                    if(!p.getShortIP().equals(getVictimIp()))
                        break;
                    if(!getCombination().getType().isApplied(p))
                        return false;
                    break;
                case MAC:
                    if(p.getUID() != getVictimMac())
                        break;
                    if(!getCombination().getType().isApplied(p))
                        return false;
                    break;
            }
        }
        return true;
    }

    public boolean unapply(){
        final Player victim = getVictim();
        if(victim != null){
            getCombination().unapply(victim);
            return true;
        }
        boolean unapplied = false;
        for(final Player p : World.getWorld().getPlayers()){
            if(p == null)
                continue;
            switch(getCombination().getTarget()){
                case IP:
                    if(!p.getShortIP().equals(getVictimIp()))
                        break;
                    getCombination().getType().unapply(p);
                    unapplied = true;
                    break;
                case MAC:
                    if(p.getUID() != getVictimMac())
                        break;
                    getCombination().getType().unapply(p);
                    unapplied = true;
                    break;
            }
        }
        return unapplied;
    }

    public String getIssuerName(){
        return issuer;
    }

    public void setIssuerName(final String issuer){
        this.issuer = issuer;
    }

    public void setIssuer(final Player issuer){
        setIssuerName(issuer.getName());
    }

    public Player getIssuer(){
        return World.getWorld().getPlayer(getIssuerName());
    }

    public String getVictimName(){
        return victim;
    }

    public Player getVictim(){
        return World.getWorld().getPlayer(getVictimName());
    }

    public String getVictimIp(){
        return victimIp;
    }

    public int getVictimMac(){
        return victimMac;
    }

    public Combination getCombination(){
        return combination;
    }

    public Time getTime(){
        return time;
    }

    public String getReason(){
        return reason;
    }

    public void setReason(final String reason){
        this.reason = reason;
    }

    public void insert(final MySQLConnection connection){
        final String query = String.format(
                "INSERT INTO punishments (issuer, victim, ip, mac, target, type, time, duration, unit, reason, active)" +
                        " VALUES ('%s', '%s', '%s', %d, '%s', '%s', %d, %d, '%s', '%s', 1)",
                getIssuerName(),
                getVictimName(),
                getVictimIp(),
                getVictimMac(),
                getCombination().getTarget().name(),
                getCombination().getType().name(),
                getTime().getStartTime(),
                getTime().getDuration(),
                getTime().getUnit().name(),
                getReason()
        );
        connection.offer(query);
    }

    public void insert(){
        insert(PunishmentManager.getInstance().getConnection());
    }

    public void update(final MySQLConnection connection){
        final String query = String.format(
                "UPDATE punishments SET issuer = '%s', time = %d, duration = %d, unit = '%s', reason = '%s' WHERE victim = '%s' AND target = '%s' AND type = '%s'",
                getIssuerName(),
                getTime().getStartTime(),
                getTime().getDuration(),
                getTime().getUnit().name(),
                getReason(),
                getVictimName(),
                getCombination().getTarget().name(),
                getCombination().getType().name()
        );
        connection.offer(query);
    }

    public void update(){
        update(PunishmentManager.getInstance().getConnection());
    }

    public void setActive(final MySQLConnection connection, final boolean isActive){
        final String query = String.format(
                "UPDATE punishments SET active = %d WHERE victim = '%s' AND target = '%s' AND type = '%s'",
                isActive ? 1 : 0,
                getVictimName(),
                getCombination().getTarget().name(),
                getCombination().getType().name()
        );
        connection.offer(query);
    }

    public void setActive(final boolean isActive){
        setActive(PunishmentManager.getInstance().getConnection(), isActive);
    }

    public void delete(final MySQLConnection connection){
        final String query = String.format(
                "DELETE FROM punishments WHERE victim = '%s' AND target = '%s' AND type = '%s'",
                getVictimName(),
                getCombination().getTarget().name(),
                getCombination().getType().name()
        );
        connection.offer(query);
    }

    public void delete(){
        delete(PunishmentManager.getInstance().getConnection());
    }

    public static Punishment create(final String issuer, final String victim, final String victimIp, final int victimMac, final Combination combination, final Time time, final String reason){
        return new Punishment(issuer, victim, victimIp, victimMac, combination, time, reason);
    }

    public static Punishment create(final Player issuer, final String victim, final String victimIp, final int victimMac, final Combination combination, final Time time, final String reason){
        return new Punishment(issuer.getName(), victim, victimIp, victimMac, combination, time, reason);
    }

    public static Punishment create(final Player issuer, final Player victim, final Combination combination, final Time time, final String reason){
        return new Punishment(issuer, victim, combination, time, reason);
    }
}
