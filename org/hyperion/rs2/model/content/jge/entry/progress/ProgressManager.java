package org.hyperion.rs2.model.content.jge.entry.progress;

import org.hyperion.rs2.model.content.jge.entry.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Administrator on 9/24/2015.
 */
public class ProgressManager {

    public final Entry entry;
    public final List<Progress> list;

    public ProgressManager(final Entry entry){
        this.entry = entry;

        list = new ArrayList<>();
    }

    public Stream<Progress> stream(){
        return list.stream();
    }

    public double quantityPercent(){
        return totalQuantity() * 100d / entry.itemQuantity;
    }

    public int totalPrice(){
        switch(entry.type){
            case BUYING:
                return stream()
                        .mapToInt(p -> p.unitPrice)
                        .sum() * totalQuantity();
            case SELLING:
                return totalQuantity() * entry.unitPrice;
            default:
                return 0;
        }

    }

    public int totalQuantity(){
        return stream()
                .mapToInt(p -> p.quantity)
                .sum();
    }

    public int remainingQuantity(){
        return entry.itemQuantity - totalQuantity();
    }

    public void add(final String playerName, final int unitPrice, final int quantity){
        list.add(new Progress(playerName, entry.type.opposite(), unitPrice, quantity));
    }

    public boolean completed(){
        return totalQuantity() == entry.itemQuantity;
    }
}
