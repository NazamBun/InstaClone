# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Règles de cadrage strictes

**Cette section est prioritaire sur tout le reste de ce fichier.**

1. **SCOPE RIGIDE** : tu ne fais QUE ce que je demande explicitement. Si tu identifies d'autres problèmes pendant que tu travailles, tu les LISTES dans la réponse mais tu n'agis pas dessus sans validation explicite.

2. **PLAN AVANT ACTION** : pour toute tâche qui touche plus d'un fichier, qui modifie la DB, qui fait un `git push`, ou qui dure plus de 2 minutes, tu présentes un PLAN NUMÉROTÉ d'abord. Tu attends mon « OK applique » avant d'exécuter quoi que ce soit.

3. **INTERDIT SANS DEMANDE EXPLICITE** : ne jamais
   - exécuter `apply_migration` ou `execute_sql` (write) sur Supabase
   - faire `git push` sans demande explicite
   - supprimer/modifier des données dans la DB (`DELETE`, `UPDATE`)
   - créer ou modifier des policies RLS
   - modifier la config Supabase
   - lancer un audit de sécurité étendu

4. **MCP SUPABASE READ-ONLY STRICT** : même si tu as accès à des tools d'écriture, tu ne les utilises QUE si je l'ai explicitement demandé pour cette tâche précise. « Read-only » = lecture uniquement, pas d'`apply_migration`, pas de `DELETE`, pas de `UPDATE`.

5. **COMMUNICATION HONNÊTE** : si tu fais quelque chose en dehors du scope demandé, tu le signales en début de réponse en gras avec « ⚠️ HORS SCOPE ».

6. **PAS DE SCOPE CREEP** : si je demande « vérifie X », tu vérifies X et tu t'arrêtes. Tu ne fais pas « vérifie X et tant qu'on y est j'ai aussi fait Y et Z ».

## Project

Kotlin Multiplatform app (Android + iOS) using Compose Multiplatform, backed by Supabase. Single Gradle module: `:composeApp`. Application id `com.nazam.instaclone`. Min SDK 24, target/compile SDK 36, JVM target 11.

## Common commands

```bash
# Build / run Android (debug)
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Common tests (KMP commonTest)
./gradlew :composeApp:allTests
./gradlew :composeApp:testDebugUnitTest             # Android-only unit tests
./gradlew :composeApp:testDebugUnitTest --tests "com.nazam.instaclone.SomeTest.method"

# iOS: open ./iosApp in Xcode, or build the framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Lint / static checks
./gradlew :composeApp:lintDebug
```

Gradle has `configuration-cache` and `caching` enabled — if you see unexpected build failures, try `./gradlew --no-configuration-cache <task>` to rule it out.

## Secrets

Supabase URL + anon key are **not** committed. They are read per-platform:

- **Android**: `local.properties` (root) → exposed via `BuildConfig.SUPABASE_URL` / `SUPABASE_ANON_KEY`. Wired in `composeApp/build.gradle.kts` (top of file).
- **iOS**: `iosApp/Secrets.xcconfig` (gitignored; `Secrets.sample.xcconfig` is the template) → injected into `Info.plist` → read by `SupabaseSecrets.ios.kt` via `NSBundle.mainBundle.objectForInfoDictionaryKey`.

The `commonMain` `expect object SupabaseSecrets` has no values — never put real keys there. If a build fails because a secret is missing, the user needs to populate `local.properties` / `Secrets.xcconfig`, not change source.

## Architecture

### Source set layout

- `composeApp/src/commonMain/kotlin/com/nazam/instaclone/` — shared code (the bulk of the app).
- `composeApp/src/androidMain/` — Android `MainActivity`, `androidPlatformModule(context)` Koin module, Android `actual` for `expect` declarations (image picker, share, clipboard, `SupabaseSecrets`, `ImageBytesReader`).
- `composeApp/src/iosMain/` — `MainViewController()`, iOS `actual` impls, `SupabaseSecrets` from Info.plist.
- `composeApp/src/commonMain/composeResources/values/strings.xml` — translatable strings exposed via the generated `Res.string.*` accessors. UI strings should go through `UiText` (see below), not raw `String`.

### Feature modules (clean architecture)

Each feature under `feature/<name>/` follows three layers:

- `data/` — DTOs (`@Serializable`, kotlinx.serialization), mappers, and repository implementations that talk to `SupabaseClient` (Postgrest / Storage / Auth).
- `domain/` — pure Kotlin: `model/`, `repository/` interfaces, `usecase/` classes (one class per use case, suspend `execute(...)`).
- `presentation/` — `viewmodel/` (extends `androidx.lifecycle.ViewModel`, uses `viewModelScope`), `ui/` Composables (`*Route` + `*Screen` split), `model/` UI state, plus optional `validator/`, `mapper/`, `handler/`.

Existing features: `auth`, `home` (feed, vote, create post, explore, categories, comments), `profile` (incl. edit), `notifications`, `permissions`. Note that the bottom nav and several screens (`Categories`, `CreatePostType`, `Explore`, `CreatePost`) currently live under `feature/home/presentation/ui/` even though they are conceptually their own areas — follow the existing placement rather than relocating them ad hoc.

### DI (Koin)

- `core/di/InitKoin.kt` — `initKoin(vararg extraModules)` is idempotent (catches `KoinApplicationAlreadyStartedException`).
- `core/di/SupabaseModule.kt` — the single `appModule` binding everything (repositories `single`, use cases `factory`, view models `viewModel { }`).
- Android entry: `MainActivity.onCreate` calls `initKoin(androidPlatformModule(this))` to inject the `Context`-bound `AndroidImageBytesReader`. iOS entry: `iOSApp.init()` (Swift) calls `InitKoinIosKt.doInitKoinIos()`, which is defined in `iosMain/.../core/di/InitKoinIos.kt` and calls `initKoin(iosPlatformModule())` to bind `IosImageBytesReader`. Both platforms initialize Koin eagerly at app startup with their platform module merged into `appModule`.
- New ViewModels must be registered in `appModule` and resolved with `koinInject()` / `koinViewModel()` from Compose. `factory` is used for short-lived VMs (auth flows); `viewModel { }` for long-lived screen VMs.

### Navigation

There is no Compose Navigation / Voyager yet. `App.kt` holds a `Screen` enum and a `when` over `currentScreen`, plus:

- `core/navigation/NavigationStore` — a global object with `setAfterLogin` / `consumeAfterLogin` / `setAuthReturnIfEmpty`. The auth flow uses it to bounce the user back after login/signup.
- `feature/profile/presentation/navigation/ProfileTargetStore` — selects whether `ProfileRoute` shows self vs. visited profile.
- `App.kt` decides which screens are auth-protected (`isProtected(...)`) and redirects to `Screen.Login` if `SessionManager.user.value == null`.

When adding a screen: extend the `Screen` enum, add a branch in `AppNavHost`, and (if protected) include it in the `isProtected` set.

### Session & permissions

`core/session/SessionManager` is the single source of truth for `AuthUser?`. `DefaultSessionManager.refresh()` calls `GetCurrentUserUseCase`, then **enriches** the user with `GetPostPermissionUseCase` to populate `canCreatePost`. `AuthRepositoryImpl` deliberately leaves `canCreatePost = false`; never compute permissions inside an auth repo. UI gates posting through `core/access/CreatePostAccess.canCreate(user)`.

After login/signup VMs must call `sessionManager.setUser(user)` (also enriches permissions) before navigating.

### Errors & user-facing strings

- Repository methods return `Result<T>` and use `core/utils/SafeCall.safeCall { ... }`. `safeCall` re-throws `CancellationException` (do not swallow it) and logs everything else. Reuse it instead of writing raw try/catch around suspending Supabase calls.
- Surface errors via `core/ui/UiText` (`Resource(StringResource)`, `ResourceArgs`, or `DynamicString`). Map Supabase exceptions to `UiText` via mappers like `feature/auth/data/error/AuthErrorMapper` — match on `error.message?.lowercase()` substrings, fall back to `error_unknown`. New error mappers should follow the same `object` + `map(Throwable): UiText` shape.

### Dispatchers

`core/dispatchers/AppDispatchers` is the abstraction; `DefaultAppDispatchers` maps `io` to `Dispatchers.Default` because `Dispatchers.IO` is unavailable on iOS. Always inject `AppDispatchers` rather than referencing `Dispatchers` directly, and use `withContext(dispatchers.io)` for repository / network calls.

### Platform `expect`/`actual` surface

When adding cross-platform behavior that needs platform APIs, mirror the existing pattern: `commonMain` declares `expect`, `androidMain` and both iOS source sets (`iosArm64Main`, `iosSimulatorArm64Main`) provide `actual`. Current expectations: `SupabaseSecrets`, `ImageBytesReader`, `ImagePicker`, `ShareLauncher`, `ShareCardRenderer`, `ClipboardManager`, `Platform`, `NetworkImage` (Compose component).

## Conventions

- Inline comments and log lines mix French and English — match the surrounding file's language; don't rewrite existing French comments.
- ViewModels use `androidx.lifecycle.ViewModel` + `viewModelScope` (the multiplatform variant from `org.jetbrains.androidx.lifecycle`). Don't introduce a custom `viewModelScope`.
- Type-safe project accessors are enabled (`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`). Reference modules as `projects.composeApp` if you ever add a sibling module.
- Version catalog: edit `gradle/libs.versions.toml` for any new dependency; don't pin versions inline in `build.gradle.kts` unless mirroring an existing exception (e.g. the Supabase artifacts and `ucrop` are referenced as raw strings because they aren't in the catalog yet).

## Style de travail avec Naz

- Réponses en français.
- Style direct, sans flatterie ni excès de politesse.
- Toujours expliquer le « pourquoi » d'une décision technique.
- Demander confirmation avant changements majeurs (refactor large, suppressions, modifications RLS).
- Tous les fichiers ≤ 150 lignes (objectif strict).
- Toujours fournir des fichiers complets, jamais de snippets partiels (sauf petite édition ciblée).
- Format de commits : Conventional Commits (`feat` / `fix` / `refactor` / `chore` / `docs`).
- **Commits git** : ne JAMAIS ajouter de signature `Co-Authored-By: Claude` ni de marqueur `🤖 Generated with Claude Code`. Les commits doivent ressembler à des commits humains classiques, format Conventional Commits uniquement.
- Ne JAMAIS modifier `local.properties` ni `iosApp/Secrets.xcconfig`.
- Pour toute modification de policies RLS Supabase ou de schéma DB : demander confirmation avant exécution.

## Bugs connus / TODO

- **iOS** : initialisation Koin via `doInitKoinIos()` depuis `iOSApp.init()` (Swift) — symétrique d'`androidPlatformModule(this)` passé depuis `MainActivity` sur Android. `IosImageBytesReader` est bindé mais `readBytes()` est encore un placeholder qui throw — l'upload d'image plantera clairement à l'usage. Test runtime iOS à faire dans une session avec Xcode/simulateur.
- Build `release` non minifié (`isMinifyEnabled = false`, R8 désactivé).
- Stores `object` globaux (`NavigationStore`, `ProfileTargetStore`, `NotificationsBadgeStore`) → migrer en singletons Koin.
- Coil + Kamel sont tous les deux dans les deps → en choisir un seul.
- `iosArm64Main` + `iosSimulatorArm64Main` dupliquent leurs deps → factoriser via `iosMain`.
- `AppDispatchers.io = Dispatchers.Default` → vrai `expect`/`actual` nécessaire pour utiliser `Dispatchers.IO` sur Android.
- Migration vers Voyager / Decompose prévue (`App.kt` fait du `when` manuel sur `Screen`).
- Confirm email **désactivé** côté Supabase (dev only) — à réactiver pour la prod.
- Rate limit Supabase mappé via `error_auth_rate_limit` dans `AuthErrorMapper`.
- `ProfileRoute.kt` utilise `LaunchedEffect(Unit) { vm.load() }` (workaround pour le cache `koinViewModel` quand on revient sur Profile après Edit).
