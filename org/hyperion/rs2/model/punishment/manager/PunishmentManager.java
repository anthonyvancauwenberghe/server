package org.hyperion.rs2.model.punishment.manager;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Time;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.sql.MySQLConnection;
import org.hyperion.rs2.util.TextUtils;

public final class PunishmentManager {

    private static PunishmentManager instance;

    private final MySQLConnection connection;
    private final Map<String, PunishmentHolder> holders;

    private PunishmentManager(final MySQLConnection connection){
        this.connection = connection;

        holders = new HashMap<>();
    }

    public MySQLConnection getConnection(){
        return connection;
    }

    public boolean load(){
        ResultSet rs = null;
        try{
            rs = connection.query("SELECT * FROM punishments WHERE active = 1");
            while(rs.next()){
                try {
                    final String issuer = rs.getString("issuer");
                    final String victim = rs.getString("victim");
                    final String ip = rs.getString("ip");
                    final int mac = rs.getInt("mac");
                    final String specialUidText = rs.getString("specialUid");
                    final String[] specialUidParts = specialUidText == null ? new String[0] : specialUidText.split(",");
                    int[] specialUid = null;
                    if(specialUidParts.length == 20){
                        specialUid = new int[20];
                        for(int i = 0; i < 20; i++)
                            specialUid[i] = Integer.parseInt(specialUidParts[i]);
                    }
                    final Target target = Target.valueOf(rs.getString("target"));
                    final Type type = Type.valueOf(rs.getString("type"));
                    final long startTime = rs.getLong("time");
                    final long duration = rs.getLong("duration");
                    final TimeUnit unit = TimeUnit.valueOf(rs.getString("unit"));
                    final String reason = rs.getString("reason");
                    final Punishment p = Punishment.create(
                            issuer, victim, ip, mac, specialUid,
                            Combination.of(target, type),
                            Time.create(startTime, duration, unit),
                            reason
                    );
                    add(p);
                }catch(final Exception ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(Exception ex){}
            }
        }
    }

    public void add(final Punishment p){
        PunishmentHolder holder = get(p.getVictimName());
        if(holder == null){
            holder = PunishmentHolder.create(p.getVictimName(), p.getVictimIp());
            add(holder);
        }
        holder.add(p);
    }

    public void add(final PunishmentHolder p){
        holders.put(p.getVictimName().toLowerCase(), p);
    }

    public PunishmentHolder get(final String victim){
        return holders.get(victim.toLowerCase());
    }

    public List<Punishment> getByIp(final String ip){
        final List<Punishment> list = new ArrayList<>();
        for(final PunishmentHolder holder : getHolders())
            for(final Punishment p : holder.getPunishments())
                if(ip.equals(p.getVictimIp()))
                    list.add(p);
        return list;
    }

    public List<Punishment> getByMac(final int mac){
        final List<Punishment> list = new ArrayList<>();
        for(final PunishmentHolder holder : getHolders())
            for(final Punishment p : holder.getPunishments())
                if(mac == p.getVictimMac())
                    list.add(p);
        return list;
    }

    public boolean isBanned(final String name, final String ip, final int mac, final int[] specialUid){
        if(name != null){
            final PunishmentHolder holder = get(name);
            if(holder != null){
                for(final Punishment p : holder.getPunishments()){
                    if(p.getCombination().getType() == Type.BAN){
                        return true;
                    }
                }
            }
        }
        for(final PunishmentHolder h : getHolders()){
            for(final Punishment p : h.getPunishments()){
                if(p.getCombination().getType() != Type.BAN)
                    continue;
                if(ip != null && p.getCombination().getTarget() == Target.IP && ip.equalsIgnoreCase(p.getVictimIp()))
                    return true;
                if(mac != -1 && p.getCombination().getTarget() == Target.MAC && mac == p.getVictimMac())
                    return true;
                if(specialUid != null && p.getCombination().getTarget() == Target.SPECIAL && Arrays.equals(specialUid, p.getVictimSpecialUid()))  {
                    TextUtils.writeToFile("./data/specialUidStops.txt", "Special UID ban stopped: " + name);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(final String victim){
        return get(victim) != null;
    }

    public Collection<PunishmentHolder> getHolders(){
        return holders.values();
    }

    public static void init(final MySQLConnection connection){
        instance = new PunishmentManager(connection);
        instance.load();
    }

    public static PunishmentManager getInstance(){
        return instance;
    }
}
