$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "==> Building..."
mvn package -DskipTests -q

Write-Host "==> Starting server..."
$server = Start-Process -FilePath "java" `
    -ArgumentList "-jar", "server\target\server-1.0-SNAPSHOT.jar" `
    -PassThru -NoNewWindow

try {
    Start-Sleep -Milliseconds 500
    Write-Host "==> Starting client..."
    & java -jar client\target\client-1.0-SNAPSHOT.jar
} finally {
    Write-Host "==> Stopping server..."
    Stop-Process -Id $server.Id -Force -ErrorAction SilentlyContinue
}
