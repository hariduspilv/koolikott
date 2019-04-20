package ee.hm.dop.model.enums;

public enum LoginFrom {
    ID_CARD, MOB_ID, EKOOL, STUUDIUM, DEV, TAAT, HAR_ID;

    public boolean isDev(){
        return this == DEV;
    }

}
