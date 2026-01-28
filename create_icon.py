"""
Create icon for Monster Dagger from texture
"""

from PIL import Image
import os

# Source texture
SOURCE = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Items\Monster_Dagger_Texture.png"

# Output icon
OUTPUT_DIR = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Icons\ItemsGenerated"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Copy/convert texture as icon (64x64)
img = Image.open(SOURCE)
img = img.convert("RGBA")

# Resize to 64x64 if needed
if img.size != (64, 64):
    img = img.resize((64, 64), Image.NEAREST)

img.save(os.path.join(OUTPUT_DIR, "Monster_Dagger.png"))
print("✓ Created icon: Monster_Dagger.png")
