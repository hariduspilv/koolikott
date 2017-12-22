SET foreign_key_checks = 0;

UPDATE Media m
  JOIN LicenseType t ON m.licenseType = t.id
SET m.licenseType = NULL
WHERE t.name = 'Other';

UPDATE Material m
  JOIN LicenseType t ON m.licenseType = t.id
SET m.licenseType = NULL
WHERE t.name = 'Other';

UPDATE Picture m
  JOIN LicenseType t ON m.licenseType = t.id
SET m.licenseType = NULL
WHERE t.name = 'Other';

DELETE from LicenseType where name = 'Other';

SET foreign_key_checks = 1;