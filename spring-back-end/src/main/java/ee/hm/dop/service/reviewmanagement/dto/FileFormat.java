package ee.hm.dop.service.reviewmanagement.dto;

public enum FileFormat {
    xls, xlsx, csv;

    public boolean isExcel(){
        return this == xls || this == xlsx;
    }
}
