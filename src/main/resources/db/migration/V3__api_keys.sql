-- Clés API par entreprise (style Stripe/Twilio). X-API-KEY → companyId.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS api_keys (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  key_hash VARCHAR(64) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT uk_api_keys_key_hash UNIQUE (key_hash)
);

CREATE INDEX IF NOT EXISTS idx_api_keys_key_hash_enabled
  ON api_keys(key_hash) WHERE enabled = true;

-- Clé démo : en clair "reloop-demo-key" (à utiliser dans l'app en Mode Entreprise).
-- Hash SHA-256 pour la première company en base.
INSERT INTO api_keys (id, company_id, key_hash, enabled)
SELECT gen_random_uuid(), c.id, encode(digest('reloop-demo-key', 'sha256'), 'hex'), true
FROM companies c
LIMIT 1
ON CONFLICT (key_hash) DO NOTHING;
