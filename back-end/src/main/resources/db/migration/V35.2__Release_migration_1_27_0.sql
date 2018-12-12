SET foreign_key_checks = 0;

call insert_translation('ABOUT_TAB', 'e-Koolikotist', 'About');
call insert_translation('KKK_TAB', 'KKK', 'FAQ');
call insert_translation('FAQ_PAGE', 'Korduma kippuvad küsimused', 'Frequently asked questions');
call insert_translation('ADD_FAQ', 'Lisa sisuplokk', 'Add FAQ');
call insert_translation('FAQ_QUESTION', 'Küsimus', 'Question');
call insert_translation('FAQ_QUESTION_IS_MANDATORY', 'Küsimus on kohustuslik', 'Question is required');
call insert_translation('FAQ_ANSWER', 'Vastus', 'Answer');
call insert_translation('FAQ_ANSWER_IS_MANDATORY', 'Vastus on kohustuslik', 'Answer is required');
call insert_translation('BUTTON_ADD_FAQ', 'Lisa', 'Add');
call insert_translation('FAQ_EDIT', 'Muuda sisuplokki', 'Edit FAQ');

SET foreign_key_checks = 1;