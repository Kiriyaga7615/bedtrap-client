package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;


public class KeyEvent extends Cancelled {

    private int code;

    public KeyEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
