CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) DEFAULT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    conta_nao_expirada BOOLEAN DEFAULT TRUE,
    conta_nao_travada BOOLEAN DEFAULT TRUE,
    credencial_nao_expirado BOOLEAN DEFAULT TRUE,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS permissoes (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios_permissoes (
    id_usuario BIGINT NOT NULL,
    id_permissao BIGINT NOT NULL,
    PRIMARY KEY ( id_usuario, id_permissao ),
    CONSTRAINT fk_usuario_permissao FOREIGN KEY ( id_usuario ) REFERENCES usuarios ( id ) ON DELETE CASCADE,
    CONSTRAINT fk_permissao_permissao FOREIGN KEY ( id_permissao ) REFERENCES permissoes ( id ) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comicinfo (
    id VARCHAR(36) PRIMARY KEY,
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
    atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_comicinfo_series ON comicinfo ( series );
CREATE INDEX IF NOT EXISTS idx_comicinfo_title ON comicinfo ( title );

CREATE TABLE IF NOT EXISTS opf (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(900) DEFAULT NULL,
    creator VARCHAR(900) DEFAULT NULL,
    contributor VARCHAR(900) DEFAULT NULL,
    publisher VARCHAR(300) DEFAULT NULL,
    date_published VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    subjects VARCHAR(900) DEFAULT NULL,
    language VARCHAR(10) DEFAULT NULL,
    identifiers VARCHAR(900) DEFAULT NULL,
    series VARCHAR(900) DEFAULT NULL,
    series_index VARCHAR(50) DEFAULT NULL,
    rights VARCHAR(500) DEFAULT NULL,
    relation VARCHAR(500) DEFAULT NULL,
    atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_opf_title ON opf ( title );
CREATE INDEX IF NOT EXISTS idx_opf_creator ON opf ( creator );
CREATE INDEX IF NOT EXISTS idx_opf_series ON opf ( series );

CREATE TABLE IF NOT EXISTS data (
    id VARCHAR(36) PRIMARY KEY,
    comicinfo_id VARCHAR(36) DEFAULT NULL,
    opf_id VARCHAR(36) DEFAULT NULL,
    tipo VARCHAR(50) DEFAULT NULL,
    file_name VARCHAR(255) DEFAULT NULL,
    file_content TEXT DEFAULT NULL,
    atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_data_comicinfo FOREIGN KEY ( comicinfo_id ) REFERENCES comicinfo ( id ) ON DELETE CASCADE,
    CONSTRAINT fk_data_opf FOREIGN KEY ( opf_id ) REFERENCES opf ( id ) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_data_comicinfo ON data ( comicinfo_id );
CREATE INDEX IF NOT EXISTS idx_data_opf ON data ( opf_id );

CREATE TABLE IF NOT EXISTS manga (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(500) DEFAULT NULL,
    file_name VARCHAR(500) DEFAULT NULL,
    extension VARCHAR(10) DEFAULT NULL,
    file_date TIMESTAMP DEFAULT NULL,
    comicinfo_id VARCHAR(36) DEFAULT NULL,
    atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_manga_comicinfo FOREIGN KEY ( comicinfo_id ) REFERENCES comicinfo ( id ) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_manga_comicinfo ON manga ( comicinfo_id );
CREATE INDEX IF NOT EXISTS idx_manga_nome ON manga ( nome );
CREATE INDEX IF NOT EXISTS idx_manga_file_name ON manga ( file_name );

CREATE TABLE IF NOT EXISTS book (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(500) DEFAULT NULL,
    file_name VARCHAR(500) DEFAULT NULL,
    extension VARCHAR(10) DEFAULT NULL,
    file_date TIMESTAMP DEFAULT NULL,
    opf_id VARCHAR(36) DEFAULT NULL,
    atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_opf FOREIGN KEY ( opf_id ) REFERENCES opf ( id ) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_book_opf ON book ( opf_id );
CREATE INDEX IF NOT EXISTS idx_book_nome ON book ( nome );
CREATE INDEX IF NOT EXISTS idx_book_file_name ON book ( file_name );

-- Inserção de dados padrão
INSERT INTO permissoes (id, descricao) VALUES
(1, 'ADMIN'),
(2, 'MANAGER'),
(3, 'COMMON_USER')
ON CONFLICT (id) DO UPDATE SET descricao = EXCLUDED.descricao;

INSERT INTO usuarios (id, username, nome, password, conta_nao_expirada, conta_nao_travada, credencial_nao_expirado, ativo) VALUES
(1, 'admin', 'Administrator', '$2a$10$BipJ1Wv3lo7QBybP/Lo6n.Fge32f17DEKjNvciEuOBJfSYvJrB2ze', TRUE, TRUE, TRUE, TRUE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome;

INSERT INTO usuarios_permissoes (id_usuario, id_permissao) VALUES
(1, 1),
(1, 2),
(1, 3)
ON CONFLICT (id_usuario, id_permissao) DO NOTHING;
