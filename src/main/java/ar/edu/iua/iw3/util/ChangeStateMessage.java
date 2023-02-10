package ar.edu.iua.iw3.util;

import java.io.Serializable;

public class ChangeStateMessage<T> implements Serializable {
    private static final long serialVersionUID = -3982857348314587186L;

    public static String TYPE_GRAPH_DATA = "GRAPH_DATA";
    public static String TYPE_NOTIFICA = "NOTIFICA";

    public ChangeStateMessage(String type, T payload) {
        super();
        this.type = type;
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private T payload;
    private String type;

}
