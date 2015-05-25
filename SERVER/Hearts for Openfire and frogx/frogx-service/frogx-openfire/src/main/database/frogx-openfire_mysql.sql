# Initializing the openfire mysql database for the Multi-User Gaming Plugin  

INSERT INTO ofVersion (name, version) VALUES ('frogx-openfire', 0);

CREATE TABLE frogxMugService (
  serviceID             BIGINT        NOT NULL,
  subdomain             VARCHAR(255)  NOT NULL,
  description           VARCHAR(255)  NULL, 
  PRIMARY KEY(subdomain),
  INDEX frogxMugService_serviceID_idx(serviceID)
);

CREATE TABLE frogxMugServiceProp (
  serviceID           BIGINT        NOT NULL,
  name                VARCHAR(100)  NOT NULL,
  propValue           TEXT          NOT NULL,
  PRIMARY KEY (serviceID, name)
);

