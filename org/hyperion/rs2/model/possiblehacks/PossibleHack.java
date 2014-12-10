package org.hyperion.rs2.model.possiblehacks;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PossibleHack {

    private final String name;
    private final String ip;

    public PossibleHack(final String name, final String ip) {
        this.name = name;
        this.ip = name;
    }


}
