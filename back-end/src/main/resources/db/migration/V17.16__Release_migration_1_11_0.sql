SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Teavita ebasobivast võtmesõnast'
WHERE translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' and translationGroup = 1;
UPDATE Translation SET translation = 'Notify of an improper tag'
WHERE translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' and translationGroup = 3;
UPDATE Translation SET translation = ''
WHERE translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER' and translationGroup = 2;

SET foreign_key_checks = 1;
