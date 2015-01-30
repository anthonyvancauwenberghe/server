package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Time;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.Packet;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jet on 1/29/2015.
 */
public class PinInterface extends Interface{

    private static final int ALLOWED_ATTEMPTS = 3;

    private static final int SET_PIN = 1;
    private static final int ENTER_PIN = 2;
    private static final int INVALID_PIN = 3;
    private static final int ENTER_LATER = 4;

    public static final int ID = 9;

    public PinInterface(){
        super(ID);
    }

    public void set(final Player player){
        player.write(createDataBuilder().put((byte) SET_PIN).toPacket());
        show(player);
    }

    public void enter(final Player player){
        player.write(createDataBuilder().put((byte)ENTER_PIN).toPacket());
        show(player);
    }

    public void handle(final Player player, final Packet pkt){
        final int id = pkt.get();
        switch(id){
            case SET_PIN:
                final int pin = pkt.getInt();
                if(pin < 1){
                    player.write(createDataBuilder().put((byte)INVALID_PIN).toPacket());
                    return;
                }
                if(player.pin != -1){
                    player.sendf("You have already set your pin");
                    return;
                }
                player.pin = pin;
                player.verified = true;
                hide(player);
                player.sendf("Successfully set your pin to: @blu@" + pin);
                break;
            case ENTER_PIN:
                final int enteredPin = pkt.getInt();
                if(enteredPin != player.pin){
                    player.write(createDataBuilder().put((byte)INVALID_PIN).toPacket());
                    player.getExtraData().put("pin_attempts", player.getExtraData().getInt("pin_attempts") + 1);
                    if(player.getExtraData().getInt("pin_attempts") > ALLOWED_ATTEMPTS){
                        final Punishment p = Punishment.create(
                                "Server Pin",
                                player,
                                Combination.of(Target.ACCOUNT, Type.BAN),
                                Time.create(30, TimeUnit.SECONDS),
                                "Too many invalid PIN attempts"
                        );
                        p.apply();
                        PunishmentManager.getInstance().add(p);
                        for(final Player pl : World.getWorld().getPlayers()){
                            if(pl == null || !Rank.hasAbility(pl, Rank.ADMINISTRATOR))
                                continue;
                            pl.sendf("@red@Potential Hack Attempt. Name: @blu@%s @red@| IP: @blu@%s -> %s", player.getName(), player.lastIp, player.getShortIP());
                        }
                    }
                    return;
                }
                player.verified = true;
                hide(player);
                player.sendf("Confirmed identity.");
                break;
            case ENTER_LATER:
                if(player.pin != -1)
                    return;
                player.verified = true;
                hide(player);
                player.sendf("You should consider setting a PIN to ensure account safety");
                break;
            default:
                System.out.println("invalid id");
        }
    }

    public static PinInterface get(){
        return InterfaceManager.get(ID);
    }
}
