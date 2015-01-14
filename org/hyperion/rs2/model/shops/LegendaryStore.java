package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

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
    }

    @Override
    public int getSpecialPrice(Item item) {
        switch(item.getId()) {
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
            case 18344:
                return 5;
            case 18839:
                return 5;
        }
        return 5000;
    }



}
