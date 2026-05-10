-- ============================================================================
-- admin-approval-2026-05-09.sql
-- ============================================================================
-- Feature : approval admin pour autoriser un user à créer un post.
-- Migration APPLIQUÉE EN PROD le 2026-05-10 via mcp__supabase__apply_migration (name: admin_approval_2026_05_09).
--
-- PROCÉDURE D'APPLY (HISTORIQUE) :
--   1. Créer un branch Supabase (mcp__supabase__create_branch ou via dashboard)
--   2. Appliquer ce SQL sur le branch
--   3. Tester le flow : login admin → grant_post_permission(target, true) →
--      le target voit son canCreatePost passer à true (via SessionManager.refresh)
--   4. Tester qu'un user NON admin appelant grant_post_permission obtient
--      une erreur NOT_ADMIN (errcode 42501)
--   5. Merge le branch sur main, puis apply prod
--
-- Modèle :
--   - public.admins : whitelist d'user_id avec privilèges admin (RLS read-only,
--     écriture exclusivement via SQL ops, pas d'API client)
--   - public.grant_post_permission(target_user_id, allow) : RPC qui upserte
--     dans post_permissions après vérification que l'appelant est admin
--   - Bootstrap : Naz seedé comme premier admin
--
-- Hors scope de ce fichier (à wirer côté Kotlin dans les commits suivants) :
--   - DTO + repository pour appeler grant_post_permission
--   - UI admin (liste users, toggle approval)
--   - Mapping erreur NOT_ADMIN → UiText
-- ============================================================================


-- A. ─────────────────────────────────────────────────────────────────────────
-- Table public.admins : whitelist des users avec privilèges admin.
-- L'écriture (INSERT/UPDATE/DELETE) n'est jamais exposée à un rôle client :
-- on ajoute/retire des admins via SQL ops uniquement.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS public.admins (
  user_id    uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE public.admins ENABLE ROW LEVEL SECURITY;

-- Un user peut voir uniquement sa propre ligne (= sait s'il est admin).
-- Sert à gater l'UI admin côté client sans exposer la liste complète.
CREATE POLICY "admins_select_self"
  ON public.admins
  FOR SELECT
  TO authenticated
  USING (auth.uid() = user_id);

-- Pas de policy INSERT / UPDATE / DELETE → bloqué par défaut pour anon
-- et authenticated. Seul le rôle postgres / service_role peut écrire
-- (la migration s'exécute justement avec ce rôle, donc le bootstrap C
-- en bas de fichier passe).


-- B. ─────────────────────────────────────────────────────────────────────────
-- RPC grant_post_permission : upsert dans post_permissions, gated par admin.
-- SECURITY DEFINER + search_path fixé (anti-hijacking, cohérent avec
-- security-hardening-2026-05-07.sql).
-- ─────────────────────────────────────────────────────────────────────────────
CREATE OR REPLACE FUNCTION public.grant_post_permission(
  target_user_id uuid,
  allow          boolean
)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = 'public', 'pg_catalog'
AS $$
BEGIN
  -- Gate : seuls les admins peuvent toggler la permission.
  IF NOT EXISTS (
    SELECT 1 FROM public.admins WHERE user_id = auth.uid()
  ) THEN
    RAISE EXCEPTION 'NOT_ADMIN' USING ERRCODE = '42501';
  END IF;

  INSERT INTO public.post_permissions (user_id, can_create_post)
  VALUES (target_user_id, allow)
  ON CONFLICT (user_id) DO UPDATE
    SET can_create_post = EXCLUDED.can_create_post;
END;
$$;

REVOKE EXECUTE ON FUNCTION public.grant_post_permission(uuid, boolean)
  FROM anon, public;

GRANT EXECUTE ON FUNCTION public.grant_post_permission(uuid, boolean)
  TO authenticated;
-- (Le check NOT_ADMIN bloquera les non-admins authentifiés en runtime ;
--  le GRANT permet juste l'appel RPC, pas l'autorisation métier.)


-- C. ─────────────────────────────────────────────────────────────────────────
-- Bootstrap : Naz comme premier admin. ON CONFLICT DO NOTHING pour rendre
-- ce script idempotent.
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO public.admins (user_id)
VALUES ('a64a3a33-4590-4b76-852f-fc0b4e9a3ae9')
ON CONFLICT (user_id) DO NOTHING;


-- ============================================================================
-- ROLLBACK
-- ============================================================================
-- À exécuter dans l'ordre inverse si besoin de rollback la feature :
--
-- DROP FUNCTION IF EXISTS public.grant_post_permission(uuid, boolean);
-- DROP TABLE IF EXISTS public.admins;
-- ============================================================================
