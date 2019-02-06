SET foreign_key_checks = 0;


UPDATE Translation SET translation = 'Koostaja' WHERE translationKey = 'CREATOR' AND translationGroup = 1;
UPDATE Translation SET translation = 'Esmane koostaja' WHERE translationKey = 'ORIGINAL_CREATOR' AND translationGroup = 1;
UPDATE Translation SET translation = 'Otsi pealkirja (sh peatüki pealkirja), kirjelduse, võtmesõna, autori, koostaja, väljaandja järgi' WHERE translationKey = 'HEADER_DEFAULT_SEARCH_PLACEHOLDER' AND translationGroup = 1;
UPDATE Translation SET translation = 'Search by title (incl chapter title), description, tag, author, creator, publisher' WHERE translationKey = 'HEADER_DEFAULT_SEARCH_PLACEHOLDER' AND translationGroup = 3;
UPDATE Translation SET translation = 'Autorites/Koostajates' WHERE translationKey = 'GROUPS_AUTHORS' AND translationGroup = 1;
UPDATE Translation SET translation = 'Authors/Creators' WHERE translationKey = 'GROUPS_AUTHORS' AND translationGroup = 3;

SET foreign_key_checks = 1;