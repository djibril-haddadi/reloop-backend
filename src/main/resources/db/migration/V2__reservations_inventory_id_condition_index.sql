-- inventory_id (optionnel) : pointe la ligne d'inventaire exacte (condition + prix)
-- condition_code : snapshot condition au moment de la résa
-- Index : (company_id, status, created_at) pour listes / filtres par vendeur

ALTER TABLE reservations ADD COLUMN IF NOT EXISTS inventory_id UUID REFERENCES inventories(id);
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS condition_code VARCHAR(32);

CREATE INDEX IF NOT EXISTS idx_reservations_company_status_created
  ON reservations (company_id, status, created_at);
