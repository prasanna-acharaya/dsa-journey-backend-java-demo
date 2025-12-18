-- Create DSA related tables

CREATE TABLE IF NOT EXISTS ids_dsa (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    unique_code VARCHAR(50) UNIQUE NOT NULL,
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    city VARCHAR(100),
    constitution VARCHAR(100),
    registration_date DATE,
    gstin VARCHAR(50),
    pan VARCHAR(20),
    empanelment_date DATE,
    agreement_date DATE,
    zone_mapping VARCHAR(100),
    agreement_period VARCHAR(50),
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    address_line_1 VARCHAR(255),
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dsa_products (
    dsa_id UUID NOT NULL,
    product_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (dsa_id) REFERENCES ids_dsa(id)
);

CREATE TABLE IF NOT EXISTS dsa_bank_details (
    id UUID PRIMARY KEY,
    dsa_id UUID NOT NULL,
    account_name VARCHAR(255),
    account_number VARCHAR(50),
    ifsc_code VARCHAR(20),
    branch_name VARCHAR(255),
    FOREIGN KEY (dsa_id) REFERENCES ids_dsa(id)
);

CREATE TABLE IF NOT EXISTS dsa_documents (
    id UUID PRIMARY KEY,
    dsa_id UUID NOT NULL,
    document_name VARCHAR(255),
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    upload_time TIMESTAMP,
    FOREIGN KEY (dsa_id) REFERENCES ids_dsa(id)
);
