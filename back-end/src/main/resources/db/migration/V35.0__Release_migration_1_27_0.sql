SET foreign_key_checks = 0;

DELIMITER //
create procedure insert_translation_all(IN tr_key varchar(255), IN tr_val_est text, IN tr_val_eng text, IN tr_val_rus text)
  BEGIN

    INSERT INTO Translation VALUES (1, tr_key, tr_val_est);
    INSERT INTO Translation VALUES (3, tr_key, tr_val_eng);
    INSERT INTO Translation VALUES (2, tr_key, tr_val_rus);
  END //

  create procedure insert_translation(IN tr_key varchar(255), IN tr_val_est text, IN tr_val_eng text)
  BEGIN
    call insert_translation_all(tr_key, tr_val_est, tr_val_eng, concat(tr_val_est, ' (RU)'));
  END //
DELIMITER;

SET foreign_key_checks = 1;