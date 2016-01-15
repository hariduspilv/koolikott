package ee.hm.dop.model.ehis;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Role {

    public static enum InstitutionalRole {
        PRINCIPAL("koolijuht"), TEACHER("õpetaja"), STUDENT("õpilane");

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
