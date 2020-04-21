SET foreign_key_checks = 0;

call update_translations('PUBLICATION_TOOLTIP', 'Jagamiseks peavad kõik õppematerjalid kogumikus omama litsentsi CC BY-SA 3.0', 'Only portfolios containing the license CC BY-SA 3.0 can be shared', 'Jagamiseks peavad kõik õppematerjalid kogumikus omama litsentsi CC BY-SA 3.0 (RU)');
call update_translations('PUBLICATION_TOOLTIP_MOBILE', 'Jagamiseks peavad kõik õppematerjalid <br> kogumikus omama litsentsi CC BY-SA 3.0', 'Only portfolios containing the license <br> CC BY-SA 3.0 can be shared', 'Jagamiseks peavad kõik õppematerjalid <br> kogumikus omama litsentsi CC BY-SA 3.0 (RU)');

SET foreign_key_checks = 1;