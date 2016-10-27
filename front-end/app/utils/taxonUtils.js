define(function () {
    return {
        constants: {
            EDUCATIONAL_CONTEXT: '.EducationalContext',
            DOMAIN: '.Domain',
            SUBJECT: '.Subject',
            TOPIC: '.Topic',
            SUBTOPIC: '.Subtopic',
            SPECIALIZATION: '.Specialization',
            MODULE: '.Module'
        },

        getEducationalContext: function (taxon) {
            return this.getTaxon(taxon, this.constants.EDUCATIONAL_CONTEXT);
        },

        getDomain: function (taxon) {
            return this.getTaxon(taxon, this.constants.DOMAIN);
        },

        getSubject: function (taxon) {
            return this.getTaxon(taxon, this.constants.SUBJECT);
        },

        getTopic: function (taxon) {
            return this.getTaxon(taxon, this.constants.TOPIC);
        },

        getSubtopic: function (taxon) {
            return this.getTaxon(taxon, this.constants.SUBTOPIC);
        },

        getSpecialization: function (taxon) {
            return this.getTaxon(taxon, this.constants.SPECIALIZATION);
        },

        getModule: function (taxon) {
            return this.getTaxon(taxon, this.constants.MODULE);
        },

        getTaxon: function (taxon, level) {
            if (!taxon) {
                return;
            }

            if (taxon.level === this.constants.EDUCATIONAL_CONTEXT) {
                return taxon.level === level ? taxon : null;
            }

            if (taxon.level === this.constants.DOMAIN) {
                return taxon.level === level ? taxon : this.getTaxon(taxon.educationalContext, level);
            }

            if (taxon.level === this.constants.SUBJECT) {
                return taxon.level === level ? taxon : this.getTaxon(taxon.domain, level);
            }

            if (taxon.level === this.constants.TOPIC) {
                if (taxon.level === level) return taxon;

                var parent = taxon.subject;
                if (!parent || angular.equals(parent, {})) {
                    parent = taxon.domain;

                    if (!parent || angular.equals(parent, {})) {
                        parent = taxon.module;
                    }
                }

                return this.getTaxon(parent, level);
            }

            if (taxon.level === this.constants.SUBTOPIC) {
                return taxon.level === level ? taxon : this.getTaxon(taxon.topic, level);
            }

            if (taxon.level === this.constants.SPECIALIZATION) {
                return taxon.level === level ? taxon : this.getTaxon(taxon.domain, level);
            }

            if (taxon.level === this.constants.MODULE) {
                return taxon.level === level ? taxon : this.getTaxon(taxon.specialization, level);
            }
        }
    }
});
