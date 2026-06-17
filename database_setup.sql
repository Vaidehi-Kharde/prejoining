-- ============================================================
-- Database setup for User / Role / UserRole schema
-- Compatible with PostgreSQL (adjust CREATE DATABASE for MySQL/SQLite)
-- ============================================================

-- 1. CREATE DATABASE
-- PostgreSQL: run while connected to postgres database
CREATE DATABASE prejoining_db;

-- Connect to the new database before running the rest:
-- \c prejoining_db   (psql)
-- USE prejoining_db; (MySQL)

-- ============================================================
-- 2. CREATE TABLES
-- ============================================================

CREATE TABLE "User" (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone         VARCHAR(20),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Role" (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE "UserRole" (
    user_id INTEGER NOT NULL REFERENCES "User"(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES "Role"(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ============================================================
-- 3. INSERT SAMPLE DATA
-- ============================================================

INSERT INTO "Role" (name) VALUES
    ('Admin'),
    ('User'),
    ('Moderator');

INSERT INTO "User" (name, email, password_hash, phone, created_at) VALUES
    ('Alice Johnson', 'alice@example.com', 'hash_alice_123', '555-0101', '2024-01-15 09:30:00'),
    ('Bob Smith',     'bob@example.com',   'hash_bob_456',   '555-0102', '2024-02-20 14:00:00'),
    ('Carol Lee',     'carol@example.com', 'hash_carol_789', '555-0103', '2024-03-10 11:45:00'),
    ('David Kim',     'david@example.com', 'hash_david_321', NULL,       '2024-04-05 08:15:00');

INSERT INTO "UserRole" (user_id, role_id) VALUES
    (1, 1),  -- Alice  -> Admin
    (1, 2),  -- Alice  -> User
    (2, 2),  -- Bob    -> User
    (3, 2),  -- Carol  -> User
    (3, 3),  -- Carol  -> Moderator
    (4, 2);  -- David  -> User

-- ============================================================
-- 4. BASIC OPERATIONS
-- ============================================================

-- 4a. List all users
SELECT id, name, email, phone, created_at
FROM "User"
ORDER BY created_at;

-- 4b. List all roles
SELECT id, name
FROM "Role"
ORDER BY id;

-- 4c. List users with their assigned roles
SELECT u.id, u.name, u.email, r.name AS role_name
FROM "User" u
JOIN "UserRole" ur ON u.id = ur.user_id
JOIN "Role" r ON ur.role_id = r.id
ORDER BY u.name, r.name;

-- 4d. Find all users with the Admin role
SELECT u.id, u.name, u.email
FROM "User" u
JOIN "UserRole" ur ON u.id = ur.user_id
JOIN "Role" r ON ur.role_id = r.id
WHERE r.name = 'Admin';

-- 4e. Count users per role
SELECT r.name AS role_name, COUNT(ur.user_id) AS user_count
FROM "Role" r
LEFT JOIN "UserRole" ur ON r.id = ur.role_id
GROUP BY r.id, r.name
ORDER BY user_count DESC;

-- 4f. Find a user by email
SELECT id, name, email, phone
FROM "User"
WHERE email = 'bob@example.com';

-- 4g. Add a new user
INSERT INTO "User" (name, email, password_hash, phone)
VALUES ('Eve Martinez', 'eve@example.com', 'hash_eve_654', '555-0104');

-- 4h. Assign a role to a user
INSERT INTO "UserRole" (user_id, role_id)
SELECT u.id, r.id
FROM "User" u, "Role" r
WHERE u.email = 'eve@example.com'
  AND r.name = 'Moderator';

-- 4i. Update a user's phone number
UPDATE "User"
SET phone = '555-0199'
WHERE email = 'david@example.com';

-- 4j. Remove a role from a user
DELETE FROM "UserRole"
WHERE user_id = (SELECT id FROM "User" WHERE email = 'carol@example.com')
  AND role_id = (SELECT id FROM "Role" WHERE name = 'Moderator');

-- 4k. List users who have no roles assigned
SELECT u.id, u.name, u.email
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
WHERE ur.user_id IS NULL;

-- 4l. Delete a user (UserRole rows cascade automatically)
-- DELETE FROM "User" WHERE email = 'david@example.com';

-- ============================================================
-- 5. SELECT AND JOIN QUERIES
-- ============================================================

-- ----------------------------------------------------------
-- 5.1 Simple SELECT queries
-- ----------------------------------------------------------

-- Select specific columns for all users
SELECT name, email, created_at
FROM "User";

-- Select users created after a given date
SELECT id, name, email, created_at
FROM "User"
WHERE created_at >= '2024-03-01'
ORDER BY created_at DESC;

-- Select users with a phone number on file
SELECT id, name, phone
FROM "User"
WHERE phone IS NOT NULL;

-- Select users whose email domain is example.com
SELECT id, name, email
FROM "User"
WHERE email LIKE '%@example.com';

-- Select the first 3 users by registration date
SELECT id, name, email, created_at
FROM "User"
ORDER BY created_at ASC
LIMIT 3;

-- Select distinct role names already assigned to at least one user
SELECT DISTINCT r.name
FROM "Role" r
JOIN "UserRole" ur ON r.id = ur.role_id
ORDER BY r.name;

-- ----------------------------------------------------------
-- 5.2 INNER JOIN queries
-- ----------------------------------------------------------

-- Inner join: only users who have at least one role
SELECT u.id, u.name, u.email, r.name AS role_name
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
INNER JOIN "Role" r ON ur.role_id = r.id
ORDER BY u.name, r.name;

-- Inner join: users and roles for a specific role id
SELECT u.name AS user_name, u.email, r.name AS role_name
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
INNER JOIN "Role" r ON ur.role_id = r.id
WHERE r.id = 2;

-- Inner join with filter on both tables
SELECT u.name, u.email, r.name AS role_name
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
INNER JOIN "Role" r ON ur.role_id = r.id
WHERE u.name LIKE 'A%'
  AND r.name IN ('Admin', 'User');

-- Roles that are currently assigned to at least one user
SELECT r.id, r.name
FROM "Role" r
INNER JOIN "UserRole" ur ON r.id = ur.role_id
GROUP BY r.id, r.name
ORDER BY r.name;

-- ----------------------------------------------------------
-- 5.3 LEFT JOIN queries
-- ----------------------------------------------------------

-- Left join: all users, including those without roles
SELECT u.id, u.name, u.email, r.name AS role_name
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
LEFT JOIN "Role" r ON ur.role_id = r.id
ORDER BY u.name, r.name;

-- Left join: all roles, including roles with zero users
SELECT r.id, r.name, u.name AS assigned_user
FROM "Role" r
LEFT JOIN "UserRole" ur ON r.id = ur.role_id
LEFT JOIN "User" u ON ur.user_id = u.id
ORDER BY r.name, u.name;

-- Left join: users with no role assignment
SELECT u.id, u.name, u.email
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
WHERE ur.user_id IS NULL;

-- Left join: roles never assigned to any user
SELECT r.id, r.name
FROM "Role" r
LEFT JOIN "UserRole" ur ON r.id = ur.role_id
WHERE ur.role_id IS NULL;

-- ----------------------------------------------------------
-- 5.4 RIGHT JOIN queries
-- ----------------------------------------------------------

-- Right join: every role row, with matching users when present
SELECT u.name AS user_name, r.name AS role_name
FROM "User" u
RIGHT JOIN "UserRole" ur ON u.id = ur.user_id
RIGHT JOIN "Role" r ON ur.role_id = r.id
ORDER BY r.name, u.name;

-- ----------------------------------------------------------
-- 5.5 Multi-table JOIN with aggregation
-- ----------------------------------------------------------

-- Count how many roles each user has
SELECT u.id, u.name, COUNT(ur.role_id) AS role_count
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
GROUP BY u.id, u.name
ORDER BY role_count DESC, u.name;

-- Count users assigned to each role
SELECT r.name AS role_name, COUNT(ur.user_id) AS user_count
FROM "Role" r
LEFT JOIN "UserRole" ur ON r.id = ur.role_id
GROUP BY r.id, r.name
ORDER BY user_count DESC;

-- Users with more than one role
SELECT u.id, u.name, COUNT(ur.role_id) AS role_count
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
GROUP BY u.id, u.name
HAVING COUNT(ur.role_id) > 1
ORDER BY role_count DESC;

-- PostgreSQL: list all roles for each user in a single row
SELECT u.id, u.name, u.email,
       STRING_AGG(r.name, ', ' ORDER BY r.name) AS roles
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
LEFT JOIN "Role" r ON ur.role_id = r.id
GROUP BY u.id, u.name, u.email
ORDER BY u.name;

-- ----------------------------------------------------------
-- 5.6 JOIN with subqueries
-- ----------------------------------------------------------

-- Users who share a role with Alice
SELECT DISTINCT u.id, u.name, u.email
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
WHERE ur.role_id IN (
    SELECT ur2.role_id
    FROM "UserRole" ur2
    INNER JOIN "User" u2 ON ur2.user_id = u2.id
    WHERE u2.name = 'Alice Johnson'
)
AND u.name <> 'Alice Johnson'
ORDER BY u.name;

-- Users who have every role that Bob has
SELECT u.id, u.name
FROM "User" u
WHERE NOT EXISTS (
    SELECT br.role_id
    FROM "UserRole" br
    INNER JOIN "User" bob ON br.user_id = bob.id
    WHERE bob.email = 'bob@example.com'
    EXCEPT
    SELECT ur.role_id
    FROM "UserRole" ur
    WHERE ur.user_id = u.id
)
AND u.email <> 'bob@example.com';

-- Roles assigned to users created in Q1 2024
SELECT DISTINCT r.id, r.name
FROM "Role" r
INNER JOIN "UserRole" ur ON r.id = ur.role_id
INNER JOIN "User" u ON ur.user_id = u.id
WHERE u.created_at BETWEEN '2024-01-01' AND '2024-03-31 23:59:59'
ORDER BY r.name;

-- ----------------------------------------------------------
-- 5.7 JOIN with aliases and combined filters
-- ----------------------------------------------------------

-- Full user-role mapping with registration date
SELECT
    u.id          AS user_id,
    u.name        AS user_name,
    u.email,
    u.created_at  AS registered_at,
    r.id          AS role_id,
    r.name        AS role_name
FROM "User" u
JOIN "UserRole" ur ON u.id = ur.user_id
JOIN "Role" r ON ur.role_id = r.id
WHERE u.phone IS NOT NULL
ORDER BY u.created_at, r.name;

-- Moderators and Admins only
SELECT u.name, u.email, r.name AS role_name
FROM "User" u
INNER JOIN "UserRole" ur ON u.id = ur.user_id
INNER JOIN "Role" r ON ur.role_id = r.id
WHERE r.name IN ('Admin', 'Moderator')
ORDER BY r.name, u.name;

-- Latest registered user and their roles
SELECT u.name, u.email, u.created_at, r.name AS role_name
FROM "User" u
LEFT JOIN "UserRole" ur ON u.id = ur.user_id
LEFT JOIN "Role" r ON ur.role_id = r.id
WHERE u.created_at = (SELECT MAX(created_at) FROM "User")
ORDER BY r.name;
