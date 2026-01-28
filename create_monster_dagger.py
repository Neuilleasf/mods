"""
Create ONE Monster Dagger texture from Iron dagger
Dark/red monster style
"""

from PIL import Image
import os

# Source: Iron dagger
SOURCE_TEXTURE = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Items\Weapons\Dagger\Iron_Texture.png"
SOURCE_MODEL = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Items\Weapons\Dagger\Iron.blockymodel"

# Output
OUTPUT_DIR = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Items"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def transform_to_monster_style(img):
    """Transform texture to dark red monster style"""
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
                # Bright = dark red glow
                new_r = min(255, int(luminosity * 0.8))
                new_g = int(luminosity * 0.1)
                new_b = int(luminosity * 0.1)
            elif is_medium:
                # Medium = dark gray with red tint
                gray = int(luminosity * 0.4)
                new_r = min(255, gray + 50)
                new_g = int(gray * 0.5)
                new_b = int(gray * 0.5)
            else:
                # Dark = near black
                new_r = int(luminosity * 0.3) + 15
                new_g = int(luminosity * 0.15)
                new_b = int(luminosity * 0.15)
            
            pixels[x, y] = (new_r, new_g, new_b, a)
    
    return img

# Transform texture
img = Image.open(SOURCE_TEXTURE)
transformed = transform_to_monster_style(img)
transformed.save(os.path.join(OUTPUT_DIR, "Monster_Dagger_Texture.png"))
print("✓ Created: Monster_Dagger_Texture.png")

# Copy model
import shutil
shutil.copy2(SOURCE_MODEL, os.path.join(OUTPUT_DIR, "Monster_Dagger.blockymodel"))
print("✓ Copied: Monster_Dagger.blockymodel")

print("\n✅ Monster Dagger created!")
