SET foreign_key_checks = 0;

call insert_translation('CONTINUE', 'Jätka', 'Continue');
call insert_translation('UNABLE_TO_MAKE_PUBLIC', 'Materjalil puuduvad vajalikud litsentsid kogumiku avalikuks tegemiseks', 'Material does not have valid licenses to make portfolio public');
call insert_translation('UNABLE_TO_ADD_MATERIAL', 'Antud litsentsiga materjali ei saa kogumikku lisada', 'Material with this license can not be added to portfolio');
call insert_translation('UNABLE_TO_SET_PRIVACY_LEVEL', 'Kogumikule ei saa antud privaatsustaset määrata, sest see sisaldab õppematerjali, millel puudub litsents CC BY-SA 3.0', 'Unable to set this privacy level to portfolio because it contains material that is not licensed under CC BY-SA 3.0');
call insert_translation('NOT_MIGRATED_PORTFOLIOS_DIALOG_HEADER', 'Osasid kogumikke ei õnnestunud migreerida', 'Some portfolios were unable to migrate');
call insert_translation('NOT_MIGRATED_PORTFOLIOS_DIALOG_TEXT', 'Kõiki kogumikke ei õnnestunud migreerida litsentsile CC BY-SA 3.0, sest need sisaldavad migreerimiseks sobimatu litsentsiga materjale, mis on teise autori poolt loodud.<br>Need kogumikud on muudetud privaatseks ning neid saab avalikustada alles siis, kui kogumik ei sisalda enam teise autori poolt loodud materjale, millel puuduvad avalikustamist võimaldavad litsentsid.<br><br>Vastavad materjalid on kogumikus tähistatud.',
                                                                'Some portfolios were unable to migrate to license CC BY-SA 3.0 because they contain materials by other creators and invalid licenses for migration.<br>These portfolios have been set to private and they can not be published until they do not contain materials with invalid licenses.<br><br>Named materials are labeled in portfolio.');

SET foreign_key_checks = 1;