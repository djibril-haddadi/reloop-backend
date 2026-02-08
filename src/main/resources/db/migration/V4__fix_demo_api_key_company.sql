-- Réattribue la clé démo à la company qui a des réservations (celle du client),
-- pour que "Mode Entreprise" affiche bien les réservations reçues.
-- Si aucune réservation : on prend la company avec le plus d'inventaires.
UPDATE api_keys ak
SET company_id = COALESCE(
  (SELECT r.company_id FROM reservations r ORDER BY r.created_at DESC NULLS LAST LIMIT 1),
  (SELECT i.company_id FROM inventories i GROUP BY i.company_id ORDER BY COUNT(*) DESC LIMIT 1)
)
WHERE ak.key_hash = encode(digest('reloop-demo-key', 'sha256'), 'hex');
