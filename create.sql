create table assignment (id_context bigint not null, id_principal bigint not null, id_role bigint not null, id_scope bigint not null, primary key (id_context, id_principal, id_role, id_scope));
create table context (id bigint generated by default as identity, description varchar(255), name varchar(255), primary key (id));
create table permission (id bigint generated by default as identity, description varchar(255), name varchar(255), primary key (id));
create table principal (id bigint generated by default as identity, login varchar(255), primary key (id));
create table resource (id bigint generated by default as identity, selector varchar(255), primary key (id));
create table role (id bigint generated by default as identity, description varchar(255), name varchar(255), primary key (id));
create table role_permission (id_permission bigint not null unique, id_role bigint not null);
create table scope (id bigint generated by default as identity, name varchar(255), primary key (id));
create table scope_resource (id_resource bigint not null unique, id_scope bigint not null);
alter table if exists assignment add constraint FKcy57gtlt2rrs1dl1lnu9q2b70 foreign key (id_context) references context;
alter table if exists assignment add constraint FK9amouryl0fv2hol6vgwpk2jm0 foreign key (id_principal) references principal;
alter table if exists assignment add constraint FKpuxd6nqyilrtbrkqichtrbxsh foreign key (id_role) references role;
alter table if exists assignment add constraint FKp2autr052xlxiaqf9u913w8px foreign key (id_scope) references scope;
alter table if exists role_permission add constraint FK2ws6nbakjvnk1p41enwua5c2j foreign key (id_permission) references permission;
alter table if exists role_permission add constraint FK4mqvliy9oxgi2g5h2tqpvvd3l foreign key (id_role) references role;
alter table if exists scope_resource add constraint FKr1xbkgifxrt37ytvv1i39re3g foreign key (id_resource) references resource;
alter table if exists scope_resource add constraint FK94gm5fvf44yrep1m3ydg772t5 foreign key (id_scope) references scope;