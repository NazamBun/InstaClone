-- ============================================================================
-- admin-rls-fix-2026-05-10.sql
-- ============================================================================
-- Fix : RLS post_permissions empêchait les admins de lire les lignes des
-- autres users, rendant la page admin inutilisable (tous les Switch
-- s'affichaient OFF puisque permsByUser[dto.id] ?: false).
--
-- Découvert le 2026-05-10 lors du test runtime de la feature admin approval
-- (commit 8fdb399). La RPC d'écriture grant_post_permission fonctionne
-- correctement (SECURITY DEFINER bypass RLS), mais la lecture côté client
-- via select * from post_permissions est filtrée par la policy
-- post_permissions_select_own (auth.uid() = user_id seulement).
--
-- Migration APPLIQUÉE EN PROD le 2026-05-10 via mcp__supabase__apply_migration.
-- ============================================================================

-- A. Nouvelle policy SELECT pour admins
-- Permet à un user présent dans public.admins de lire toutes les lignes
-- de post_permissions. Cohabite avec post_permissions_select_own.
CREATE POLICY "post_permissions_select_admin"
  ON public.post_permissions
  FOR SELECT
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM public.admins WHERE user_id = auth.uid()
    )
  );

-- ============================================================================
-- ROLLBACK
-- ============================================================================
-- DROP POLICY IF EXISTS "post_permissions_select_admin" ON public.post_permissions;
-- ============================================================================
