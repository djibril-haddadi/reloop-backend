-- Idempotent : réattribue la clé démo à la company qui a des réservations (ou la plus fournie en stock).
-- À exécuter après chaque nouvelle résa si la clé pointait ailleurs (ex. plusieurs companies en prod).
UPDATE api_keys ak
SET company_id = COALESCE(
  (SELECT r.company_id FROM reservations r ORDER BY r.created_at DESC NULLS LAST LIMIT 1),
  (SELECT i.company_id FROM inventories i GROUP BY i.company_id ORDER BY COUNT(*) DESC LIMIT 1)
)
WHERE ak.key_hash = encode(digest('reloop-demo-key', 'sha256'), 'hex');
