-- ============================================================================
-- post-type-2026-05-12.sql
-- ============================================================================
-- Préparation du flow de création YES_NO.
--
-- Ajoute une colonne discriminante post_type à public.posts (default 'DUEL'
-- pour assurer la rétrocompatibilité avec les rows existantes), une CHECK
-- constraint pour limiter aux valeurs 'DUEL' et 'YES_NO', et recrée les
-- 2 vues posts_feed et posts_following_feed pour exposer le nouveau champ.
--
-- Le code Kotlin existant (HomeRepositoryImpl, PostDto, PostMapper) ne
-- référence pas post_type côté lecture, donc l'ajout d'une colonne en fin
-- de SELECT des vues est rétrocompatible avec le flow DUEL.
--
-- Migration APPLIQUÉE EN PROD le 2026-05-12 via mcp__supabase__apply_migration
-- (name: post_type_2026_05_12).
-- ============================================================================

-- A. Nouvelle colonne discriminante avec default DUEL
ALTER TABLE public.posts
  ADD COLUMN post_type text NOT NULL DEFAULT 'DUEL';

-- B. CHECK constraint qui limite les valeurs autorisées
ALTER TABLE public.posts
  ADD CONSTRAINT posts_post_type_chk
  CHECK (post_type IN ('DUEL', 'YES_NO'));

-- C. Recréer posts_feed pour exposer post_type
-- (CREATE OR REPLACE VIEW autorise l'ajout de colonnes en fin de SELECT)
CREATE OR REPLACE VIEW public.posts_feed AS
SELECT p.id,
    p.created_at,
    p.question,
    p.category,
    p.author_id,
    COALESCE(pr.display_name, p.author_name) AS author_name,
    COALESCE(pr.avatar_url, p.author_avatar) AS author_avatar,
    p.left_image,
    p.right_image,
    p.left_label,
    p.right_label,
    p.left_votes,
    p.right_votes,
    p.voted_left_ids,
    p.voted_right_ids,
    p.vs_badge_id,
    v.choice AS user_choice,
    count(c.id)::integer AS comments_count,
    p.post_type
FROM posts p
    LEFT JOIN profiles pr ON pr.id = p.author_id
    LEFT JOIN votes v ON v.post_id = p.id AND v.user_id = auth.uid()
    LEFT JOIN comments c ON c.post_id = p.id
GROUP BY p.id, p.created_at, p.question, p.category, p.author_id,
    pr.display_name, p.author_name, pr.avatar_url, p.author_avatar,
    p.left_image, p.right_image, p.left_label, p.right_label,
    p.left_votes, p.right_votes, p.voted_left_ids, p.voted_right_ids,
    p.vs_badge_id, v.choice, p.post_type
ORDER BY p.created_at DESC;

-- D. Recréer posts_following_feed pour exposer post_type
CREATE OR REPLACE VIEW public.posts_following_feed AS
SELECT p.id,
    p.created_at,
    p.question,
    p.category,
    p.author_id,
    COALESCE(pr.display_name, p.author_name) AS author_name,
    COALESCE(pr.avatar_url, p.author_avatar) AS author_avatar,
    p.left_image,
    p.right_image,
    p.left_label,
    p.right_label,
    p.left_votes,
    p.right_votes,
    p.voted_left_ids,
    p.voted_right_ids,
    p.vs_badge_id,
    v.choice AS user_choice,
    count(c.id)::integer AS comments_count,
    p.post_type
FROM posts p
    LEFT JOIN profiles pr ON pr.id = p.author_id
    LEFT JOIN votes v ON v.post_id = p.id AND v.user_id = auth.uid()
    LEFT JOIN comments c ON c.post_id = p.id
WHERE p.author_id = auth.uid()
    OR p.author_id IN (
        SELECT f.following_id
        FROM follows f
        WHERE f.follower_id = auth.uid()
    )
GROUP BY p.id, p.created_at, p.question, p.category, p.author_id,
    pr.display_name, p.author_name, pr.avatar_url, p.author_avatar,
    p.left_image, p.right_image, p.left_label, p.right_label,
    p.left_votes, p.right_votes, p.voted_left_ids, p.voted_right_ids,
    p.vs_badge_id, v.choice, p.post_type
ORDER BY p.created_at DESC;

-- ============================================================================
-- ROLLBACK
-- ============================================================================
-- DROP VIEW IF EXISTS public.posts_following_feed;
-- DROP VIEW IF EXISTS public.posts_feed;
-- ALTER TABLE public.posts DROP CONSTRAINT IF EXISTS posts_post_type_chk;
-- ALTER TABLE public.posts DROP COLUMN IF EXISTS post_type;
-- -- Puis recréer les 2 vues sans p.post_type depuis leur définition d'origine
-- -- (cf. git show <commit avant migration> ou pg_get_viewdef historique).
-- ============================================================================
