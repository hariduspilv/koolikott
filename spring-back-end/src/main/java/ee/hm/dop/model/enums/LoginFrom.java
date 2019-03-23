package ee.hm.dop.model.enums;

public enum LoginFrom {
    ID_CARD, MOB_ID, EKOOL, STUUDIUM, DEV, TAAT;

    boolean isDev(){
        return this == DEV;
    }
}
