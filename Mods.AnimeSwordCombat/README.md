# ⚔️ AnimeSwordCombat

## Combat d'épée style ANIME pour Hytale

> *"Unleash your inner samurai!"*

---

## ✨ Fonctionnalités

### 🔥 Système de Combo (10 hits pour le FINISHER)

| Combo | Attaque | Dégâts | Effets Visuels |
|-------|---------|--------|----------------|
| 1-2 | Light Slash | x1.0 | Étincelles bleues basiques |
| 3-4 | Swift Strike | x1.15 | Idem + UI combo verte |
| 5-6 | **Heavy Cleave** | x1.3 | Flash jaune + shockwave + screen effect |
| 7-9 | **Critical Slash** | x1.5 | Flash orange/or + lignes de slash + tint entity |
| 10+ | **★ FINISHER ★** | x2.5 | MEGA explosion + flash noir/blanc + multi-ring |

### ⚡ Impact Frames Style Anime
- **Heavy**: Flash blanc rapide (style Naruto)
- **Critical**: Flash orange + entity tint dorée (style Demon Slayer)
- **Finisher**: Séquence multi-flash blanc → noir → feu (style Dragon Ball Z)

### 🌟 Particules (basées sur le jeu vanilla, amplifiées)
Utilise les textures existantes du jeu :
- `Particles/Textures/Basic/Shape5.png` - Flash central
- `Particles/Textures/Basic/Spark.png` - Étincelles
- `Particles/Textures/Circles/Shockwave2.png` & `Shockwave3.png` - Anneaux
- `Particles/Textures/Impacts/Bash_Trail_Straight.png` - Lignes de slash

**Couleurs par niveau :**
- Light: Blanc → Bleu (#ffffff → #6699ff)
- Heavy: Jaune → Orange (#ffff00 → #ff6600)
- Critical: Or → Orange (#ffcc00 → #ff4400)
- Finisher: Blanc → Noir → Rouge/Or

### 📺 Screen Effects
Utilise les effets du jeu vanilla :
- `ScreenEffects/Immune.png` - Flash blanc
- `ScreenEffects/Fire.png` - Tint orange/rouge

---

## 📁 Structure du mod

```
Mods.AnimeSwordCombat/
├── manifest.json
├── build.gradle
├── src/main/java/com/nakai/animesword/
│   ├── AnimeSwordPlugin.java      # Plugin principal + Events
│   ├── AttackType.java            # Enum des types d'attaques
│   ├── ComboSystem.java           # Logique des seuils de combo
│   ├── ComboTracker.java          # Suivi combo par joueur
│   ├── ImpactFrameManager.java    # Effets d'écran anime
│   ├── ParticleVFXManager.java    # Spawn des particules
│   └── SlashTrailManager.java     # Trails de slash
└── Assets/Server/
    ├── Particles/AnimeSword/
    │   ├── Impact_Light.particlesystem
    │   ├── Impact_Heavy.particlesystem
    │   ├── Impact_Critical.particlesystem
    │   ├── Impact_Finisher.particlesystem
    │   └── Spawners/
    │       └── [18 spawners avec couleurs custom]
    └── Entity/Effects/
        ├── AnimeSword_Impact_Heavy.json
        ├── AnimeSword_Impact_Critical.json
        └── AnimeSword_Impact_Finisher.json
```

---

## 🎮 Comment jouer

1. **Équipe une épée** (tout item contenant "sword", "blade", ou "katana")
2. **Frappe un ennemi** → Combo démarre
3. **Continue dans les 2 secondes** → Combo augmente
4. **Atteins x10** → 💥 **FINISHER** avec 2.5x dégâts !

---

## 🔧 Build

```powershell
cd Mods.AnimeSwordCombat
.\gradlew clean build
```

Le JAR sera dans `build/libs/AnimeSwordCombat-1.0.0.jar`

---

## 🚀 Installation

```powershell
Copy-Item "build\libs\AnimeSwordCombat-1.0.0.jar" "$env:APPDATA\Hytale\UserData\Mods\"
```

---

## ⚙️ Personnalisation

### Changer les seuils de combo
[ComboSystem.java](src/main/java/com/nakai/animesword/ComboSystem.java) :
```java
private static final int FINISHER_THRESHOLD = 10;  // Réduire pour finisher plus rapide
```

### Changer les dégâts
[AttackType.java](src/main/java/com/nakai/animesword/AttackType.java) :
```java
FINISHER("ULTIMATE FINISHER", 2.5f, ...);  // Modifier le multiplicateur
```

### Changer les couleurs des particules
Modifier les fichiers `.particlespawner` dans `Assets/Server/Particles/AnimeSword/Spawners/`
```json
"Color": "#ff4400"  // Format hex
```

---

## 🎬 Effets détaillés

### Finisher Sequence (250ms total)
```
0ms   → MegaFlash blanc (énorme)
50ms  → BlackFrame (écran noir 50ms) ← C'EST ÇA L'IMPACT FRAME ANIME
100ms → WhiteFlash retour
150ms → Ring1, Ring2, Ring3 en cascade
200ms → Shockwave géante + StarBurst radial
```

C'est cette alternance rapide blanc→noir→blanc qui donne le feeling "manga panel" !

---

**Made with ⚔️ by Nakai**
