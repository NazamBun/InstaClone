-- ============================================================================
-- security-hardening-2026-05-07.sql
-- ============================================================================
-- Audit `get_advisors security` du 2026-05-07 sur le projet ushaekvefuarbqsesaut.
-- Migration NON APPLIQUÉE — à exécuter dans une session future après test
-- complet sur un branch Supabase et validation runtime côté client.
--
-- Avant d'appliquer :
--   1. Créer un branch Supabase (mcp__supabase__create_branch ou via dashboard)
--   2. Appliquer ce SQL sur le branch
--   3. Re-tester le flow signup, vote, feed, post creation côté app
--   4. Re-run get_advisors security pour vérifier qu'il revient propre
--   5. Merge le branch sur main
--
-- Findings d'origine (8 lints) :
--   - 2 ERROR : posts_feed et posts_following_feed en SECURITY DEFINER (bypass RLS)
--   - 1 WARN  : handle_new_user() sans search_path fixé (risque hijacking)
--   - 4 WARN  : SECURITY DEFINER functions exposées en RPC à anon/authenticated
--   - 1 WARN  : storage bucket posts-images permet le listing public
--   + 1 WARN auth : Leaked Password Protection désactivée
--     (à activer manuellement : Dashboard > Authentication > Settings >
--      Password Strength > "Prevent use of leaked passwords")
--
-- Faille additionnelle découverte hors lint :
--   - consume_invite(text) appelable par anon SANS vérification d'auth :
--     un attaquant peut marquer une invite légitime comme USED → DOS sur
--     les invites. Fix : déplacer la consommation dans le trigger
--     handle_new_user (ne s'exécute qu'après création réelle de l'user).
--
-- Vérifié côté code Kotlin : aucun appel client à consume_invite() ni
-- can_signup() (grep sur composeApp/src). Le passage des vues en
-- security_invoker n'introduit pas de régression (joints sur posts/profiles
-- en SELECT public, votes en SELECT propre user — comportement identique).
-- ============================================================================


-- A. ─────────────────────────────────────────────────────────────────────────
-- Vues : SECURITY DEFINER → SECURITY INVOKER
-- Les RLS de l'appelant s'appliquent désormais. Aucune régression attendue
-- (tables jointes ont des policies cohérentes).
-- ─────────────────────────────────────────────────────────────────────────────
ALTER VIEW public.posts_feed           SET (security_invoker = on);
ALTER VIEW public.posts_following_feed SET (security_invoker = on);


-- B. ─────────────────────────────────────────────────────────────────────────
-- handle_new_user() : fixer search_path + intégrer la consommation d'invite
-- (élimine le DOS de consume_invite et le risque de search_path hijacking).
-- ─────────────────────────────────────────────────────────────────────────────
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = 'public', 'pg_catalog'
AS $$
BEGIN
  INSERT INTO public.profiles (id, display_name, avatar_url)
  VALUES (
    new.id,
    coalesce(new.raw_user_meta_data->>'display_name', split_part(new.email, '@', 1)),
    coalesce(new.raw_user_meta_data->>'avatar_url', null)
  )
  ON CONFLICT (id) DO NOTHING;

  -- Consomme l'invite ssi un user vient réellement d'être créé.
  UPDATE public.invites
  SET status = 'USED', used_at = now()
  WHERE lower(email) = lower(new.email) AND status = 'PENDING';

  RETURN new;
END;
$$;


-- C. ─────────────────────────────────────────────────────────────────────────
-- REVOKE EXECUTE sur les fonctions SECURITY DEFINER qui ne devraient pas
-- être exposées via /rest/v1/rpc/... aux rôles indiqués.
-- ─────────────────────────────────────────────────────────────────────────────
REVOKE EXECUTE ON FUNCTION public.handle_new_user()
  FROM anon, authenticated, public;
-- (le trigger l'invoque toujours — un trigger ignore les GRANT/REVOKE)

REVOKE EXECUTE ON FUNCTION public.consume_invite(text)
  FROM anon, authenticated, public;
-- (la logique est désormais dans handle_new_user ; on garde la fonction au
--  cas où elle serait référencée par un script ops, mais plus appelable)

REVOKE EXECUTE ON FUNCTION public.vote_post(uuid, uuid, text)
  FROM anon;
-- (un anon échouait déjà avec AUTH_REQUIRED en interne, on aligne le contrat)

REVOKE EXECUTE ON FUNCTION public.can_signup(text)
  FROM authenticated;
-- (un user déjà connecté n'a pas à check si une invite existe)


-- D. ─────────────────────────────────────────────────────────────────────────
-- Storage : retirer la policy SELECT large (qui permet le listing) et
-- supprimer la policy INSERT en doublon. Les URLs publiques continueront
-- de fonctionner (elles passent par /object/public/... qui n'utilise pas
-- la policy SELECT).
-- ─────────────────────────────────────────────────────────────────────────────
DROP POLICY IF EXISTS "allow_read_posts_images 11z1voy_0"
  ON storage.objects;

DROP POLICY IF EXISTS "allow_authenticated_upload_posts_images 11z1voy_0"
  ON storage.objects;
-- (la policy "Allow authenticated users to upload images" reste — c'est la
--  même règle, donc pas de régression sur les uploads)


-- ============================================================================
-- Étape manuelle (Dashboard, pas en SQL) :
--   Authentication > Settings > "Prevent use of leaked passwords" → ON
-- ============================================================================
