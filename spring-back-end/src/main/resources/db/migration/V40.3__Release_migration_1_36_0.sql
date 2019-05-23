SET foreign_key_checks = 0;

call insert_translation('LOG_MOMENT', 'Seisuga', 'Saved on');
call insert_translation('LOG_HISTORY', 'Ajalugu', 'History');
call insert_translation('LOG_CHOOSE', 'Vali versioon', 'Choose version');
call insert_translation('LOG_AUTO_SAVE', 'automaatselt salvestatud', 'autosaved');
call insert_translation('LOG_AUTO_MANUAL', 'manuaalselt salvestatud', 'manually saved');
call insert_translation('PORTFOLIO_SAVE_FAILED', 'Kogumiku salvestamine ebaõnnestus. Palun pöörduge <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.', 'Portfolio save failed. For further assistance contact customer support <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.');
call insert_translation('PORTFOLIO_AUTOSAVE_FAILED', 'Muudatuste salvestamine ebaõnnestus. Palun pöörduge <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.', 'Portfolio save failed. For further assistance contact customer support <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.');
call insert_translation('PORTFOLIO_SHOW_HISTORY', 'Kogumiku ajalugu', 'Portfolio history');
call insert_translation('LOG_GIVEUP', 'LOOBUN', 'CANCEL');
call insert_translation('LOG_CONFIRM', 'KINNITAN', 'CONFIRM');
call insert_translation('LOG_VERSION_STAY', 'Määra <strong>${createdAtDate} kell ${createdAtTime}</strong> kasutaja <strong>${creator}</strong> poolt
salvestatud kogumik praeguseks kogumiku versiooniks?',
    'Set the portfolio saved on <strong>${createdAtDate} at ${createdAtTime}</strong> by <strong>${creator}</strong> as the current version of the portfolio?');
call insert_translation('LOG_ARE_YOU_SURE', 'Kas oled kindel?', 'Are you sure?');

UPDATE Translation SET translation = 'Et saaksid E-koolikotti kasutada, on meil vaja sinu isikuandmeid. Palun loe alltoodud teavet isikuandmete töötlemise kohta. Nõustumisel kinnitad, et oled infoga tutvunud.' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' AND translationGroup = 1;
UPDATE Translation SET translation = 'Et saaksid E-koolikotti kasutada, on meil vaja sinu isikuandmeid. Palun loe alltoodud teavet isikuandmete töötlemise kohta. Nõustumisel kinnitad, et oled infoga tutvunud.' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' AND translationGroup = 2;


SET foreign_key_checks = 1;
