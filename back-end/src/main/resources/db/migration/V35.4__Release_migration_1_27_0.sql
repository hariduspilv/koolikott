SET foreign_key_checks = 0;

update Translation set translation = 'Videojuhendid' where translationKey = 'USER_GUIDE_PAGE' and translationGroup = 1;
update Translation set translation = 'Videojuhendid' where translationKey = 'USER_GUIDE_PAGE' and translationGroup = 2;
update Translation set translation = 'Video guides' where translationKey = 'USER_GUIDE_PAGE' and translationGroup = 3;
update Translation set translation = 'Videojuhendid' where translationKey = 'USER_MANUAL_TAB' and translationGroup = 1;
update Translation set translation = 'Videojuhendid' where translationKey = 'USER_MANUAL_TAB' and translationGroup = 2;
update Translation set translation = 'Video guides' where translationKey = 'USER_MANUAL_TAB' and translationGroup = 3;
update Translation set translation = 'Videojuhendid' where translationKey = 'USER_MANUALS_HEADING' and translationGroup = 1;
update Translation set translation = 'Videojuhendid' where translationKey = 'USER_MANUALS_HEADING' and translationGroup = 2;
update Translation set translation = 'Video guides' where translationKey = 'USER_MANUALS_HEADING' and translationGroup = 3;

SET foreign_key_checks = 1;