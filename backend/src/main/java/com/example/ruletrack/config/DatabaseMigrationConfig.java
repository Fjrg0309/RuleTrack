package com.example.ruletrack.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseMigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    @Bean
    ApplicationRunner applyLegacyMigrations(JdbcTemplate jdbc) {
        return args -> {
            // 1. Eliminar NOT NULL de codigo_organizacion si existe con esa restricción
            try {
                Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns " +
                    "WHERE table_schema = 'public' AND table_name = 'usuarios' " +
                    "AND column_name = 'codigo_organizacion' AND is_nullable = 'NO'",
                    Integer.class
                );
                if (count != null && count > 0) {
                    jdbc.execute("ALTER TABLE usuarios ALTER COLUMN codigo_organizacion DROP NOT NULL");
                    log.info("Migration: codigo_organizacion NOT NULL constraint removed.");
                }
            } catch (Exception e) {
                log.warn("Migration warning (codigo_organizacion): {}", e.getMessage());
            }

            // 2. Actualizar constraint CHECK de rol para incluir ORGANIZADOR y USUARIO
            try {
                jdbc.execute(
                    "DO $$ BEGIN " +
                    "  IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'usuarios_rol_check') THEN " +
                    "    ALTER TABLE usuarios DROP CONSTRAINT usuarios_rol_check; " +
                    "  END IF; " +
                    "  ALTER TABLE usuarios ADD CONSTRAINT usuarios_rol_check " +
                    "    CHECK (rol IN ('ORGANIZADOR', 'USUARIO', 'ADMIN', 'EDITOR', 'VIEWER')); " +
                    "END $$"
                );
                log.info("Migration: usuarios_rol_check constraint updated with current Rol enum values.");
            } catch (Exception e) {
                log.warn("Migration warning (usuarios_rol_check): {}", e.getMessage());
            }
        };
    }
}
