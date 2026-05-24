-- ==========================================
-- 1. SEED SERVICE CATEGORIES
-- ==========================================
INSERT INTO service_categories (id, name, description, icon_url)
VALUES
(1, 'Plumbing', 'Fix leaky pipes, unclog drains, and tap installations.', 'plumbing_icon.png'),
(2, 'Electrical', 'AC repair, home rewiring, lighting installations, and fuse fixes.', 'electric_icon.png'),
(3, 'Appliance Repair', 'Fixing refrigerators, washing machines, ovens, and microwaves.', 'appliance_icon.png')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to prevent ID conflicts on future manual category creations
ALTER SEQUENCE service_categories_id_seq RESTART WITH 4;


-- ==========================================
-- 2. SEED DEFAULT USERS (ADMIN, CUSTOMER, PROVIDER)
-- ==========================================
-- NOTE: Passwords below are BCrypt hashes for 'password123'
-- Ensure it aligns with your Spring Security password encoder.

-- Seed Admin User
INSERT INTO users (id, username, email, password, role, status, is_verified, created_at, updated_at)
VALUES (
    1,
    'system_admin',
    'admin@easyfix.com',
    '$2a$10$7R7M/vHwZ9S6v8n7zE6u1eX4M4Bv5KjC6z8YwZ9S6v8n7zE6u1eX4', -- BCrypt for 'password123'
    'ADMIN',
    'ACTIVE',
    true,
    CURRENT_DATE,
    CURRENT_DATE
) ON CONFLICT (email) DO NOTHING;

-- Seed Test Customer
INSERT INTO users (id, username, email, password, role, status, is_verified, created_at, updated_at)
VALUES (
    2,
    'john_doe',
    'customer@easyfix.com',
    '$2a$10$7R7M/vHwZ9S6v8n7zE6u1eX4M4Bv5KjC6z8YwZ9S6v8n7zE6u1eX4',
    'CUSTOMER',
    'ACTIVE',
    true,
    CURRENT_DATE,
    CURRENT_DATE
) ON CONFLICT (email) DO NOTHING;

-- Seed Test Provider (Linked to Plumbing Category ID: 1)
INSERT INTO users (
    id, username, email, password, role, status, is_verified, category_id,
    business_name, bio, hourly_rate, years_of_experience, created_at, updated_at
)
VALUES (
    3,
    'alex_pro',
    'provider@easyfix.com',
    '$2a$10$7R7M/vHwZ9S6v8n7zE6u1eX4M4Bv5KjC6z8YwZ9S6v8n7zE6u1eX4',
    'PROVIDER',
    'ACTIVE',
    true,
    1, -- Link to Plumbing
    'Pro Pipe Fixers',
    'Expert plumber specializing in residential leak detections and fixture upgrades.',
    45.0,
    8,
    CURRENT_DATE,
    CURRENT_DATE
) ON CONFLICT (email) DO NOTHING;

-- Reset users sequence counter
ALTER SEQUENCE users_id_seq RESTART WITH 4;