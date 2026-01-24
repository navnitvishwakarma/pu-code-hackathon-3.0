$base = "d:\team rocket\android App\DriverApp\app\src\main\res"
$folders = @("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")

foreach ($f in $folders) {
    $path = "$base\$f"
    if (Test-Path $path) {
        # remove webp files
        Remove-Item "$path\ic_launcher.webp" -ErrorAction SilentlyContinue -Force
        Remove-Item "$path\ic_launcher_round.webp" -ErrorAction SilentlyContinue -Force
        Write-Host "Cleaned $f"
    }
}

# Ensure anydpi XMLs are gone too
$anydpi = "$base\mipmap-anydpi-v26"
if (Test-Path $anydpi) {
    Remove-Item "$anydpi\ic_launcher.xml" -ErrorAction SilentlyContinue -Force
    Remove-Item "$anydpi\ic_launcher_round.xml" -ErrorAction SilentlyContinue -Force
    Write-Host "Cleaned anydpi"
}
