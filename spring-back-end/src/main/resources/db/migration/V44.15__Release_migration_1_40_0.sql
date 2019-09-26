SET foreign_key_checks = 0;

call update_translations('TERMS_OF_LICENSE', 'litsentsi tingimustega.', 'license', 'litsentsi tingimustega. (RU)');
call update_translations('UNABLE_TO_MAKE_PUBLIC', 'Materjalil puudub kogumiku avalikustamiseks sobiv litsents.', 'Material does not have valid license to publish portfolio.', 'Materjalil puudub kogumiku avalikustamiseks sobiv litsents. (RU)');
call update_translations('UNABLE_TO_ADD_MATERIAL', 'Materjali ei saa kogumikku lisada, puudub sobiv litsents.', 'Unable to add material to portfolio - material does not have valid license.', 'Materjali ei saa kogumikku lisada, puudub sobiv litsents. (RU)');
call update_translations('UNABLE_TO_SET_PRIVACY_LEVEL', 'Kogumikku ei saa avalikustada, sest see sisaldab õppematerjali, mis ei luba litsentsi CC BY-SA 3.0 kohaldamist.', 'Portfolio can not be published because it contains material that does not allow applying CC BY-SA 3.0 license.', 'Kogumikku ei saa avalikustada, sest see sisaldab õppematerjali, mis ei luba litsentsi CC BY-SA 3.0 kohaldamist. (RU)');

call insert_translation('ATTENTION', 'Tähelepanu!', 'Attention!');
call insert_translation('PORTFOLIOS_SET_TO_PRIVATE_TEXT', 'Osad Sinu kogumikud on muudetud privaatseks, sest need sisaldavad teiste autorite materjale, mille litsents või autoriõigused ei luba neid avalikes kogumikes kuvada.<br>Neid kogumikke saad avalikustada alles siis, kui neis olevad materjalid omavad litsentsi CC BY-SA 3.0 (mida saab lisada materjali omanik) või kui oled kokkusobimatud materjalid oma kogumikust eemaldanud.',
                                                          'Some of your portfolios have been set to private because they contain materials by other creators which have invalid licenses for publishing.<br>These portfolios can not be published before these materials have CC BY-SA 3.0 license (which can be set by the owner of the material) or the materials are removed from portfolio.');

SET foreign_key_checks = 1;