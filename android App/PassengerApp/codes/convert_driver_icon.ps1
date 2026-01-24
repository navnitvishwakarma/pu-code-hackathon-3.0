Add-Type -AssemblyName System.Drawing
$source = "d:\team rocket\android App\PassengerApp\codes\driver app icon.ico"
$dest = "d:\team rocket\android App\PassengerApp\codes\driver_ic_launcher.png"

try {
    if (Test-Path $source) {
        $icon = [System.Drawing.Icon]::new($source)
        $bitmap = $icon.ToBitmap()
        $bitmap.Save($dest, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "Success: Converted to $dest"
    } else {
        Write-Host "Error: Source file not found at $source"
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
