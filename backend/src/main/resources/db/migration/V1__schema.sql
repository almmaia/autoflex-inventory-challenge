create table if not exists product (
  id bigserial primary key,
  code varchar(50) not null unique,
  name varchar(200) not null,
  price numeric(12,2) not null
);

create table if not exists raw_material (
  id bigserial primary key,
  code varchar(50) not null unique,
  name varchar(200) not null,
  stock_qty numeric(14,3) not null
);

create table if not exists product_material (
  product_id bigint not null references product(id) on delete cascade,
  material_id bigint not null references raw_material(id) on delete cascade,
  qty_needed numeric(14,3) not null,
  primary key (product_id, material_id)
);
