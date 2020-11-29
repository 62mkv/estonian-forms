INSERT INTO FORM_TYPE_COMBINATIONS(id, eki_representation)
VALUES (1,' SgN'),(2,'SgG'),('3','SgP');

INSERT INTO REPRESENTATIONS(id, representation) VALUES (1, 'ema');

INSERT INTO PARTS_OF_SPEECH(id, part_of_speech, eki_codes)
VALUES (1, 'Noun', 'GS');

INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, secondary)
VALUES (1, 1000, 1, 1, false);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (1, 1, 1, 1);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (2, 1, 2, 1);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (3, 1, 3, 1);
