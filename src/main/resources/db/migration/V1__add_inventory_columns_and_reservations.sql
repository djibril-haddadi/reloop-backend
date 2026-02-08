-- Migration delta : colonnes inventories + table reservations.
-- Prérequis : tables companies, components, inventories déjà présentes (créées par Hibernate ou manuellement).
-- Idempotent : safe si colonnes/table existent déjà.

-- 1) Colonnes sur inventories (défaut 0 pour price_cents pour backfill, pas 50€)
ALTER TABLE inventories ADD COLUMN IF NOT EXISTS price_cents INT NOT NULL DEFAULT 0;
ALTER TABLE inventories ADD COLUMN IF NOT EXISTS condition_code VARCHAR(32) NOT NULL DEFAULT 'USED';
ALTER TABLE inventories ADD COLUMN IF NOT EXISTS available BOOLEAN NOT NULL DEFAULT true;

-- 2) Backfill : available jamais NULL (avant de simplifier le filtre repo en "available = true")
UPDATE inventories SET available = true WHERE available IS NULL;
UPDATE inventories SET price_cents = 0 WHERE price_cents IS NULL;

-- 3) Contrainte métier : quantity >= 0
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_inventory_quantity_non_negative'
  ) THEN
    ALTER TABLE inventories ADD CONSTRAINT chk_inventory_quantity_non_negative CHECK (quantity >= 0);
  END IF;
END $$;

-- 4) Unicité (company, component, condition)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'uk_inventories_company_component_condition'
  ) THEN
    ALTER TABLE inventories ADD CONSTRAINT uk_inventories_company_component_condition
      UNIQUE (company_id, component_id, condition_code);
  END IF;
END $$;

-- 5) Table reservations
CREATE TABLE IF NOT EXISTS reservations (
  id UUID PRIMARY KEY,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  status VARCHAR(32) NOT NULL,
  company_id UUID NOT NULL REFERENCES companies(id),
  component_id UUID NOT NULL REFERENCES components(id),
  quantity INT NOT NULL CHECK (quantity >= 0),
  customer_name VARCHAR(255) NOT NULL,
  customer_email VARCHAR(255) NOT NULL,
  reserved_price_cents INT NOT NULL
);
