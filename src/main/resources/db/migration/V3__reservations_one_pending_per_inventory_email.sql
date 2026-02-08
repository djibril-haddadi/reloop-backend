-- Un seul PENDING par (inventory_id, email) pour éviter les doublons.
-- La logique service (existsByInventory_IdAndCustomerEmailIgnoreCaseAndStatus) fait le contrôle métier ;
-- cet index évite les races en base.
CREATE UNIQUE INDEX IF NOT EXISTS idx_reservations_one_pending_per_inventory_email
  ON reservations (inventory_id, LOWER(customer_email))
  WHERE status = 'PENDING';
