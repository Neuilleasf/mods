# Grand Résumé du Projet MonsterDaggers

Ce document synthétise les exigences critiques, les standards de qualité ("La Bible") et les directives techniques extraites de l'historique de conversation et des fichiers logs.

## 1. Contexte & Philosophie "Pro"
L'objectif est de produire un mod **Hytale natif** de qualité professionnelle ("Quali et Pro").
*   **Référence Absolue** : Le mod doit suivre strictement l'architecture de `Mars_Lighter_Sword`.
*   **Règle d'Or** : Ne pas coder en dur ce qui peut être fait via les fichiers de configuration (JSON).
*   **Structure** : Utilisation intensive de l'héritage (`Parent`) et des variables d'interaction (`InteractionVars`) pour garder le code propre et maintenable.

## 2. État Actuel & Problèmes Signalés
Le mod `MonsterDaggers` est actuellement en cours de développement mais présente des lacunes critiques signalées par l'utilisateur.

### A. Le Système de Lock (Priorité Absolue)
*   **Constat** : "La compétence lock ne marche pas, il n'y a pas de système de lock."
*   **Exigence** : Il faut un système de **ciblage robuste** codé en Java.
*   **Technique** : Remplacer la logique simpliste actuelle par un **Raycast** ou un **Cone Check** (Vérification en cône devant le joueur) pour verrouiller la cible regardée, et non juste la plus proche.

### B. La Compétence Signature (Ultimate)
*   **Concept** : "Je veux une TP etc avec les clones."
*   **Mécanique Attendue** :
    1.  **Séquence de Téléportation Rapide** : Le joueur se téléporte successivement **Derrière -> Droite -> Gauche -> Devant** la cible verrouillée.
    2.  **Effet de Confusion (Clones)** : À chaque téléportation, un **Clone** (Afterimage) reste figé à la position où le joueur vient de frapper pour désorienter l'adversaire.
    3.  L'animation et le feeling doivent être impactants (VFX/SFX).

## 3. Standards Techniques Extraits ("Habitudes à prendre")
D'après l'analyse des logs (`62b8c...json`), voici les règles de structure à respecter impérativement :

### Structure des Dossiers
Les assets et data doivent suivre cette hiérarchie précise pour être chargés correctement par le jeu :
```text
Mods.MonsterDaggers/
├── src/main/java/... (Code Logique pur : Lock, Events)
└── Assets/
    └── [NomDuMod]/
        ├── Textures/
        └── VFX/
└── Server/
    ├── Item/
    │   └── Interactions/ (Définition des attaques en JSON)
    ├── Effects/
    └── Abilities/
```

### Conventions JSON
*   **IDs Uniques** : Utiliser le préfixe du mod (ex: `monster_daggers:ability_signature`).
*   **Héritage** : Toujours hériter des templates de base (ex: ` "parent": "hytale:base_dagger"`) pour éviter la redondance.

## 4. Feuille de Route Immédiate
1.  **Réparation du Cerveau (Java)** : Réécrire `MonsterDaggersPlugin.java` pour implémenter un vrai système de Lock (Cone Target).
2.  **Implémentation des Muscles (JSON)** : Créer les fichiers d'interaction pour l'attaque Signature (Teleport + Spawn Entity/Clone).
3.  **Finitions (Assets)** : S'assurer que les VFX/SFX sont bien reliés.

---
*Document généré par GitHub Copilot le 28 Janvier 2026 suite à l'analyse de l'historique complet.*
