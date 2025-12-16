#!/bin/bash

# Iniciar Payara en background
${PAYARA_DIR}/bin/asadmin start-domain --verbose=false &

# Esperar que esté listo
sleep 15

# Desplegar aplicación
echo "Desplegando Cliente Web..."
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile deploy --force=true ${DEPLOY_DIR}/*.war

echo "=== Cliente Web desplegado exitosamente ==="

# Mantener contenedor activo
${PAYARA_DIR}/bin/asadmin stop-domain
exec ${PAYARA_DIR}/bin/asadmin start-domain --verbose --watchdog
