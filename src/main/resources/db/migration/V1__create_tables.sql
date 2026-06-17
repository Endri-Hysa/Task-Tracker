CREATE TABLE users (
                       id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username   VARCHAR(50)  NOT NULL,
                       email      VARCHAR(100) NOT NULL,
                       password   VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projects (
                          id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name        VARCHAR(50)  NOT NULL,
                          description VARCHAR(500),
                          owner_id    BIGINT NOT NULL,
                          created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE tasks (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title       VARCHAR(100) NOT NULL,
                       description VARCHAR(1000),
                       status      VARCHAR(20)  NOT NULL,
                       priority    VARCHAR(10)  NOT NULL,
                       due_date    DATE,
                       project_id  BIGINT NOT NULL,
                       assignee_id BIGINT,
                       created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (project_id) REFERENCES projects(id),
                       FOREIGN KEY (assignee_id) REFERENCES users(id)
);