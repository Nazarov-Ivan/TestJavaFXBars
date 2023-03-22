create table contracts
(
    id          bigserial
        primary key,
    number      integer not null,
    date        date    not null,
    last_update date    not null
);

alter table contracts
    owner to postgres;

INSERT INTO public.contracts (id, number, date, last_update) VALUES (2, 2, '2002-06-04', '2022-12-01');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (1, 1, '1994-05-12', '2023-01-12');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (3, 3, '2007-11-16', '2023-03-09');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (5, 5, '2014-06-13', '2023-02-22');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (4, 4, '2018-08-10', '2021-06-09');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (6, 6, '2019-06-13', '2023-03-02');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (7, 7, '2018-08-24', '2023-03-01');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (8, 8, '2021-05-01', '2022-03-04');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (9, 9, '2016-02-13', '2023-03-22');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (10, 10, '2023-03-01', '2023-03-01');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (11, 11, '2020-09-19', '2022-03-05');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (12, 12, '2022-11-05', '2023-03-21');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (13, 13, '2022-12-04', '2023-03-01');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (14, 14, '2009-05-01', '2023-02-02');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (15, 15, '2020-03-07', '2023-03-02');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (16, 16, '2017-03-10', '2022-03-01');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (17, 17, '2020-05-13', '2023-03-05');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (18, 18, '2010-03-12', '2023-01-18');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (19, 19, '2023-03-01', '2023-03-05');
INSERT INTO public.contracts (id, number, date, last_update) VALUES (20, 20, '2023-01-19', '2023-03-19');
