Add-Type -AssemblyName System.Drawing

# Same source image, but target Passenger App
$srcPath = "C:/Users/NAVNIT KUMAR/.gemini/antigravity/brain/c891a529-dbbc-44ac-9907-f4e5d253d91b/uploaded_media_1769222695756.jpg"
$base = "d:\team rocket\android App\PassengerApp\app\src\main\res"
$folders = @("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")

if (-not (Test-Path $srcPath)) {
    Write-Host "Error: Source image not found at $srcPath"
    Exit
}

try {
    $originalImg = [System.Drawing.Image]::FromFile($srcPath)
    $size = $originalImg.Width
    
    # Create a new bitmap with transparency
    $newBitmap = New-Object System.Drawing.Bitmap($size, $size)
    $graph = [System.Drawing.Graphics]::FromImage($newBitmap)
    $graph.Clear([System.Drawing.Color]::Transparent)
    $graph.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic

    # Calculate padded size (75% of original)
    $scale = 0.75
    $newWidth = [int]($size * $scale)
    $newHeight = [int]($size * $scale)
    $x = [int](($size - $newWidth) / 2)
    $y = [int](($size - $newHeight) / 2)

    # Draw the original image centered and scaled
    $graph.DrawImage($originalImg, $x, $y, $newWidth, $newHeight)
    
    foreach ($f in $folders) {
        $destDir = "$base\$f"
        # Create dir if not exists (though usually it does for standard project)
        if (-not (Test-Path $destDir)) { New-Item -ItemType Directory -Force -Path $destDir | Out-Null }

        $destPng = "$destDir\ic_launcher.png"
        $destRound = "$destDir\ic_launcher_round.png"
        
        # Save Padded Version
        $newBitmap.Save($destPng, [System.Drawing.Imaging.ImageFormat]::Png)
        $newBitmap.Save($destRound, [System.Drawing.Imaging.ImageFormat]::Png)
        
        Write-Host "Updated padded icon in $f"
    }
    
    $graph.Dispose()
    $newBitmap.Dispose()
    $originalImg.Dispose()

} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
