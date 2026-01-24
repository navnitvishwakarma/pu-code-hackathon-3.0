import sys
try:
    from PIL import Image
except ImportError:
    print("Pillow not installed")
    sys.exit(1)

try:
    source = r"d:\team rocket\android App\PassengerApp\codes\bus-stand.ico"
    dest = r"d:\team rocket\android App\PassengerApp\app\src\main\res\drawable\ic_bus_stand_custom.png"
    img = Image.open(source)
    # Resize to 70%
    new_width = int(img.width * 0.15)
    new_height = int(img.height * 0.15)
    img = img.resize((new_width, new_height), Image.Resampling.LANCZOS)
    img.save(dest, format="PNG")
    print("Success")
except Exception as e:
    print(f"Error: {e}")
    sys.exit(1)
