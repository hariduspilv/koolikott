SET foreign_key_checks = 0;

call insert_translation('MATERIAL_PUBLICATION_TITLE', 'Materjali avalikustamine', 'Material publication');
call insert_translation('MATERIAL_LICENSE_WARNING', 'Kuna materjalil puudub CC BY 3.0 litsents, ei saa materjali muuta avalikuks. Esmalt tuleb materjalile korrektne litsents lisada ja seejärel saab materjali avalikustada. Selleks, et materjalile korrektne litsents lisada, vajuta allpool olevale nupule.', 'This material cannot be made public, since it does not have the required CC BY 3.0 license. First you must add the correct license to the material, then you can make it public. To add the correct license to the material, click on the button below.');
call insert_translation('LICENSE_ADD', 'Lisa litsents', 'Add license');

SET foreign_key_checks = 1;