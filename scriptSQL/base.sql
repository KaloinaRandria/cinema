-- =========================
-- Optionnel mais recommandé pour UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- ENUMS
-- =========================
DO $$ BEGIN
    CREATE TYPE type_salle AS ENUM ('STANDARD', 'VIP', 'IMAX', 'THREED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE type_siege AS ENUM ('STANDARD', 'VIP', 'PMR');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE langue_seance AS ENUM ('VF', 'VO');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE format_seance AS ENUM ('2D', '3D');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE statut_seance AS ENUM ('PROGRAMMEE', 'ANNULEE', 'TERMINEE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE statut_reservation AS ENUM ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'EXPIREE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE statut_billet AS ENUM ('RESERVE', 'PAYE', 'ANNULE', 'UTILISE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE methode_paiement AS ENUM ('ESPECE', 'CARTE', 'MOBILE_MONEY');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE statut_paiement AS ENUM ('INITIE', 'SUCCES', 'ECHEC', 'REMBOURSE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE classification_film AS ENUM ('TP', '-12', '-16', '-18');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- =========================
-- TABLES
-- =========================

-- CINEMA
CREATE TABLE IF NOT EXISTS cinema (
                                      id_cinema      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      nom            VARCHAR(150) NOT NULL,
                                      adresse        VARCHAR(255),
                                      ville          VARCHAR(120),
                                      telephone      VARCHAR(50),
                                      email          VARCHAR(150),
                                      actif          BOOLEAN NOT NULL DEFAULT TRUE,
                                      created_at     TIMESTAMP NOT NULL DEFAULT now()
);

-- SALLE
CREATE TABLE IF NOT EXISTS salle (
                                     id_salle   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     code       VARCHAR(50) NOT NULL,
                                     nom        VARCHAR(150),
                                     type_salle type_salle NOT NULL DEFAULT 'STANDARD',
                                     capacite   INTEGER NOT NULL CHECK (capacite >= 0),
                                     id_cinema  UUID NOT NULL,
                                     CONSTRAINT fk_salle_cinema
                                         FOREIGN KEY (id_cinema) REFERENCES cinema(id_cinema)
                                             ON UPDATE CASCADE ON DELETE RESTRICT,
                                     CONSTRAINT uq_salle_code_cinema UNIQUE (id_cinema, code)
);

CREATE INDEX IF NOT EXISTS idx_salle_cinema ON salle(id_cinema);

-- SIEGE
CREATE TABLE IF NOT EXISTS siege (
                                     id_siege   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     rangee     VARCHAR(10) NOT NULL,
                                     numero     INTEGER NOT NULL CHECK (numero > 0),
                                     type_siege type_siege NOT NULL DEFAULT 'STANDARD',
                                     id_salle   UUID NOT NULL,
                                     CONSTRAINT fk_siege_salle
                                         FOREIGN KEY (id_salle) REFERENCES salle(id_salle)
                                             ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT uq_siege_position UNIQUE (id_salle, rangee, numero)
);

CREATE INDEX IF NOT EXISTS idx_siege_salle ON siege(id_salle);

-- FILM
CREATE TABLE IF NOT EXISTS film (
                                    id_film            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    titre              VARCHAR(200) NOT NULL,
                                    duree_minutes      INTEGER NOT NULL CHECK (duree_minutes > 0),
                                    genre              VARCHAR(80),
                                    classification     classification_film DEFAULT 'TP',
                                    synopsis           TEXT,
                                    realisateur        VARCHAR(150),
                                    date_sortie        DATE,
                                    affiche_url        TEXT,
                                    bande_annonce_url  TEXT,
                                    actif              BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_film_titre ON film(titre);

-- SEANCE
CREATE TABLE IF NOT EXISTS seance (
                                      id_seance   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      debut       TIMESTAMP NOT NULL,
                                      fin         TIMESTAMP NOT NULL,
                                      langue      langue_seance NOT NULL DEFAULT 'VF',
                                      format      format_seance NOT NULL DEFAULT '2D',
                                      prix_base   NUMERIC(12,2) NOT NULL CHECK (prix_base >= 0),
                                      statut      statut_seance NOT NULL DEFAULT 'PROGRAMMEE',
                                      id_film     UUID NOT NULL,
                                      id_salle    UUID NOT NULL,
                                      CONSTRAINT fk_seance_film
                                          FOREIGN KEY (id_film) REFERENCES film(id_film)
                                              ON UPDATE CASCADE ON DELETE RESTRICT,
                                      CONSTRAINT fk_seance_salle
                                          FOREIGN KEY (id_salle) REFERENCES salle(id_salle)
                                              ON UPDATE CASCADE ON DELETE RESTRICT,
                                      CONSTRAINT chk_seance_dates CHECK (fin > debut)
);

CREATE INDEX IF NOT EXISTS idx_seance_film  ON seance(id_film);
CREATE INDEX IF NOT EXISTS idx_seance_salle ON seance(id_salle);
CREATE INDEX IF NOT EXISTS idx_seance_debut ON seance(debut);

-- UTILISATEUR
CREATE TABLE IF NOT EXISTS utilisateur (
                                           id_user       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           nom           VARCHAR(120),
                                           prenom        VARCHAR(120),
                                           email         VARCHAR(180) NOT NULL,
                                           password_hash TEXT NOT NULL,
                                           telephone     VARCHAR(50),
                                           actif         BOOLEAN NOT NULL DEFAULT TRUE,
                                           created_at    TIMESTAMP NOT NULL DEFAULT now(),
                                           CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS idx_user_email ON utilisateur(email);

-- ROLE
CREATE TABLE IF NOT EXISTS role (
                                    id_role  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    code     VARCHAR(30) NOT NULL,
                                    libelle  VARCHAR(120),
                                    CONSTRAINT uq_role_code UNIQUE (code)
);

-- UTILISATEUR_ROLE (N-N)
CREATE TABLE IF NOT EXISTS utilisateur_role (
                                                id_user UUID NOT NULL,
                                                id_role UUID NOT NULL,
                                                PRIMARY KEY (id_user, id_role),
                                                CONSTRAINT fk_ur_user
                                                    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)
                                                        ON UPDATE CASCADE ON DELETE CASCADE,
                                                CONSTRAINT fk_ur_role
                                                    FOREIGN KEY (id_role) REFERENCES role(id_role)
                                                        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ur_role ON utilisateur_role(id_role);

-- RESERVATION
CREATE TABLE IF NOT EXISTS reservation (
                                           id_reservation    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           reference         VARCHAR(40) NOT NULL,
                                           date_reservation  TIMESTAMP NOT NULL DEFAULT now(),
                                           statut            statut_reservation NOT NULL DEFAULT 'EN_ATTENTE',
                                           montant_total     NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (montant_total >= 0),
                                           expire_at         TIMESTAMP,
                                           id_user           UUID NOT NULL,
                                           id_seance         UUID NOT NULL,
                                           CONSTRAINT uq_res_reference UNIQUE (reference),
                                           CONSTRAINT fk_res_user
                                               FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)
                                                   ON UPDATE CASCADE ON DELETE RESTRICT,
                                           CONSTRAINT fk_res_seance
                                               FOREIGN KEY (id_seance) REFERENCES seance(id_seance)
                                                   ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_res_user   ON reservation(id_user);
CREATE INDEX IF NOT EXISTS idx_res_seance ON reservation(id_seance);
CREATE INDEX IF NOT EXISTS idx_res_statut ON reservation(statut);

-- RESERVATION_SIEGE
CREATE TABLE IF NOT EXISTS reservation_siege (
                                                 id_res_siege    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 prix            NUMERIC(12,2) NOT NULL CHECK (prix >= 0),
                                                 id_reservation  UUID NOT NULL,
                                                 id_siege        UUID NOT NULL,
                                                 CONSTRAINT fk_rs_reservation
                                                     FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation)
                                                         ON UPDATE CASCADE ON DELETE CASCADE,
                                                 CONSTRAINT fk_rs_siege
                                                     FOREIGN KEY (id_siege) REFERENCES siege(id_siege)
                                                         ON UPDATE CASCADE ON DELETE RESTRICT,
                                                 CONSTRAINT uq_rs_res_siege UNIQUE (id_reservation, id_siege)
);

CREATE INDEX IF NOT EXISTS idx_rs_reservation ON reservation_siege(id_reservation);
CREATE INDEX IF NOT EXISTS idx_rs_siege       ON reservation_siege(id_siege);

-- BILLET
CREATE TABLE IF NOT EXISTS billet (
                                      id_billet        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      code_billet      VARCHAR(60) NOT NULL,
                                      qr_data          TEXT,
                                      statut           statut_billet NOT NULL DEFAULT 'RESERVE',
                                      created_at       TIMESTAMP NOT NULL DEFAULT now(),
                                      id_res_siege     UUID NOT NULL,
                                      CONSTRAINT uq_billet_code UNIQUE (code_billet),
                                      CONSTRAINT uq_billet_res_siege UNIQUE (id_res_siege),
                                      CONSTRAINT fk_billet_res_siege
                                          FOREIGN KEY (id_res_siege) REFERENCES reservation_siege(id_res_siege)
                                              ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_billet_statut ON billet(statut);

-- PAIEMENT
CREATE TABLE IF NOT EXISTS paiement (
                                        id_paiement     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        reference       VARCHAR(60) NOT NULL,
                                        montant         NUMERIC(12,2) NOT NULL CHECK (montant >= 0),
                                        methode         methode_paiement NOT NULL,
                                        statut          statut_paiement NOT NULL DEFAULT 'INITIE',
                                        date_paiement   TIMESTAMP NOT NULL DEFAULT now(),
                                        id_reservation  UUID NOT NULL,
                                        CONSTRAINT uq_paiement_reference UNIQUE (reference),
                                        CONSTRAINT fk_paiement_reservation
                                            FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation)
                                                ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_paiement_reservation ON paiement(id_reservation);
CREATE INDEX IF NOT EXISTS idx_paiement_statut      ON paiement(statut);

-- =========================
-- NOTE IMPORTANTE (SIÈGE UNIQUE PAR SÉANCE)
-- =========================
-- La contrainte parfaite "un siège ne peut être réservé qu'une seule fois pour une même séance"
-- est difficile en SQL pur ici car id_seance est sur reservation, pas sur reservation_siege.
-- Le plus simple et courant : vérifier en code (service) avant insertion.
-- Si tu veux du 100% DB, on peut ajouter une colonne id_seance dans reservation_siege
-- (dénormalisation contrôlée) + UNIQUE(id_seance, id_siege).


INSERT INTO genre (id_genre, libelle, code) VALUES
                                                ('GEN001', 'Action', 'ACT'),
                                                ('GEN002', 'Comédie', 'COM'),
                                                ('GEN003', 'Drame', 'DRM'),
                                                ('GEN004', 'Science-Fiction', 'SF'),
                                                ('GEN005', 'Horreur', 'HOR'),
                                                ('GEN006', 'Animation', 'ANI'),
                                                ('GEN007', 'Romance', 'ROM');


INSERT INTO film (id_film, titre, description, duree, id_genre, date_sortie) VALUES
                                                                                 (
                                                                                     'FIL001',
                                                                                     'Avengers: Infinity War',
                                                                                     'Les Avengers affrontent Thanos pour sauver l univers.',
                                                                                     149,
                                                                                     'GEN001',
                                                                                     '2018-04-27'
                                                                                 ),
                                                                                 (
                                                                                     'FIL002',
                                                                                     'Interstellar',
                                                                                     'Un voyage spatial pour sauver l humanité.',
                                                                                     169,
                                                                                     'GEN004',
                                                                                     '2014-11-07'
                                                                                 ),
                                                                                 (
                                                                                     'FIL003',
                                                                                     'Le Dîner de Cons',
                                                                                     'Une comédie culte française.',
                                                                                     80,
                                                                                     'GEN002',
                                                                                     '1998-04-15'
                                                                                 ),
                                                                                 (
                                                                                     'FIL004',
                                                                                     'Titanic',
                                                                                     'Une histoire d amour tragique à bord du Titanic.',
                                                                                     195,
                                                                                     'GEN007',
                                                                                     '1997-12-19'
                                                                                 ),
                                                                                 (
                                                                                     'FIL005',
                                                                                     'Joker',
                                                                                     'L origine sombre du célèbre ennemi de Batman.',
                                                                                     122,
                                                                                     'GEN003',
                                                                                     '2019-10-04'
                                                                                 ),
                                                                                 (
                                                                                     'FIL006',
                                                                                     'Conjuring',
                                                                                     'Des phénomènes paranormaux terrifiants.',
                                                                                     112,
                                                                                     'GEN005',
                                                                                     '2013-07-19'
                                                                                 ),
                                                                                 (
                                                                                     'FIL007',
                                                                                     'Toy Story',
                                                                                     'Les jouets prennent vie quand les humains ont le dos tourné.',
                                                                                     81,
                                                                                     'GEN006',
                                                                                     '1995-11-22'
                                                                                 );
INSERT INTO film (id_film, titre, description, duree, id_genre, date_sortie) VALUES
                                                                                 (
                                                                                     'FIL008',
                                                                                     'Inception',
                                                                                     'Un voleur infiltre les rêves pour y implanter une idée.',
                                                                                     148,
                                                                                     'GEN004',
                                                                                     '2010-07-16'
                                                                                 ),
                                                                                 (
                                                                                     'FIL009',
                                                                                     'The Dark Knight',
                                                                                     'Batman affronte le Joker, un criminel anarchiste.',
                                                                                     152,
                                                                                     'GEN001',
                                                                                     '2008-07-18'
                                                                                 ),
                                                                                 (
                                                                                     'FIL010',
                                                                                     'Forrest Gump',
                                                                                     'La vie extraordinaire d un homme simple au destin incroyable.',
                                                                                     142,
                                                                                     'GEN003',
                                                                                     '1994-07-06'
                                                                                 ),
                                                                                 (
                                                                                     'FIL011',
                                                                                     'Parasite',
                                                                                     'Une famille pauvre s infiltre dans la vie d une famille riche.',
                                                                                     132,
                                                                                     'GEN003',
                                                                                     '2019-05-30'
                                                                                 ),
                                                                                 (
                                                                                     'FIL012',
                                                                                     'Gladiator',
                                                                                     'Un général romain trahi devient gladiateur.',
                                                                                     155,
                                                                                     'GEN001',
                                                                                     '2000-05-05'
                                                                                 ),
                                                                                 (
                                                                                     'FIL013',
                                                                                     'La La Land',
                                                                                     'Une histoire d amour entre un musicien et une actrice à Los Angeles.',
                                                                                     128,
                                                                                     'GEN007',
                                                                                     '2016-12-09'
                                                                                 ),
                                                                                 (
                                                                                     'FIL014',
                                                                                     'Ça',
                                                                                     'Une entité maléfique terrorise une petite ville.',
                                                                                     135,
                                                                                     'GEN005',
                                                                                     '2017-09-08'
                                                                                 ),
                                                                                 (
                                                                                     'FIL015',
                                                                                     'Spider-Man: No Way Home',
                                                                                     'Peter Parker affronte les conséquences de son identité révélée.',
                                                                                     148,
                                                                                     'GEN001',
                                                                                     '2021-12-17'
                                                                                 ),
                                                                                 (
                                                                                     'FIL016',
                                                                                     'Le Roi Lion',
                                                                                     'Un jeune lion doit reprendre sa place sur le trône.',
                                                                                     88,
                                                                                     'GEN006',
                                                                                     '1994-06-15'
                                                                                 ),
                                                                                 (
                                                                                     'FIL017',
                                                                                     'Matrix',
                                                                                     'La réalité n est qu une illusion contrôlée par des machines.',
                                                                                     136,
                                                                                     'GEN004',
                                                                                     '1999-03-31'
                                                                                 ),
                                                                                 (
                                                                                     'FIL018',
                                                                                     'Shining',
                                                                                     'Un écrivain sombre dans la folie dans un hôtel isolé.',
                                                                                     146,
                                                                                     'GEN005',
                                                                                     '1980-05-23'
                                                                                 ),
                                                                                 (
                                                                                     'FIL019',
                                                                                     'Pulp Fiction',
                                                                                     'Des histoires criminelles entremêlées à Los Angeles.',
                                                                                     154,
                                                                                     'GEN003',
                                                                                     '1994-10-14'
                                                                                 ),
                                                                                 (
                                                                                     'FIL020',
                                                                                     'Coco',
                                                                                     'Un jeune garçon découvre le monde des morts.',
                                                                                     105,
                                                                                     'GEN006',
                                                                                     '2017-11-22'
                                                                                 );



SELECT f.id_film, f.titre, g.libelle AS genre, f.duree, f.date_sortie
FROM film f
         JOIN genre g ON g.id_genre = f.id_genre;
