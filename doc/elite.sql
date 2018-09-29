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
    'Pilots Federation'
);
ALTER TYPE public.major_power
    OWNER TO elite;


-- Type: state
create type state as enum (
    'Boom',
    'Bust',e 
    'Civil Unrest',
    'Civil War',
    'Election',
    'Expansion',
    'Famine',
    'Investment',
    'Lockdown',
    'None',
    'Outbreak',
    'Retreat',
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
    'Refinery',
    'Repair',
    'Rescue',
    'Service',
    'Terraforming',
    'Tourism'
);
ALTER TYPE public.economy
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


-- Table: public.system
CREATE TABLE public.system
(
    system_id integer NOT NULL,
    system_name character varying(64) COLLATE pg_catalog."default",
    coordinates geometry,
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
    station_state state,
    arrival_distance integer,
    system_id integer,
    max_landing_pad_size landing_pad_size,
    has_docking boolean,
    is_planetary boolean,
    CONSTRAINT station_pkey PRIMARY KEY (station_id),
    CONSTRAINT system_id FOREIGN KEY (system_id)
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

CREATE INDEX arrival_distance
    ON public.station USING btree
    (arrival_distance)
    TABLESPACE pg_default;

CREATE INDEX station_state
    ON public.station USING btree
    (station_state)
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


-- Table: public.faction_presence
CREATE TABLE public.faction_presence
(
    faction_id integer NOT NULL,
    system_id integer NOT NULL,
    CONSTRAINT faction_presence_pkey PRIMARY KEY (faction_id, system_id),
    CONSTRAINT faction_id FOREIGN KEY (faction_id)
        REFERENCES public.faction (faction_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT system_id FOREIGN KEY (system_id)
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


-- Table: public.station_economy
CREATE TABLE public.station_economy
(
    station_id integer NOT NULL,
    station_economy economy NOT NULL,
    CONSTRAINT station_economy_pkey PRIMARY KEY (station_id, station_economy),
    CONSTRAINT station_id FOREIGN KEY (station_id)
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

CREATE INDEX station_id
    ON public.station_economy USING btree
    (station_id)
    TABLESPACE pg_default;

CREATE INDEX station_economy_idx
    ON public.station_economy USING btree
    (station_economy)
    TABLESPACE pg_default;

