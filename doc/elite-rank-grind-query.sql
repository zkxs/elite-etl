-- beginning of super duper rank grind finder
begin;

-- create temporary tables
create temp table rank_grind_systems
(
    system_id integer not null,
    system_name character varying(64),
    coordinates geometry,
    aligned_factions integer not null,
    total_factions integer not null,
    good_boom_stations integer not null,
    good_stations integer not null,
    bad_stations integer not null,
    any_large boolean not null,
    max_arrival_distance double precision not null,
    nearest integer,
    second_nearest double precision,
    CONSTRAINT rank_grind_systems_pkey PRIMARY KEY (system_id)
)
on commit drop;
CREATE INDEX tmp_coordinates
    ON rank_grind_systems USING gist
    (coordinates gist_geometry_ops_nd);

create temp table red_herrings
(
    system_id integer not null,
    coordinates geometry,
    CONSTRAINT red_herrings_pkey PRIMARY KEY (system_id)
)
on commit drop;
CREATE INDEX tmp_coordinates_red_herrings
    ON red_herrings USING gist
    (coordinates gist_geometry_ops_nd);
    
-- find valid rank grind systems
insert into rank_grind_systems
select *
from (
    select
        *,
        (
            select count(*) from faction_presence
            left join faction on faction_presence.faction_id = faction.faction_id
            where faction_allegiance = 'Federation' -- 'Federation' or 'Empire'
            and system.system_id = faction_presence.system_id
        ) as aligned_factions,
        (
            select count(*) from faction_presence
            where system.system_id = faction_presence.system_id
        ) as total_factions,
        (
            select count(*) from station
            left join station_state on station.station_id = station_state.station_id
            where system.system_id = station.system_id
            and has_docking
            and not is_planetary
            and station_state = 'Boom'
        ) as good_boom_stations,
        (
            select count(*) from station
            where system.system_id = station.system_id
            and has_docking
            and not is_planetary
        ) as good_stations,
        (
            select count(*) from station
            where system.system_id = station.system_id
            and has_docking
            and is_planetary
        ) as bad_stations,
        (
            select bool_or(max_landing_pad_size = 'L' and not is_planetary) from station -- do we care how big the stations are?
            where system.system_id = station.system_id
        ) as any_large,
        (
            select max(arrival_distance) from station
            where system.system_id = station.system_id
            and not is_planetary
        ) max_arrival_distance
    from system
) d1
where aligned_factions > 0
and good_stations > 0
-- and bad_stations = 0 -- does planetary matter?
;

-- red herrings. any system we could get missions to
insert into red_herrings
select system_id, coordinates from system
where (
    select bool_or(has_docking and not is_planetary) from station -- purposely allow planetaries
    where system.system_id = station.system_id
);

-- calculate nearest red herrings
update rank_grind_systems
set nearest=(
    select red_herrings.system_id
    from red_herrings
    where rank_grind_systems.system_id != red_herrings.system_id
    order by ST_3DDistance(rank_grind_systems.coordinates, red_herrings.coordinates) asc
    limit 1
);

-- second nearest red herrings
update rank_grind_systems
set second_nearest=(
    select ST_3DDistance(rank_grind_systems.coordinates, red_herrings.coordinates) as distance
    from red_herrings
    where rank_grind_systems.system_id != red_herrings.system_id
    order by distance asc
    offset 1
    limit 1
);

-- find possible pairs
select
    -- a_system_id,
    a_system_name,
    a_factions || '/' || a_total_factions as a_factions,
    a_boom_stations || '/' || a_stations as a_boom_stations,
    -- a_nearest,
    to_char(a_second_nearest, '99999999999.9') || ' ly' as a_second_nearest,
    case when a_any_large then 'yes' else 'no' end as a_any_large,
    a_ls || ' ls' as a_ls, 
    -- b_system_id,
    b_system_name,
    b_factions || '/' || b_total_factions as b_factions,
    b_boom_stations || '/' || b_stations as b_boom_stations,
    -- b_nearest,
    to_char(b_second_nearest, '99999999999.9') || ' ly' as b_second_nearest,
    case when b_any_large then 'yes' else 'no' end as b_any_large,
    b_ls || ' ls' as b_ls,
    to_char(distance, '99999999999.9') || ' ly' as distance
from (
    select
        a.system_id             as a_system_id,
        a.system_name           as a_system_name,
        a.aligned_factions      as a_factions,
        a.total_factions        as a_total_factions,
        a.good_boom_stations    as a_boom_stations,
        a.good_stations         as a_stations,
        a.nearest               as a_nearest,
        a.second_nearest        as a_second_nearest,
        a.any_large             as a_any_large,
        a.max_arrival_distance  as a_ls,
        b.system_id             as b_system_id,
        b.system_name           as b_system_name,
        b.aligned_factions      as b_factions,
        b.total_factions        as b_total_factions,
        b.good_boom_stations    as b_boom_stations,
        b.good_stations         as b_stations,
        b.nearest               as b_nearest,
        b.second_nearest        as b_second_nearest,
        b.any_large             as b_any_large,
        b.max_arrival_distance  as b_ls,
        ST_3DDistance(a.coordinates, b.coordinates) as distance
    from
        rank_grind_systems a,
        rank_grind_systems b
    where a.system_id < b.system_id
) d1
where distance < 20 -- 10 ly is max range of data delivery
and a_system_id = b_nearest -- make sure there is no closer system that could have missions
and b_system_id = a_nearest
and a_second_nearest > 20 -- make sure second nearest system is out of data delivery range
and b_second_nearest > 20
-- and greatest(a_stations, b_stations) = 1
-- and least(a_factions, b_factions) > 3
order by least(a_second_nearest, b_second_nearest) desc;

commit;
-- end of super duper rank grind finder
