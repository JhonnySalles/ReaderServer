CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) DEFAULT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    conta_nao_expirada BIT(1) DEFAULT b'1',
    conta_nao_travada BIT(1) DEFAULT b'1',
    credencial_nao_expirado BIT(1) DEFAULT b'1',
    ativo BIT(1) DEFAULT b'1',
    PRIMARY KEY ( id ),
    UNIQUE KEY uk_usuario_username ( username )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS permissoes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(255) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY uk_permissao_descricao ( descricao )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios_permissoes (
    id_usuario BIGINT NOT NULL,
    id_permissao BIGINT NOT NULL,
    PRIMARY KEY ( id_usuario, id_permissao ),
    CONSTRAINT fk_usuario_permissao FOREIGN KEY ( id_usuario ) REFERENCES usuarios ( id ) ON DELETE CASCADE,
    CONSTRAINT fk_permissao_permissao FOREIGN KEY ( id_permissao ) REFERENCES permissoes ( id ) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comicinfo (
    id VARCHAR(36) NOT NULL,
    comic VARCHAR(250) DEFAULT NULL,
    idMal BIGINT DEFAULT NULL,
    series VARCHAR(900) DEFAULT NULL,
    title VARCHAR(900) DEFAULT NULL,
    publisher VARCHAR(300) DEFAULT NULL,
    genre VARCHAR(900) DEFAULT NULL,
    imprint VARCHAR(300) DEFAULT NULL,
    seriesGroup VARCHAR(900) DEFAULT NULL,
    storyArc VARCHAR(900) DEFAULT NULL,
    maturityRating VARCHAR(100) DEFAULT NULL,
    alternateSeries VARCHAR(900) DEFAULT NULL,
    language VARCHAR(3) DEFAULT NULL,
    PRIMARY KEY ( id ),
    KEY idx_comicinfo_series ( series(255) ),
    KEY idx_comicinfo_title ( title(255) )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserção de dados padrão
INSERT INTO permissoes (id, descricao) VALUES
(1, 'ADMIN'),
(2, 'MANAGER'),
(3, 'COMMON_USER')
ON DUPLICATE KEY UPDATE descricao = VALUES(descricao);

INSERT INTO usuarios (id, username, nome, password, conta_nao_expirada, conta_nao_travada, credencial_nao_expirado, ativo) VALUES
(1, 'admin', 'Administrator', '$2a$10$BipJ1Wv3lo7QBybP/Lo6n.Fge32f17DEKjNvciEuOBJfSYvJrB2ze', b'1', b'1', b'1', b'1')
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

INSERT INTO usuarios_permissoes (id_usuario, id_permissao) VALUES
(1, 1),
(1, 2),
(1, 3)
ON DUPLICATE KEY UPDATE id_usuario = id_usuario;
