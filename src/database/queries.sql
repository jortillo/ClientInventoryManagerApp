CREATE TABLE CLIENT(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    NAME VARCHAR(255),
    Street VARCHAR(255),
    City VARCHAR(255),
    Zip CHAR(5),
    CONSTRAINT CLIENT_uk
        UNIQUE (Street, City, Zip)
);
    
CREATE TABLE PRODUCT(
    PID INTEGER PRIMARY KEY AUTOINCREMENT,
    Description TEXT,
    Price INT,
    Quantity INT
);
    
CREATE TABLE CLIENT_CONTACTS(
    ID INTEGER,
    Contact_Info VARCHAR(255),
    CONSTRAINT CLIENT_CONTACTS_fk 
    FOREIGN KEY(ID) REFERENCES CLIENT(ID)
    ON UPDATE CASCADE
);
    
CREATE TABLE COMMISSION(
    ID INTEGER,
    PID INTEGER,
    Order_Date DATE,
    Order_Status VARCHAR(255),
    Payment_Status VARCHAR(255),
    ETC VARCHAR(255),
    FOREIGN KEY(ID) REFERENCES CLIENT(ID)
    ON UPDATE CASCADE,
    FOREIGN KEY(PID) REFERENCES PRODUCT(PID)
    ON UPDATE CASCADE
);
    
CREATE VIEW CLIENT_VIEW AS
SELECT CLIENT.Name, CLIENT.Street, CLIENT.City, CLIENT.Zip, CLIENT_CONTACTS.Contact_Info
FROM CLIENT
INNER JOIN CLIENT_CONTACTS ON CLIENT.ID = CLIENT_CONTACTS.ID;

CREATE VIEW COMMISSION_VIEW AS
SELECT CLIENT.Name, PRODUCT.Description AS "Commissioned Product", PRODUCT.Price, COMMISSION.Order_Date, COMMISSION.Order_Status, COMMISSION.Payment_Status, COMMISSION.ETC  
FROM COMMISSION
INNER JOIN CLIENT ON CLIENT.ID = COMMISSION.ID
INNER JOIN PRODUCT ON PRODUCT.PID = COMMISSION.PID;

CREATE VIEW PRODUCT_VIEW AS 
SELECT PRODUCT.Description, PRODUCT.Price, PRODUCT.Quantity
FROM PRODUCT;

INSERT INTO CLIENT
VALUES(1, "John Doe", "1111", "A St", 12345);


INSERT INTO CLIENT
VALUES(2, "Dave Lee", "2222", "B St", 54321);

INSERT INTO CLIENT
VALUES(3, "Joe Moe", "3333", "C St", 14231);


INSERT INTO CLIENT_CONTACTS
VALUES(1, "Twitter: @johndoe");

INSERT INTO CLIENT_CONTACTS
VALUES(2, "Phone: 111-111-1111");

INSERT INTO CLIENT_CONTACTS
VALUES(3, "Discord: #1111 Joe");

INSERT INTO PRODUCT
VALUES(NULL, "Sailor Moon GC Controller", 159.99, 0);

INSERT INTO PRODUCT
VALUES(NULL, "Backlit Modded Gameboy Advance", 129.99, 1);

INSERT INTO PRODUCT
VALUES(NULL, "Custom PC, i7, GTX 1080, 16gb Ram, 500GB HD", 799.99, 2);

INSERT INTO PRODUCT
VALUES(NULL, "Website Design", 299.99, 1);

INSERT INTO COMMISSION
VALUES(1,1, "2020-01-20", "Delivered", "Paid", "2 weeks");

INSERT INTO COMMISSION
VALUES(2,3, "2020-06-22", "In-Progress", "Paid", "4 weeks");


SELECT * FROM PRODUCT;
SELECT * FROM CLIENT_CONTACTS;
SELECT * FROM CLIENT;
SELECT * FROM COMMISSION;
SELECT * FROM CLIENT_VIEW;
SELECT * FROM COMMISSION_VIEW;
SELECT * FROM PRODUCT_VIEW;
