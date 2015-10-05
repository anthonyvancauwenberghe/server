package org.hyperion.rs2.model.content.jge.event;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.util.Time;

import java.io.IOException;

public class PulseGrandExchangeEvent extends Event {

    public PulseGrandExchangeEvent(){
        super(15 * Time.ONE_MINUTE);
    }

    public void execute() throws IOException{
        JGrandExchange.getInstance().stream()
                .forEach(JGrandExchange.getInstance()::submit);
    }
}
