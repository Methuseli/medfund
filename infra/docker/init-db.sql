-- Initialize schemas for Keycloak and audit
CREATE SCHEMA IF NOT EXISTS keycloak;
CREATE SCHEMA IF NOT EXISTS audit;

-- Create audit database for Go audit service
CREATE DATABASE medfund_audit;
