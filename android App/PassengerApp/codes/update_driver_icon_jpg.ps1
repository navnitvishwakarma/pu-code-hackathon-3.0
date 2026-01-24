Add-Type -AssemblyName System.Drawing

$srcPath = "C:/Users/NAVNIT KUMAR/.gemini/antigravity/brain/c891a529-dbbc-44ac-9907-f4e5d253d91b/uploaded_media_1769222695756.jpg"
$base = "d:\team rocket\android App\DriverApp\app\src\main\res"
$folders = @("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")

if (-not (Test-Path $srcPath)) {
    Write-Host "Error: Source image not found at $srcPath"
    Exit
}

try {
    $img = [System.Drawing.Image]::FromFile($srcPath)
    
    foreach ($f in $folders) {
        $destDir = "$base\$f"
        if (Test-Path $destDir) {
            # Save as PNG
            $destPng = "$destDir\ic_launcher.png"
            $destRound = "$destDir\ic_launcher_round.png"
            
            $img.Save($destPng, [System.Drawing.Imaging.ImageFormat]::Png)
            $img.Save($destRound, [System.Drawing.Imaging.ImageFormat]::Png)
            
            # Clean up potential conflicts
            Remove-Item "$destDir\ic_launcher.webp" -ErrorAction SilentlyContinue
            Remove-Item "$destDir\ic_launcher_round.webp" -ErrorAction SilentlyContinue
            
            Write-Host "Updated $f"
        }
    }
    
    # Clean anydpi
    $anydpi = "$base\mipmap-anydpi-v26"
    if (Test-Path $anydpi) {
        Remove-Item "$anydpi\ic_launcher.xml" -ErrorAction SilentlyContinue
        Remove-Item "$anydpi\ic_launcher_round.xml" -ErrorAction SilentlyContinue
        Write-Host "Cleaned anydpi XMLs"
    }

} catch {
    Write-Host "Error: $($_.Exception.Message)"
} finally {
    if ($img) { $img.Dispose() }
}
