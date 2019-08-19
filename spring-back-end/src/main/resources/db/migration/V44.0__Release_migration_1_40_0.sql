SET foreign_key_checks = 0;

call insert_translation('MATERIAL_SAVED', 'Materjal salvestatud', 'Material saved');
call insert_translation('MATERIAL_MAKE_PUBLIC', 'Kas soovid muuta materjali avalikuks?', 'Do you wish to make the material public?');
call insert_translation('MATERIAL_WARNING', 'Avalik materjal on kõigile nähtav!', 'Public material is visible to everybody!');
call insert_translation('MATERIAL_YES', 'Välju ja tee avalikuks', 'Exit and make public');
call insert_translation('MATERIAL_NO', 'Välju avalikuks tegemata', 'Exit without making public');

SET foreign_key_checks = 1;
