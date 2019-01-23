SET foreign_key_checks = 0;

UPDATE Translation set translation = 'Otsi pealkirja (sh peatüki pealkirja), kirjelduse, võtmesõna, autori, väljaandja järgi' WHERE translationKey = 'HEADER_DEFAULT_SEARCH_PLACEHOLDER' and translationGroup = 1;
UPDATE Translation set translation = 'Search by title (incl chapter title), description, tag, author, publisher' WHERE translationKey = 'HEADER_DEFAULT_SEARCH_PLACEHOLDER' and translationGroup = 3;

SET foreign_key_checks = 1;
