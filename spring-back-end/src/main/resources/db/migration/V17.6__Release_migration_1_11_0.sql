SET foreign_key_checks = 0;

INSERT INTO ReportingReason (improperContent, reason)
  SELECT
    ic.id,
    'LO_CONTENT'
  FROM ImproperContent ic
  WHERE ic.reviewed = 0;

INSERT INTO ReportingReason (improperContent, reason)
  SELECT
    ic.id,
    'LO_FORM'
  FROM ImproperContent ic
  WHERE ic.reviewed = 0;

INSERT INTO ReportingReason (improperContent, reason)
  SELECT
    ic.id,
    'LO_METADATA'
  FROM ImproperContent ic
  WHERE ic.reviewed = 0;

UPDATE ImproperContent
    SET reportingText = 'Raporteeritud enne põhjuse valimise funktsionaalsuse implementeerimist. Kõik raporteerimise põhjused on määratud valituks seoses põhjuse valimise funktsionaalsuse täiendamisega'
WHERE reviewed = 0;

SET foreign_key_checks = 1;