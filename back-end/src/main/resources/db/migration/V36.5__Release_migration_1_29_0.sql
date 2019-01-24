SET foreign_key_checks = 0;

call update_translations('EMAIL_VALIDATION_DIALOG_TEXT', 'Sinu e-posti aadressile <strong>${email}</strong> on saadetud 4-kohaline kinnituskood. Kui sa kinnituskoodi oma postkastist ei leia, kontrolli ka rämpsposti kausta.<br><br>Trüki kinnituskood siia:', 'We have sent a 4 digit code to your e-mail address <strong>${email}</strong>. If you can''t find the code be sure to check your spam folder.<br><br>Insert code below:', 'Sinu e-posti aadressile <strong>${email}</strong> on saadetud 4-kohaline kinnituskood. Kui sa kinnituskoodi oma postkastist ei leia, kontrolli ka rämpsposti kausta.<br><br>Trüki kinnituskood siia: (RU)');

SET foreign_key_checks = 1;
