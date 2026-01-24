$src = "d:\team rocket\android App\PassengerApp\codes\driver_ic_launcher.png"
$base = "d:\team rocket\android App\DriverApp\app\src\main\res"
$folders = @("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")

foreach ($f in $folders) {
    if (Test-Path "$base\$f") {
        # Copy PNGs
        Copy-Item -Path $src -Destination "$base\$f\ic_launcher.png" -Force
        Copy-Item -Path $src -Destination "$base\$f\ic_launcher_round.png" -Force
        
        # Remove WebP if they exist to prevent duplicates
        Remove-Item "$base\$f\ic_launcher.webp" -ErrorAction SilentlyContinue
        Remove-Item "$base\$f\ic_launcher_round.webp" -ErrorAction SilentlyContinue
        
        Write-Host "Updated $f"
    } else {
        Write-Host "Skipped $f (Not Found)"
    }
}

# Clean anydpi
$anydpi = "$base\mipmap-anydpi-v26"
if (Test-Path $anydpi) {
    Remove-Item "$anydpi\ic_launcher.xml" -ErrorAction SilentlyContinue
    Remove-Item "$anydpi\ic_launcher_round.xml" -ErrorAction SilentlyContinue
    Write-Host "Cleaned anydpi XMLs"
}
