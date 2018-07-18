DROP TABLE IF EXISTS trades;
CREATE TABLE trades (
  id       INTEGER NOT NULL AUTO_INCREMENT,
  bought_price   DOUBLE NOT NULL ,
  sell_price   DOUBLE NOT NULL ,
  bought_date DATE NOT NULL ,
  sell_date DATE NOT NULL ,
  profit   DOUBLE NOT NULL ,
  profit_percent   DOUBLE NOT NULL ,
  PRIMARY KEY (id)
);
