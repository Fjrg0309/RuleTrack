-- Eliminar NOT NULL de codigo_organizacion si existe (columna legacy)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'usuarios'
          AND column_name = 'codigo_organizacion'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE usuarios ALTER COLUMN codigo_organizacion DROP NOT NULL;
    END IF;
END $$;
