--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS request_statistic;
--
-- Name: request_statistic; Type: DATABASE; Schema: -; Owner: statuser
--

CREATE DATABASE request_statistic WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Russian_Ukraine.1251' LC_CTYPE = 'Russian_Ukraine.1251';


ALTER DATABASE request_statistic OWNER TO statuser;

\connect request_statistic

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: statuser
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO statuser;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: request; Type: TABLE; Schema: public; Owner: statuser
--

CREATE TABLE public.request (
    id bigint NOT NULL,
    request_url text,
    request_time timestamp without time zone,
    ip_address text,
    sent_bytes numeric,
    speed numeric,
    request_duration numeric,
    request_status_id bigint NOT NULL
);


ALTER TABLE public.request OWNER TO statuser;

--
-- Name: status; Type: TABLE; Schema: public; Owner: statuser
--

CREATE TABLE public.status (
    id bigint NOT NULL,
    request_number integer,
    last_request_url text,
    last_request_time timestamp without time zone,
    last_ip_address text
);


ALTER TABLE public.status OWNER TO statuser;

--
-- Name: status2_id_seq; Type: SEQUENCE; Schema: public; Owner: statuser
--

CREATE SEQUENCE public.status2_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status2_id_seq OWNER TO statuser;

--
-- Name: status2_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: statuser
--

ALTER SEQUENCE public.status2_id_seq OWNED BY public.status.id;


--
-- Name: status_upd_seq; Type: SEQUENCE; Schema: public; Owner: statuser
--

CREATE SEQUENCE public.status_upd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status_upd_seq OWNER TO statuser;

--
-- Name: table_name_id_seq; Type: SEQUENCE; Schema: public; Owner: statuser
--

CREATE SEQUENCE public.table_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.table_name_id_seq OWNER TO statuser;

--
-- Name: table_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: statuser
--

ALTER SEQUENCE public.table_name_id_seq OWNED BY public.request.id;


--
-- Name: request id; Type: DEFAULT; Schema: public; Owner: statuser
--

ALTER TABLE ONLY public.request ALTER COLUMN id SET DEFAULT nextval('public.table_name_id_seq'::regclass);


--
-- Name: status id; Type: DEFAULT; Schema: public; Owner: statuser
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status2_id_seq'::regclass);


--
-- Data for Name: request; Type: TABLE DATA; Schema: public; Owner: statuser
--

COPY public.request (id, request_url, request_time, ip_address, sent_bytes, speed, request_duration, request_status_id) FROM stdin;
102	http://sinoptik.ua	2020-02-09 20:39:16.951685	212.42.76.150	24.146	28.508	0.847	101
103	http://rozetka.ua	2020-02-09 20:40:19.652258	78.27.198.40	111.378	403.543	0.276	101
104	http://prom.ua	2020-02-09 20:42:46.611809	193.34.169.17	89.4	234.031	0.382	101
\.


--
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: statuser
--

COPY public.status (id, request_number, last_request_url, last_request_time, last_ip_address) FROM stdin;
101	3	http://prom.ua	2020-02-09 20:42:46.611809	193.34.169.17
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: statuser
--

SELECT pg_catalog.setval('public.hibernate_sequence', 104, true);


--
-- Name: status2_id_seq; Type: SEQUENCE SET; Schema: public; Owner: statuser
--

SELECT pg_catalog.setval('public.status2_id_seq', 1, false);


--
-- Name: status_upd_seq; Type: SEQUENCE SET; Schema: public; Owner: statuser
--

SELECT pg_catalog.setval('public.status_upd_seq', 1, false);


--
-- Name: table_name_id_seq; Type: SEQUENCE SET; Schema: public; Owner: statuser
--

SELECT pg_catalog.setval('public.table_name_id_seq', 1, false);


--
-- Name: status status2_pk; Type: CONSTRAINT; Schema: public; Owner: statuser
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status2_pk PRIMARY KEY (id);


--
-- Name: request table_name_pk; Type: CONSTRAINT; Schema: public; Owner: statuser
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT table_name_pk PRIMARY KEY (id);


--
-- Name: table_name_id_uindex; Type: INDEX; Schema: public; Owner: statuser
--

CREATE UNIQUE INDEX table_name_id_uindex ON public.request USING btree (id);


--
-- Name: request table_name_status2_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: statuser
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT table_name_status2_id_fk FOREIGN KEY (request_status_id) REFERENCES public.status(id);


--
-- Name: TABLE status; Type: ACL; Schema: public; Owner: statuser
--

REVOKE ALL ON TABLE public.status FROM statuser;
GRANT ALL ON TABLE public.status TO statuser WITH GRANT OPTION;


--
-- Name: SEQUENCE status_upd_seq; Type: ACL; Schema: public; Owner: statuser
--

REVOKE ALL ON SEQUENCE public.status_upd_seq FROM statuser;
GRANT ALL ON SEQUENCE public.status_upd_seq TO statuser WITH GRANT OPTION;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO statuser WITH GRANT OPTION;


--
-- PostgreSQL database dump complete
--

