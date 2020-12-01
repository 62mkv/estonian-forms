INSERT INTO FORM_TYPE_COMBINATIONS(id, eki_representation)
VALUES (1,' SgN'),(2,'SgG'),('3','SgP');

INSERT INTO PARTS_OF_SPEECH(id, part_of_speech, eki_codes)
VALUES (1, 'Noun', 'GS');

INSERT INTO REPRESENTATIONS(id, representation) VALUES (1, 'ema');

/* single paradigm */
INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, inflection_type)
VALUES (1, 1000, 1, 1, '22');

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (1, 1, 1, 1);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (2, 1, 2, 1);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (3, 1, 3, 1);

INSERT INTO REPRESENTATIONS(id, representation) VALUES (2, 'koer');
INSERT INTO REPRESENTATIONS(id, representation) VALUES (3, 'koera');
INSERT INTO REPRESENTATIONS(id, representation) VALUES (4, 'koerat');
INSERT INTO REPRESENTATIONS(id, representation) VALUES (5, 'koeru'); /* this is fake! */

/* two paradigms */
INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, inflection_type)
VALUES (2, 2000, 2, 1, '22');

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (4, 2, 1, 2);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (5, 2, 2, 3);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (6, 2, 3, 4);

INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, inflection_type)
VALUES (3, 2000, 2, 1, '23');

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (7, 3, 1, 2);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (8, 3, 2, 3);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (9, 3, 3, 5);

/* homonyms */

INSERT INTO REPRESENTATIONS(id, representation) VALUES (6, 'minema');
INSERT INTO REPRESENTATIONS(id, representation) VALUES (7, 'minna');

INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, inflection_type)
VALUES (4, 3000, 6, 1, '31');

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (10, 4, 1, 6);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (11, 4, 2, 7);

INSERT INTO EKILEX_PARADIGMS(id, word_id, word_representation_id, part_of_speech_id, inflection_type)
VALUES (5, 3001, 6, 1, '31');

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (12, 5, 1, 6);

INSERT INTO EKILEX_FORMS(id, paradigm_id, form_type_combination_id, word_representation_id)
VALUES (13, 5, 2, 7);

