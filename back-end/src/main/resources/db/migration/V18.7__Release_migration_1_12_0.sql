SET foreign_key_checks = 0;

ALTER TABLE ImproperContent ADD needs_rr BOOLEAN DEFAULT 0;

INSERT INTO ImproperContent (creator, learningObject, createdAt, reviewed, reviewedAt, status, needs_rr)
  SELECT bc.creator, bc.material, bc.added,
    CASE WHEN bc.deleted=1 THEN 1 ELSE 0 END,
    CASE WHEN bc.deleted=1 THEN CURRENT_TIMESTAMP ELSE NULL END,
    CASE WHEN bc.deleted=1 THEN 'ACCEPTED' ELSE NULL END,
    1
  FROM BrokenContent bc;

INSERT INTO ReportingReason (improperContent, reason)
  SELECT ic.id, 'LO_FORM'
  FROM ImproperContent ic
  WHERE ic.needs_rr = 1;

SET foreign_key_checks = 1;