SET foreign_key_checks = 0;

call update_translations('AGREE_TO', 'Olen tutvunud ja nõustun E-koolikoti', 'I have read and agree to the', 'Olen tutvunud ja nõustun E-koolikoti');

call update_translations('WITH_TERMS', 'kasutustingimustega', 'Terms of Use', 'kasutustingimustega');

call update_translations('AGREE_TO_GDPR', 'Olen tutvunud ja nõustun', 'I have read and agree to the processing of my personal data according to the', 'Olen tutvunud ja nõustun');

call update_translations('GDPR_TERMS', 'isikuandmete töötlemise tingimustega E-koolikotis', 'GDPR Processing Conditions', 'isikuandmete töötlemise tingimustega E-koolikotis');

SET foreign_key_checks = 1;
