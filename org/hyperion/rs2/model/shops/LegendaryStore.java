package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/14/15
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class LegendaryStore extends CurrencyShop {
    /**
     * @param id
     * @param name
     * @param container
     */
    public LegendaryStore(int id, String name, Container container) {
        super(id, name, container, 13663, false);
        for(final ThirdAgeSet set : ThirdAgeSet.values()) {
            for(final Integer piece : set.ids) {
                addStaticItem(Item.create(piece));
                container.add(Item.create(piece));
            }
        }

        World.getWorld().submit(new Event(2000) {
            @Override
            public void execute() throws IOException {
                final File file = new File("./data/legendaryshop.txt");
                if(!file.exists())
                    file.createNewFile();
                try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    if(!file.exists())
                        file.createNewFile();
                    for(final Item item : container.toArray()) {
                        if(item == null) continue;
                        int price = getSpecialPrice(item);
                        writer.write(String.format("[%s]: PRICE: %d PKP: %,d DP: %,d", item.getDefinition().getName(), price, price * 100_000, price * 1000));
                        writer.newLine();
                    }
                } catch(final Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            }
        });
    }

    @Override
    public int getSpecialPrice(Item item) {
        if(ItemSpawning.canSpawn(item.getId()))
            return 0;
        for(final ThirdAgeSet set : ThirdAgeSet.values()) {
            for(final Piece piece : set.pieces) {
                if(piece.id == item.getId())
                    return piece.price;
            }
        }
        switch(item.getId()) {
            //ringmaster
            case 17662:
                return 15;
            case 13672:
                return 10;
            case 13673:
                return 15;
            case 13674:
                return 8;
            case 13675:
                return 5;

            //scrolls
            case 18344:
                return 5;
            case 18839:
                return 5;

        }
        return 5000;
    }

    private static final Piece of(final int id, final int price) {
        return new Piece(id, price);
    }

    public enum ThirdAgeSet {
        DRUIDIC(Constants.MAGE,
        of(19308, 30), of(19311, 25), of(19314, 40),
                of(19317, 35), of(19320, 30)),
        MELEE(Constants.MELEE, of(10350, 7), of(10352, 6), of(10348, 10), of(10346, 8)),
        RANGE(Constants.RANGE, of(10330, 8), of(10332, 7), of(10334, 8), of(10336, 5)),
        MAGE(Constants.MAGE, of(10342, 8), of(10344, 5), of(10338, 10), of(10340, 8));

        private final Piece[]  pieces;
        public final int type;
        private final List<Integer> ids;
        private ThirdAgeSet(final int type, final Piece... pieces) {
            this.pieces = pieces;
            this.type = type;
            this.ids = Stream.of(pieces).map(p -> p.id).collect(Collectors.toList());

        }

        public boolean has(final Container container) {
            int piececount = 0;
            for(final Item item : container.toArray()) {
                if(item == null) continue;
                if(ids.contains(item.getId()))
                    piececount++;
            }
            return pieces.length == piececount;
        }

        public static ThirdAgeSet setFor(int type) {
            if(type > 5)
                type -= 5;
            for(final ThirdAgeSet set : values()) {
                if(type == set.type)
                    return set;
            }
            return MELEE;
        }
    }

    private static final class Piece {
        private final int id;
        private final int price;

        public Piece(final int id, final int price) {
            this.id = id;
            this.price = price;
        }
    }




}
