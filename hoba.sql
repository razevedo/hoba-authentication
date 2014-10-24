create table hoba_user (
	id_user serial primary key,
	field1 varchar (40),
	field2 varchar (40),
	field3 varchar(40)
	);


create table hoba_token (
	id_token serial primary key,
	token varchar (200),
	expiration timestamp ,
	is_valid boolean,
	id_user  integer references hoba_user(id_user)
	);

create table hoba_devices (
	id_devices serial primary key,
	iduser integer references hoba_user(id_user),
	did varchar(20),
	ip_address varchar(25),
	last_date timestamp,
	didtype varchar(200)
	);
	
create table hoba_keys (
	id_keys serial primary key,
	id_devices integer references hoba_devices(id_devices),
	kidtype varchar(20),
	kid varchar(40),
	pub varchar(500)
	);

drop table hoba_chalenges;

create table hoba_challenges (
	id_chalenge serial primary key,
	id_keys integer references hoba_keys(id_keys),
	chalenge varchar(200),
	expiration timestamp,
	is_valid boolean
	);