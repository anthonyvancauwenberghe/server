package cluescrolleditor.cluescroll.reward;

import cluescrolleditor.cluescroll.util.ClueScrollUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemReward extends Reward {

    private int id;

    public ItemReward(final int id, final int minAmount, final int maxAmount, final int chance){
        super(Type.ITEM, minAmount, maxAmount, chance);
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(final int id){
        this.id = id;
    }

    protected void append(final Document doc, final Element root){
        root.appendChild(ClueScrollUtils.createElement(doc, "id", id));
    }

    public String toString(){
        return String.format("%s: %d", super.toString(), id);
    }

    public static ItemReward parse(final Element element){
        final int itemId = ClueScrollUtils.getInteger(element, "id");
        final int minAmount = ClueScrollUtils.getInteger(element, "minAmount");
        final int maxAmount = ClueScrollUtils.getInteger(element, "maxAmount");
        final int chance = ClueScrollUtils.getInteger(element, "chance");
        return new ItemReward(itemId, minAmount, maxAmount, chance);
    }
}
