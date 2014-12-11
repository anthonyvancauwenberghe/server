package org.hyperion.rs2.model.possiblehacks;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PossibleHack {

    public final String name;
    public final String ip;
    public final String date;

    public PossibleHack(final String name, final String ip, final String date) {
        this.name = name;
        this.ip = ip;
        this.date = date;
    }

    @Override public abstract String toString();


}
