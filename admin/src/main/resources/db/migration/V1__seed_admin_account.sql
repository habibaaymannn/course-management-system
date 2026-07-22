CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS admins (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES users(id)
);

INSERT INTO users (id, first_name, last_name, email, password, role)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'System',
    'Admin',
    'admin@example.com',
    '$2a$10$1ye7XwdmSksa7kWMYgCbNuVyMq12Wr8XzG3vWSkKkUNzcugMW7fE.',
    'ADMIN'
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO admins (id)
VALUES ('00000000-0000-0000-0000-000000000001')
ON CONFLICT (id) DO NOTHING;
