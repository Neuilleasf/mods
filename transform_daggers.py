"""
Transform dagger textures to dark/red monster style
Like the BetterDaggers image - black/gray base with red accents
"""

from PIL import Image
import os
import shutil

# Paths
SOURCE_DIR = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Assets\Common\Items\Weapons\Dagger"
OUTPUT_DIR = r"c:\Users\Kiwi\Music\Nouveau dossier (14)\Mods.MonsterDaggers\Common\Items\Weapons\Dagger"

# Create output directory
os.makedirs(OUTPUT_DIR, exist_ok=True)

def transform_to_monster_style(img):
    """Transform texture to dark red monster style"""
    img = img.convert("RGBA")
    pixels = img.load()
    width, height = img.size
    
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            
            # Skip transparent pixels
            if a == 0:
                continue
            
            # Calculate luminosity
            luminosity = (r * 0.299 + g * 0.587 + b * 0.114)
            
            # Check if it's a metallic/bright pixel (blade highlight)
            is_bright = luminosity > 150
            is_medium = 80 < luminosity <= 150
            
            if is_bright:
                # Bright pixels become dark red (blade edge glow)
                new_r = min(255, int(luminosity * 0.7))
                new_g = int(luminosity * 0.1)
                new_b = int(luminosity * 0.1)
            elif is_medium:
                # Medium pixels become dark gray with slight red tint
                gray = int(luminosity * 0.4)
                new_r = min(255, gray + 40)
                new_g = int(gray * 0.6)
                new_b = int(gray * 0.6)
            else:
                # Dark pixels stay very dark (near black)
                new_r = int(luminosity * 0.3) + 10
                new_g = int(luminosity * 0.2)
                new_b = int(luminosity * 0.2)
            
            pixels[x, y] = (new_r, new_g, new_b, a)
    
    return img

# Process all dagger textures
textures_processed = 0
for filename in os.listdir(SOURCE_DIR):
    if filename.endswith("_Texture.png"):
        source_path = os.path.join(SOURCE_DIR, filename)
        output_path = os.path.join(OUTPUT_DIR, filename)
        
        # Transform texture
        img = Image.open(source_path)
        transformed = transform_to_monster_style(img)
        transformed.save(output_path)
        
        print(f"✓ Transformed: {filename}")
        textures_processed += 1
    
    elif filename.endswith(".blockymodel"):
        # Copy model files as-is
        source_path = os.path.join(SOURCE_DIR, filename)
        output_path = os.path.join(OUTPUT_DIR, filename)
        shutil.copy2(source_path, output_path)
        print(f"✓ Copied model: {filename}")

print(f"\n✅ Done! Transformed {textures_processed} dagger textures to monster style!")
print(f"Output: {OUTPUT_DIR}")
