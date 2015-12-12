package org.hyperion.rs2.model.content.misc;

public class TicketBuilder {
    private final long enteredTime;
    private String reason;
    private boolean answered = false;

    public TicketBuilder(final String reason, final long enteredTime) {
        this.reason = reason;
        this.enteredTime = enteredTime;
    }

    public String getReason() {
        return reason;
    }

    public long startTime() {
        return enteredTime;
    }

    public void answerTicket() {
        answered = true;
        reason = "@str@" + reason;
    }

    public boolean isAnswered() {
        return answered;
    }

}
