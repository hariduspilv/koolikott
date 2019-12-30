SET foreign_key_checks = 0;

call update_translations('LICENSE_AGREEMENT_DIALOG_HEADER', '<strong>Nõusolek minu materjalide ja kogumike litsentsi muutmiseks</strong>',
                          '<strong>Consent to change the licenses for my materials and portfolios</strong>',
                          '<strong>Nõusolek minu materjalide ja kogumike litsentsi muutmiseks</strong>');

call update_translations('LICENSE_AGREEMENT_DIALOG_TEXT', 'Kogumike avalikustamiseks ühtlustame E-koolikotis kasutatavat litsentsi, milleks on <strong>CC BY-SA 3.0.</strong> Litsentsi kokkuvõte asub <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/" target="_blank">SIIN</a> ja täistekst <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/legalcode" target="_blank">SIIN</a>.<br><br><strong>Nõustumisel muudame automaatselt kõigi Sinu materjalide ja kogumike litsentsiks CC BY-SA 3.0.</strong><br><strong>Mittenõustumisel</strong> muutuvad kõik Sinu materjalid ja kogumikud, millel ei ole CC BY-SA 3.0 litsentsi, privaatseks (st mitte avalikuks). Privaatsetele materjalidele ja kogumikele saad uue litsentsi hiljem ükshaaval käsitsi määrata ja need taas avalikuks muuta.<br><br><strong>NB! Litsents ei laiene sisule, mis asub E-koolikotist väljaspool (nt lingitud sisu). Litsents kehtib otse E-koolikotti laaditud või kirjutatud (sh kopeeritud) sisule.</strong>',
    'To publish portfolios we are harmonizing licences in the E-schoolbag. The license is <strong>CC BY-SA 3.0.</strong> Summary of license can be found <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/" target="_blank">HERE</a> and the full version of the license is <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/legalcode" target="_blank">HERE</a>.<br><br><strong>If you agree, we will automatically change all your materials and portfolios license to CC BY-SA 3.0.</strong><br><strong>If you do not agree</strong>, all your materials which do not have CC BY-SA 3.0 license, will be changed to private (it means not published). Later you can publish the materials and the collection if you are adding licenses for materials and collections one by one.<br><br><strong>NB! The license doesn´t extend to the content which is placed  outside of E-schoolbag (fe. linked content). The license is valid for the content which is loaded or written (also copied) directly to the E-schoolbag.</strong>',
    'Kogumike avalikustamiseks ühtlustame E-koolikotis kasutatavat litsentsi, milleks on <strong>CC BY-SA 3.0.</strong> Litsentsi kokkuvõte asub <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/" target="_blank">SIIN</a> ja täistekst <a href="https://creativecommons.org/licenses/by-sa/3.0/ee/legalcode" target="_blank">SIIN</a>.<br><br><strong>Nõustumisel muudame automaatselt kõigi Sinu materjalide ja kogumike litsentsiks CC BY-SA 3.0.</strong><br><strong>Mittenõustumisel</strong> muutuvad kõik Sinu materjalid ja kogumikud, millel ei ole CC BY-SA 3.0 litsentsi, privaatseks (st mitte avalikuks). Privaatsetele materjalidele ja kogumikele saad uue litsentsi hiljem ükshaaval käsitsi määrata ja need taas avalikuks muuta.<br><br><strong>NB! Litsents ei laiene sisule, mis asub E-koolikotist väljaspool (nt lingitud sisu). Litsents kehtib otse E-koolikotti laaditud või kirjutatud (sh kopeeritud) sisule.</strong>');

call update_translations('PORTFOLIOS_SET_TO_PRIVATE_TEXT', 'Osad Sinu kogumikud (loetelu allpool) on muudetud privaatseks, sest need sisaldavad teiste autorite materjale, mille litsents või autoriõigused ei luba neid kogumikus avalikustada.<br><br>Selliseid kogumikke saad avalikustada alles siis, kui neis olevad materjalid on litsentsiga CC BY-SA 3.0 (mida saab lisada materjali omanik) või kui eemaldad sobimatu litsentsiga materjalid oma kogumikust.',
                          'Some of the portfolios (the list is below) have been change to private because these portfolios include materials from other authors and the material’s license or copyrights do not allow you to publish the portfolios.<br><br>You can publish these portfolios after you have added the license of CC BY-SA 3.0 for all included materials (licenses can added by the material’s owner) or you just remove all the materials without license from the portfolio.',
                          'Osad Sinu kogumikud (loetelu allpool) on muudetud privaatseks, sest need sisaldavad teiste autorite materjale, mille litsents või autoriõigused ei luba neid kogumikus avalikustada.<br><br>Selliseid kogumikke saad avalikustada alles siis, kui neis olevad materjalid on litsentsiga CC BY-SA 3.0 (mida saab lisada materjali omanik) või kui eemaldad sobimatu litsentsiga materjalid oma kogumikust.');

call update_translations('MATERIAL_LICENSE_WARNING', 'Ühtlustame E-koolikotis kasutatavat litsentsi. Kuna materjalil puudub <strong>CC BY-SA 3.0 litsents</strong> (või sellega kokku sobiv CC BY 3.0 litsents), ei saa materjali hetkel avalikustada.<br><br>Materjali avalikustamine on võimalik vaid juhul, kui nõustud nimetatud litsentsiga (litsents ei kehti lingitud sisule, mis asub E-koolikotist väljas). Selleks ava materjali muutmise vaade ja kinnita litsentsiga nõusolek.',
                          'Harmonizing license in the E-schoolbag. We can´t publish your material because your material doesn´t have <strong>CC BY-SA 3.0 license</strong> (or a compatible CC BY 3.0 license).<br><br>Publishing the material is possible if you agree with mentioned license (the license doesn´t apply for the materials which are placed outside the E-Schoolbag). Please open the file in the view for modifications and give your accept for the license.',
                          'Ühtlustame E-koolikotis kasutatavat litsentsi. Kuna materjalil puudub <strong>CC BY-SA 3.0 litsents</strong> (või sellega kokku sobiv CC BY 3.0 litsents), ei saa materjali hetkel avalikustada.<br><br>Materjali avalikustamine on võimalik vaid juhul, kui nõustud nimetatud litsentsiga (litsents ei kehti lingitud sisule, mis asub E-koolikotist väljas). Selleks ava materjali muutmise vaade ja kinnita litsentsiga nõusolek.');

call update_translations('REPORT_IMPROPER', 'Teatan probleemist õppevaras', 'Report content', 'Teatan probleemist õppevaras (RU)');

call update_translations('ARE_YOU_SURE_CANCEL', 'Kas oled kindel, et tahad katkestada?', 'Are you sure you want to cancel?', 'Kas oled kindel, et tahad katkestada? (RU)');

call update_translations('REPORT_IMPROPER_TITLE', 'Teatan probleemist õppevaras', 'Report content', 'Teatan probleemist õppevaras (RU)');

call insert_translation('PERSONAL_DATA_TAB', 'Isikuandmete töötlemine', 'GDPR');

call insert_translation('ADD_TERM', 'Lisa tingimus', 'Add term');

call update_translations('AGREEMENT_DIALOG_HEADER', 'Tingimustega nõustumine', 'Agreement to terms required', 'Tingimustega nõustumine (RU)');

call update_translations('AGREEMENT_DIALOG_TEXT', 'Näed käesolevat teadet, kuna registreerud esmakordselt e-koolikoti kasutajaks või on juriidilised tingimused pärast Sinu viimast sisselogimist uuenenud. Palun loe tingimusi põhjalikult ja kinnita oma nõusolek.', 'You are seeing this notification as a first time user or because some terms have changed since your last login. Please read the following terms and conditions carefully and mark your acceptance below.', 'Näed käesolevat teadet, kuna registreerud esmakordselt e-koolikoti kasutajaks või on juriidilised tingimused pärast Sinu viimast sisselogimist uuenenud. Palun loe tingimusi põhjalikult ja kinnita oma nõusolek. (RU)');

call update_translations('AGREE_TO', 'Olen tutvunud ja nõustun E-koolikoti', 'I have read and agree to the', 'Olen tutvunud ja nõustun (RU)');

call update_translations('WITH_TERMS', 'kasutustingimustega', 'Terms of Use', 'kasutustingimustega (RU)');

call update_translations('AGREEMENT_REQUIRED', ' *','of E-schoolbag *', ' * (RU)');

call insert_translation('AGREE_TO_GDPR', 'Olen tutvunud ja nõustun', 'I have read and agree to the processing of my personal data according to the');

call insert_translation('GDPR_TERMS', 'isikuandmete töötlemise tingimustega E-koolikotis', 'GDPR Processing Conditions');

SET foreign_key_checks = 1;
