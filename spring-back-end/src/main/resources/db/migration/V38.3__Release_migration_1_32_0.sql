SET foreign_key_checks = 0;

call insert_translation('USER_PROFILE_HEADER', 'Minu andmed', 'My profile');
call insert_translation('USER_PROFILE_ROLE', 'Roll', 'Role');
call insert_translation('PROFILE_STUDENT', 'Õpilane', 'Student');
call insert_translation('PROFILE_TEACHER', 'Õpetaja', 'Teacher');
call insert_translation('PROFILE_PARENT', 'Lapsevanem', 'Parent');
call insert_translation('PROFILE_OTHER', 'Muu', 'Other');
call insert_translation('USER_PROFILE_INTERESTS', 'Sulle huvi pakkuvad teemad', 'Topics that interest you');
call insert_translation('USER_PROFILE_CHOOSE_SCHOOLS', 'Vali õppeasutus', 'Choose school');
call insert_translation('USER_PROFILE_SEARCH_SCHOOLS', 'Otsi õppeasutust', 'Search schools');
call insert_translation('USER_PROFILE_CHOOSE_AREA', 'Vali maakond', 'Choose county');
call insert_translation('USER_PROFILE_UPDATED', 'Andndmed uuendatud', 'User profile updated');
call insert_translation('USER_PROFILE_UPDATE_FAILED', 'Andmete uuendamine ebaõnnestus', 'User profile update failed');
call insert_translation('USER_PROFILE_ADD_INSTITUTION', 'Lisa õppeasutus', 'Add school');
call insert_translation('USER_PROFILE_REMOVE_INSTITUTION', 'Eemalda õppeasutus', 'Remove school');
call insert_translation('USER_PROFILE_ADD_INTEREST', 'Lisa teema', 'Add topic');
call insert_translation('USER_PROFILE_REMOVE_INTEREST', 'Eemalda teema', 'Remove topic');
call insert_translation('USER_PROFILE_INSTITUTION', 'Õppeasutus', 'School');

SET foreign_key_checks = 1;