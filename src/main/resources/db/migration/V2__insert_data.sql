CREATE TABLE task_activities (
                                 id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 task_id      BIGINT NOT NULL,
                                 action       VARCHAR(100) NOT NULL,
                                 old_value    VARCHAR(255),
                                 new_value    VARCHAR(255),
                                 performed_by VARCHAR(50) NOT NULL,
                                 performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (task_id) REFERENCES tasks(id)
);

INSERT INTO users (username, email, password, created_at) VALUES
                                                              ('admin', 'endrihysa4@gmail.com', '$2a$10$GNKMgtJLLhhwNAgn5cDNmuykoO7nOjvYo4LTTrUu0zhN0hNxNG7Oi', CURRENT_TIMESTAMP),
                                                              ('endri', 'endrihysa04@gmail.com', '$2a$10$A0Sl/jkuirh6erM22r4PDOpoRvbShB9GgD9L/qe79MeFsQ3I9X9M6', CURRENT_TIMESTAMP),
                                                              ('iva', 'endrihysa0@gmail.com', '$2a$10$ybrXJeJu8ZMcueYf/Z.ThOlPe8PhX/o/sQS6p0bsc/OvTREnGaTCi', CURRENT_TIMESTAMP);

INSERT INTO projects (name, description, owner_id, created_at) VALUES
                                                                   ('Projekti 1', 'Projekti i pare', 1, CURRENT_TIMESTAMP),
                                                                   ('Projekti 2', 'Projekti i dyte', 2, CURRENT_TIMESTAMP),
                                                                   ('Projekti 3', 'Projekti i trete', 3, CURRENT_TIMESTAMP);

INSERT INTO tasks (title, description, status, priority, due_date, project_id, assignee_id, created_at) VALUES
                                                                                                            ('Task 1', 'Pershkrimi i task 1', 'TODO', 'HIGH', '2026-07-01', 1, 1, CURRENT_TIMESTAMP),
                                                                                                            ('Task 2', 'Pershkrimi i task 2', 'IN_PROGRESS', 'MEDIUM', '2026-07-05', 1, 2, CURRENT_TIMESTAMP),
                                                                                                            ('Task 3', 'Pershkrimi i task 3', 'COMPLETED', 'LOW', '2026-06-15', 2, 3, CURRENT_TIMESTAMP),
                                                                                                            ('Task 4', 'Pershkrimi i task 4', 'TODO', 'HIGH', '2026-07-10', 3, 1, CURRENT_TIMESTAMP);