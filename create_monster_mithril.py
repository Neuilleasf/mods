"""
Create Monster Dagger from Mithril base
Better texture and icon
"""

from PIL import Image
import os
import shutil

# Source: Mithril dagger
SOURCE_TEXTURE = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Items\Weapons\Dagger\Mithril_Texture.png"
SOURCE_MODEL = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Items\Weapons\Dagger\Mithril.blockymodel"
SOURCE_ICON = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Icons\ItemsGenerated\Weapon_Daggers_Mithril.png"

# Output
OUTPUT_ITEMS = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Items"
OUTPUT_ICONS = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Icons\ItemsGenerated"
os.makedirs(OUTPUT_ITEMS, exist_ok=True)
os.makedirs(OUTPUT_ICONS, exist_ok=True)

def transform_to_monster_style(img):
    """Transform to dark red monster style"""
    img = img.convert("RGBA")
    pixels = img.load()
    width, height = img.size
    
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            
            if a == 0:
                continue
            
            luminosity = (r * 0.299 + g * 0.587 + b * 0.114)
            is_bright = luminosity > 150
            is_medium = 80 < luminosity <= 150
            
            if is_bright:
                # Bright = dark red glow (blade edge)
                new_r = min(255, int(luminosity * 0.9))
                new_g = int(luminosity * 0.15)
                new_b = int(luminosity * 0.15)
            elif is_medium:
                # Medium = dark crimson
                gray = int(luminosity * 0.5)
                new_r = min(255, gray + 80)
                new_g = int(gray * 0.3)
                new_b = int(gray * 0.3)
            else:
                # Dark = very dark red/black
                new_r = int(luminosity * 0.4) + 25
                new_g = int(luminosity * 0.1)
                new_b = int(luminosity * 0.1)
            
            pixels[x, y] = (new_r, new_g, new_b, a)
    
    return img

# Transform texture
print("Creating Monster Dagger from Mithril base...")
img = Image.open(SOURCE_TEXTURE)
transformed = transform_to_monster_style(img)
transformed.save(os.path.join(OUTPUT_ITEMS, "Monster_Dagger_Texture.png"))
print("✓ Texture created")

# Copy model
shutil.copy2(SOURCE_MODEL, os.path.join(OUTPUT_ITEMS, "Monster_Dagger.blockymodel"))
print("✓ Model copied")

# Transform icon (same style)
icon = Image.open(SOURCE_ICON)
icon_transformed = transform_to_monster_style(icon)
icon_transformed.save(os.path.join(OUTPUT_ICONS, "Monster_Dagger.png"))
print("✓ Icon created")

print("\n✅ Monster Dagger (Mithril base) ready!")
