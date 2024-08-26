ALTER TABLE teachers
    DROP COLUMN search_vector;
ALTER TABLE teachers
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', first_name || ' ' || last_name || ' ' || acronym)) STORED;
CREATE INDEX teachers_search_vector_de_idx ON teachers USING gin (search_vector);

ALTER TABLE rooms
    DROP COLUMN search_vector;
ALTER TABLE rooms
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', name || ' ' || building_name)) STORED;
CREATE INDEX rooms_search_vector_de_idx ON rooms USING gin (search_vector);

ALTER TABLE students
    DROP COLUMN search_vector;
ALTER TABLE students
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', first_name || ' ' || last_name)) STORED;
CREATE INDEX students_search_vector_de_idx ON students USING gin (search_vector);

ALTER TABLE student_groups
    DROP COLUMN search_vector;
ALTER TABLE student_groups
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', name)) STORED;
CREATE INDEX student_groups_search_vector_de_idx ON student_groups USING gin (search_vector);

ALTER TABLE subjects
    DROP COLUMN search_vector;
ALTER TABLE subjects
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', name)) STORED;
CREATE INDEX subjects_search_vector_de_idx ON subjects USING gin (search_vector);

ALTER TABLE tags
    DROP COLUMN search_vector;
ALTER TABLE tags
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', name)) STORED;
CREATE INDEX tags_search_vector_de_idx ON tags USING gin (search_vector);

ALTER TABLE grades
    DROP COLUMN search_vector;
ALTER TABLE grades
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (to_tsvector('german', name)) STORED;
CREATE INDEX grades_search_vector_de_idx ON grades USING gin (search_vector);
