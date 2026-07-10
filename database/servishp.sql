-- DATABASE SERVIS HP
DROP DATABASE IF EXISTS servishp;
CREATE DATABASE servishp;
USE servishp;

CREATE TABLE customer (
    id_cust VARCHAR(15) NOT NULL,
    nama VARCHAR(100) NOT NULL,
    no_telp VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_cust)
);

CREATE TABLE servis (
    id_trans VARCHAR(20) NOT NULL,
    id_cust VARCHAR(15) NOT NULL,
    jenis_servis VARCHAR(50) NOT NULL,
    harga_layanan INT NOT NULL,
    tambahan TEXT,
    harga_tambahan INT NOT NULL,
    diskon INT NOT NULL,
    total INT NOT NULL,
    tanggal DATE NOT NULL,
    PRIMARY KEY (id_trans),
    CONSTRAINT fk_customer FOREIGN KEY (id_cust)
      REFERENCES customer(id_cust)
      ON UPDATE CASCADE
      ON DELETE CASCADE
);

INSERT INTO customer (id_cust,nama,no_telp) VALUES
('C001','Andi Saputra','081234567890'),
('C002','Budi Santoso','082123456789'),
('C003','Citra Lestari','085712345678'),
('C004','Dimas Pratama','081998887777'),
('C005','Eka Wulandari','089512345678');

INSERT INTO servis
(id_trans,id_cust,jenis_servis,harga_layanan,tambahan,harga_tambahan,diskon,total,tanggal)
VALUES
('TR01072026-1010','C001','Ganti LCD',100000,'Temper Glass',50000,0,150000,'2026-07-01'),
('TR02072026-1145','C002','Ganti Baterai',50000,'Baterai Original',100000,0,150000,'2026-07-02'),
('TR03072026-1400','C003','Servis Mesin',300000,'IC Power',250000,55000,495000,'2026-07-03'),
('TR04072026-0900','C004','Perbaikan Tombol',50000,'Flexibel',50000,0,100000,'2026-07-04'),
('TR05072026-1600','C005','Lainnya',100000,'Speaker',50000,0,150000,'2026-07-05');
