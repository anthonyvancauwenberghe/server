package org.hyperion.rs2.model.content.itfactivation;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.PacketBuilder;

import java.util.stream.Stream;

/**
 * Created by Jet on 1/8/2015.
 */
public class ChangeMaxCape extends Interface implements ContentTemplate{

    private static final int ID = 4;

    public ChangeMaxCape() {
        super(ID);
    }


    @Override
    public void handle(Player player, Packet pkt) {
        player.maxCapePrimaryColor = pkt.getInt();
        player.maxCapeSecondaryColor = pkt.getInt();
        player.sendMessage("You successfully changed your colors");
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);

    }

    @Override
    public boolean clickObject(Player player, int type, int a, int b, int c, int d) {
        show(player);
        final PacketBuilder builder = createDataBuilder();
        final int indexOne = Stream.of(Color.values()).filter(x -> x.color == player.maxCapePrimaryColor).findFirst().get().ordinal();
        final int indexTwo = Stream.of(Color.values()).filter(x -> x.color == player.maxCapeSecondaryColor).findFirst().get().ordinal();
        builder.put((byte)indexOne).put((byte)indexTwo);
        player.write(builder.toPacket());
        return true;
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.ITEM_OPTION3)
            return new int[]{12744};
        return new int[0];
    }
}
