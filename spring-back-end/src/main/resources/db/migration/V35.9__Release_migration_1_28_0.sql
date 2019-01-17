SET foreign_key_checks = 0;

DELIMITER //
create procedure update_translations(IN tr_key varchar(255), IN tr_val_est text, IN tr_val_eng text, IN tr_val_rus text)
  BEGIN

    update Translation set translation = tr_val_est where translationKey = tr_key and translationGroup = 1;
    update Translation set translation = tr_val_eng where translationKey = tr_key and translationGroup = 3;
    update Translation set translation = tr_val_rus where translationKey = tr_key and translationGroup = 2;
  END //
DELIMITER;

SET foreign_key_checks = 1;