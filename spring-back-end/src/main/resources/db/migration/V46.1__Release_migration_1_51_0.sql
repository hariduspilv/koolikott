SET foreign_key_checks = 0;

call insert_translation('COPY_CHAPTER_LINK', 'Peatüki asukoha link kopeeritud!', 'Chapter link copied!');
call update_translations('TERMS_OF_LICENSE', 'litsentsi tingimustega. Litsents kehtib ainult E-koolikotis asuvale tekstile ja üles laetud failidele, ei laiene lingiga jagatud sisule.', 'license. This license applies to the text and files uploaded to E-koolikott, but not to external links.', 'litsentsi tingimustega. Litsents kehtib ainult E-koolikotis asuvale tekstile ja üles laetud failidele, ei laiene lingiga jagatud sisule. (RU)');

SET foreign_key_checks = 1;