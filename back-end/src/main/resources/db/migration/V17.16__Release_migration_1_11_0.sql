SET foreign_key_checks = 0;

UPDATE Translation WHERE translationGroup = 1 AND translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' SET translation = 'Teavita ebasobivast võtmesõnast');
UPDATE Translation WHERE translationGroup = 3 AND translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' SET translation = 'Notify of an improper tag');
UPDATE Translation WHERE translationGroup = 2 AND translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' SET translation = '');

SET foreign_key_checks = 1;
