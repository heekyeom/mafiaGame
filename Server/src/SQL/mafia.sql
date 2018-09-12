drop table userinfo;

create table userinfo(
	id varchar2(12) not null primary key,
	pw varchar2(12) not null,
	win number(3) default 0,
	lose number(3) default 0,
	rate number(3) default 0
);


select * from userinfo;
