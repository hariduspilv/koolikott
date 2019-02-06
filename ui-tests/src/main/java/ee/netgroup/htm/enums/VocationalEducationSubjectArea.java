package ee.netgroup.htm.enums;

import ee.netgroup.htm.enums.subjectArea.SubjectArea;

public enum VocationalEducationSubjectArea implements SubjectArea {
    ARTS {
        @Override
        public String getText() {
            return "Kunstid";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_KUNSTID";
        }
    },
    HUMANITIES {
        @Override
        public String getText() {
            return "Humanitaaria";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_HUMANITAARIA";
        }
    },
    SOCIAL_AND_BEHAVIORAL_SCIENCE {
        @Override
        public String getText() {
            return "Sotsiaal-ja käitumisteadused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_SOTSIAAL-_JA_KÄITUMISTEADUSED";
        }
    },
    JOURNALISM_AND_INFORMATION {
        @Override
        public String getText() {
            return "Ajakirjandus ja infolevi";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_AJAKIRJANDUS_JA_INFOLEVI";
        }
    },
    BUSINESS_AND_ADMINISTRATION {
        @Override
        public String getText() {
            return "Ärindus ja haldus";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ÄRINDUS_JA_HALDUS";
        }
    },
    LAW {
        @Override
        public String getText() {
            return "Õigus";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ÕIGUS";
        }
    },
    @Deprecated
    BIOLOGICAL_AND_RELATED_SCIENCES {
        @Override
        public String getText() {
            return "Bioteadused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_BIOTEADUSED";
        }
    },
    PHYSICAL_SCIENCES {
        @Override
        public String getText() {
            return "Füüsikalised loodusteadused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_FÜÜSIKALISED_LOODUSTEADUSED";
        }
    },
    MATHEMATICS_AND_STATISTICS {
        @Override
        public String getText() {
            return "Matemaatika ja statistika";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_MATEMAATIKA_JA_STATISTIKA";
        }
    },
    INFORMATION_AND_COMMUNICATION_TECHNOLOGIES {
        @Override
        public String getText() {
            return "Arvutiteadused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ARVUTITEADUSED";
        }
    },
    ENGINEERING_AND_ENGINEERING_TRADES {
        @Override
        public String getText() {
            return "Tehnikaalad";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_TEHNIKAALAD";
        }
    },
    MANUFACTURING_AND_PROCESSING {
        @Override
        public String getText() {
            return "Tootmine ja töötlemine";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_TOOTMINE_JA_TÖÖTLEMINE";
        }
    },
    ARCHITECTURE_AND_CONSTRUCTION {
        @Override
        public String getText() {
            return "Arhitektuur ja ehitus";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ARHITEKTUUR_JA_EHITUS";
        }
    },
    AGRICULTURE {
        @Override
        public String getText() {
            return "Põllumajandus";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_PÕLLUMAJANDUS";
        }
    },
    VETERINARY {
        @Override
        public String getText() {
            return "Veterinaaria";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_VETERINAARIA";
        }
    },
    HEALTH {
        @Override
        public String getText() {
            return "Tervis";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_TERVIS";
        }
    },
    WELFARE {
        @Override
        public String getText() {
            return "Sotsiaalteenused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_SOTSIAALTEENUSED";
        }
    },
    PERSONAL_SERVICES {
        @Override
        public String getText() {
            return "Isikuteenindus";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ISIKUTEENINDUS";
        }
    },
    TRANSPORT_SERVICES {
        @Override
        public String getText() {
            return "Transporditeenused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_TRANSPORDITEENUSED";
        }
    },
    SECURITY_SERVICES {
        @Override
        public String getText() {
            return "Turvamine";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_TURVAMINE";
        }
    },
    PERSONAL_SKILLS_AND_DEVELOPMENT {
        @Override
        public String getText() {
            return "Õpetajakoolitus ja kasvatusteadused";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_ÕPETAJAKOOLITUS_JA_KASVATUSTEADUSED";
        }
    },
    @Deprecated
    COMMUNITY_SANITATION {
        @Override
        public String getText() {
            return "Keskkonnakaitse";
        }

        @Override
        public String getTranslationKey() {
            return "DOMAIN_KESKKONNAKAITSE";
        }
    }

}
