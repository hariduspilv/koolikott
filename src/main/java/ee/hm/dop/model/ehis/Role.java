package ee.hm.dop.model.ehis;

public class Role {

    public static enum InstitutionalRole {
        PRINCIPAL("direktor"), TEACHER("õpetaja"), STUDENT("õpilane");

        private final String estonianName;

        InstitutionalRole(String estonianName) {
            this.estonianName = estonianName;
        }

        public static InstitutionalRole byEstonianName(String estonianName) {
            InstitutionalRole result = null;

            for (InstitutionalRole role : InstitutionalRole.values()) {
                if (role.estonianName.equalsIgnoreCase(estonianName)) {
                    result = role;
                    break;
                }
            }
            return result;
        }
    }

    private InstitutionalRole institutionalRole;
    private String schoolYear;
    private String schoolClass;

    public InstitutionalRole getInstitutionalRole() {
        return institutionalRole;
    }

    public void setInstitutionalRole(InstitutionalRole institutionalRole) {
        this.institutionalRole = institutionalRole;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }
}
