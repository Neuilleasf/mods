# CONTEXTE MOD: Mods.MonsterDaggers
**Date:** 28 Janvier 2026
**Auteur:** Kiwi (Utilisateur) & Assistant

## 1. Description du Mod
*   **Nom** : MonsterDaggers
*   **Thème** : Dagues légendaires style "Monstre/Bête" (Bestial/Assassin).
*   **Esthétique** : Rouge sombre, noir, effets de rage.
*   **Inspiration** : Style "Shadowstep" / "Lighter Sword" mais version Monster.

## 2. Compétences & Mécaniques (MANDATAIRES)

### A. Primary: Beast Frenzy
*   Combo de 4 coups rapides.
*   Monte une jauge de rage.

### B. Secondary: Savage Counter
*   Posture défensive -> Contre-attaque.

### C. Signature: Rampage / Shadow Clone Assault (CRUCIAL)
*   **Description** : L'utilisateur **se téléporte (TP)** sur la cible verrouillée.
*   **Effet Spécial** : Création de **CLONES (Shadow Clones)** qui attaquent la cible sous différents angles simultanément ou en séquence rapide (ex: derrière, gauche, droite).
*   **Séquence** : TP Initial -> Clones frappent -> Explosion finale.
*   **État Actuel** : Le code fait des TP (`performComboStep` avec offsets) mais la mécanique de "Clones" (visuelle ou entités) manque ou est invisible. L'utilisateur insiste sur "TP + Clones".

## 3. Problème Technique MAJEUR (À FIXER EN PREMIER)
**Le système de "Lock" (Verrouillage de cible) ne fonctionne pas.**
*   **Symptôme** : "La compétence lock marche pas, y a pas de système de lock".
*   L'utilisateur a besoin d'un verrouillage fiable pour que la Signature puisse trouver sa cible (TP + Clones).
*   Sans lock, la Signature tape dans le vide.

## 4. Fichiers & Structure
*   `MonsterDaggersPlugin.java` : Contient la logique. Actuellement, `findNearestTarget` semble simpliste ou cassé. La boucle `updateLockOns` existe mais ne semble pas satisfaire l'utilisateur.
*   `create_monster_dagger.py` : Scripts assets (OK).

## 5. Instructions pour l'IA Suivante
1.  **FIXER LE LOCK** : Implémenter un Raycast ou un Cone check correct pour verrouiller la cible regardée (pas juste "la plus proche" dans le dos). Ajouter un feedback visuel si possible (particule sur la cible lockée).
2.  **Vérifier la Signature** : S'assurer que le code de la Signature utilise bien ce Lock et implémente la logique de **TP + CLONES**.

**NOTE UTILISATEUR** : "Je veux une TP etc avec les clones".
