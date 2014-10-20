create table hoba_user (
	id_user serial primary key,
	field1 varchar (40),
	field2 varchar (40),
	field3 varchar(40)
	);

create table hoba_token (
	id_token serial primary key,
	token varchar (200),
	expiration integer references hoba_user(id_user),
	id_user integer 
	);



create table hoba_devices (
	id_devices serial primary key,
	iduser integer references hoba_user(id_user),
	did varchar(20),
	didtype varchar(40)
	);

alter table hoba_devices alter column didtype set data type varchar (200);

create table hoba_keys (
	id_keys serial primary key,
	id_devices integer references hoba_devices(id_devices),
	kidtype varchar(20),
	kid varchar(40),
	pub varchar(500)
	);

create table hoba_chalenges (
	id_chalenge serial primary key,
	id_keys integer references hoba_keys(id_keys),
	chalenge varchar(200),
	expiration timestamp
	);