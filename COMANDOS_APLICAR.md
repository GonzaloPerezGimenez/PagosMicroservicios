# Cómo aplicar estos cambios

Copia el contenido de esta carpeta encima de la raíz de tu repositorio `PagosMicroservicios`.

Después ejecuta:

```bash
git rm --cached .env || true
git rm -r --cached .vscode Gateway_Service/target User_Services/target Payments_Services/target || true
git add README.md .env.example .gitignore docker-compose.yml Gateway_Service/src/main/resources/application.properties User_Services/src/main/resources/application.properties Payments_Services/src/main/resources/application.properties
git commit -m "docs: prepare project for recruiters and external configuration"
git push
```

IMPORTANTE: rota la contraseña publicada antes de volver a compartir el repo.
