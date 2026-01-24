import sys
from PIL import Image
try:
    source = r"d:\team rocket\android App\PassengerApp\codes\bus-stand.ico"
    img = Image.open(source)
    print(f"Original Dimensions: {img.width}x{img.height}")
except Exception as e:
    print(e)
