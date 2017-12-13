CREATE TABLE users (
    id INT AUTO_INCREMENT,
    user_id VARCHAR(128) NOT NULL UNIQUE,
    role VARCHAR(300) NOT NULL DEFAULT 'normal',
    hasher VARCHAR(300) NOT NULL,
    salt VARCHAR(300),
    password VARCHAR(300) NOT NULL,
    first_name VARCHAR(300),
    last_name VARCHAR(300),
    email VARCHAR(300) not null,
    avatar_url VARCHAR(300),
    activated BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY pk_user_id (user_id)
);


CREATE TABLE coffee_beans (
    id INT AUTO_INCREMENT,
    name VARCHAR(300),
    kind VARCHAR(300),
    coffee_shop_id INT,
    PRIMARY KEY (id)
);

CREATE TABLE coffee_shops (
    id INT AUTO_INCREMENT,
    name VARCHAR(300),
    email VARCHAR(300),
    owner_name VARCHAR(300),
    address VARCHAR(300),
    PRIMARY KEY (id)
);
