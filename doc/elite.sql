-- reset
drop database elite;

-- Database: elite
CREATE DATABASE elite
    WITH 
    OWNER = elite
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
ALTER DATABASE elite
    SET search_path TO "$user", public;

CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION postgis_sfcgal;


-- Type: major_power
create type major_power as enum (
    'Alliance',
    'Empire',
    'Federation',
    'Independent',
    'None',
    'Pilots Federation'
);
ALTER TYPE public.major_power
    OWNER TO elite;


-- Type: state
create type state as enum (
    'Blight',
    'Boom',
    'Bust',
    'Civil Liberty',
    'Civil Unrest',
    'Civil War',
    'Damaged',
    'Drought',
    'Election',
    'Expansion',
    'Famine',
    'Infrastructure Failure',
    'Investment',
    'Lockdown',
    'Natural Disaster',
    'None',
    'Outbreak',
    'Pirate Attack',
    'Public Holiday',
    'Retreat',
    'Terrorist Attack',
    'Under Repairs',
    'War'
);
ALTER TYPE public.state
    OWNER TO elite;


-- Type: economy
create type economy as enum (
    'Agriculture',
    'Colony',
    'Damaged',
    'Extraction',
    'High Tech',
    'Industrial',
    'Military',
    'None',
    'Prison',
    'Private Enterprise',
    'Refinery',
    'Repair',
    'Rescue',
    'Service',
    'Terraforming',
    'Tourism'
);
ALTER TYPE public.economy
    OWNER TO elite;


-- Type: economy
create type government as enum (
    'Anarchy',
    'Communism',
    'Confederacy',
    'Corporate',
    'Cooperative',
    'Democracy',
    'Dictatorship',
    'Feudal',
    'Patronage',
    'Prison Colony',
    'Theocracy',
    'None',
    'Engineer',
    'Prison'
);
ALTER TYPE public.government
    OWNER TO elite;


-- Type: landing_pad_size
create type landing_pad_size as enum (
    'None',
    'S',
    'M',
    'L'
);
ALTER TYPE public.landing_pad_size
    OWNER TO elite;

-- Type: security
create type security as enum (
    'Anarchy',
    'Low',
    'Medium',
    'High'
);
ALTER TYPE public.security
    OWNER TO elite;

-- Table: public.system
CREATE TABLE public.system
(
    system_id integer NOT NULL,
    system_name character varying(64) COLLATE pg_catalog."default",
    coordinates geometry,
    security security,
    population bigint,
    CONSTRAINT system_pkey PRIMARY KEY (system_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.system
    OWNER to elite;

CREATE INDEX coordinates
    ON public.system USING gist
    (coordinates gist_geometry_ops_nd)
    TABLESPACE pg_default;


-- Table: public.station
CREATE TABLE public.station
(
    station_id integer NOT NULL,
    station_name character varying(64) COLLATE pg_catalog."default",
    arrival_distance integer,
    system_id integer,
    max_landing_pad_size landing_pad_size,
    has_docking boolean,
    is_planetary boolean,
    has_blackmarket boolean,
    has_market boolean,
    has_refuel boolean,
    has_repair boolean,
    has_rearm boolean,
    has_outfitting boolean,
    has_shipyard boolean,
    has_commodities boolean,
    CONSTRAINT station_pkey PRIMARY KEY (station_id),
    CONSTRAINT station_system_id FOREIGN KEY (system_id)
        REFERENCES public.system (system_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.station
    OWNER to elite;

CREATE INDEX station_arrival_distance
    ON public.station USING btree
    (arrival_distance)
    TABLESPACE pg_default;

CREATE INDEX station_system_id
    ON public.station USING btree
    (system_id)
    TABLESPACE pg_default;


-- Table: public.faction
CREATE TABLE public.faction
(
    faction_id integer NOT NULL,
    faction_name character varying(128) COLLATE pg_catalog."default",
    faction_allegiance major_power,
    faction_government government,
    CONSTRAINT faction_pkey PRIMARY KEY (faction_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.faction
    OWNER to elite;

CREATE INDEX faction_allegiance
    ON public.faction USING btree
    (faction_allegiance)
    TABLESPACE pg_default;

CREATE INDEX faction_government
    ON public.faction USING btree
    (faction_government)
    TABLESPACE pg_default;


-- Table: public.faction_presence
CREATE TABLE public.faction_presence
(
    faction_id integer NOT NULL,
    system_id integer NOT NULL,
    CONSTRAINT faction_presence_pkey PRIMARY KEY (faction_id, system_id),
    CONSTRAINT faction_presence_faction_id FOREIGN KEY (faction_id)
        REFERENCES public.faction (faction_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT faction_presence_system_id FOREIGN KEY (system_id)
        REFERENCES public.system (system_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.faction_presence
    OWNER to elite;

CREATE INDEX faction_id
    ON public.faction_presence USING btree
    (faction_id)
    TABLESPACE pg_default;

CREATE INDEX system_id
    ON public.faction_presence USING btree
    (system_id)
    TABLESPACE pg_default;


-- Table: public.faction_presence_state
CREATE TABLE public.faction_presence_state
(
    faction_id integer NOT NULL,
    system_id integer NOT NULL,
    faction_presence_state state,
    CONSTRAINT faction_presence_state_pkey PRIMARY KEY (faction_id, system_id, faction_presence_state),
    CONSTRAINT faction_presence_state_fkey FOREIGN KEY (faction_id, system_id)
        REFERENCES public.faction_presence (faction_id, system_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.faction_presence_state
    OWNER to elite;

-- Table: public.station_economy
CREATE TABLE public.station_economy
(
    station_id integer NOT NULL,
    station_economy economy NOT NULL,
    CONSTRAINT station_economy_pkey PRIMARY KEY (station_id, station_economy),
    CONSTRAINT station_economy_station_id FOREIGN KEY (station_id)
        REFERENCES public.station (station_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.station_economy
    OWNER to elite;

CREATE INDEX station_economy_station_id
    ON public.station_economy USING btree
    (station_id)
    TABLESPACE pg_default;

CREATE INDEX station_economy_station_economy
    ON public.station_economy USING btree
    (station_economy)
    TABLESPACE pg_default;


-- Table: public.station_state
CREATE TABLE public.station_state
(
    station_id integer NOT NULL,
    station_state state NOT NULL,
    CONSTRAINT station_state_pkey PRIMARY KEY (station_id, station_state),
    CONSTRAINT station_state_station_id FOREIGN KEY (station_id)
        REFERENCES public.station (station_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.station_state
    OWNER to elite;

CREATE INDEX station_state_station_id
    ON public.station_state USING btree
    (station_id)
    TABLESPACE pg_default;

CREATE INDEX station_state_station_state
    ON public.station_state USING btree
    (station_state)
    TABLESPACE pg_default;


-- Table: public.system_state
CREATE TABLE public.system_state
(
    system_id integer NOT NULL,
    system_state state NOT NULL,
    CONSTRAINT system_state_pkey PRIMARY KEY (system_id, system_state),
    CONSTRAINT station_state_system_id FOREIGN KEY (system_id)
        REFERENCES public.system (system_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.system_state
    OWNER to elite;

CREATE INDEX system_state_system_id
    ON public.system_state USING btree
    (system_id)
    TABLESPACE pg_default;

CREATE INDEX system_state_system_state
    ON public.system_state USING btree
    (system_state)
    TABLESPACE pg_default;
